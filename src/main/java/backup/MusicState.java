package backup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.DecodedTrackHolder;

import audio.CircularDeque;
import audio.MusicController;
import audio.TrackScheduler;
import bot.hierarchy.MusicBot;
import database.DBManager;
import database.Query;
import database.TableManager;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public abstract class MusicState {
	private static final Logger logger = LoggerFactory.getLogger(MusicState.class);
	private static final String 
			LAST_CHANNEL = "lastVoiceChannel", CURRENT_PLAYLIST = "currentPlaylist",
			POSITION = "position", VOLUME = "lastVolume", CURRENT = "current",
			PAUSED = "wasPaused", PLAYLIST = "playlist";
	
	/* Static convenience methods */
	
	public static final void backup(MusicBot bot) {
		new Backup(bot).run();
	}
	
	public static final void restore(MusicBot bot) {
		new Restore(bot).run();
	}
	
	public static final void clear(MusicBot bot, long guildID) throws SQLException {
		getTable(bot, guildID).clear();
	}
	
	private static final String tableName(MusicBot bot, long guildID) {
		return "Backup" + bot.getBotName() + guildID;
	}
	
	private static final TableManager getTable(MusicBot bot, long guildID) throws SQLException {
		return DBManager.INSTANCE.manage(tableName(bot, guildID));
	}

	/* Interface that both backup and restore classes implement */
	
	private static interface Handler extends Runnable {
		Map<Long, TableManager> getGuildTables();
		void handlePlaylists(long guildID, TableManager table);
		void handleVoiceChannel(long guildID, TableManager table);
		void handleVolumeAndPause(long guildID, TableManager table);
		void handleSongAndPosition(long guildID, TableManager table);
		
		default boolean preExecute(long guildID, TableManager table) throws Exception {
			return true;
		}
		
		default void postExecute(long guildID, TableManager table) throws Exception {}
		
		@Override
		default void run() {
			boolean success;
			for (Entry<Long, TableManager> entry : getGuildTables().entrySet()) {
				long guildID = entry.getKey();
				TableManager table = entry.getValue();
				try {
					success = preExecute(guildID, table);
					if (!success)
						continue;
					handlePlaylists(guildID, table);
					handleVoiceChannel(guildID, table);
					handleSongAndPosition(guildID, table);
					handleVolumeAndPause(guildID, table);
					postExecute(guildID, table);
				} catch (Exception e) {
					logger.error("Error with guild " + guildID, e);
					continue;
				}
			}
		}
		
		/* Convenience method */
		
		default Map<Long, TableManager> createTableManagers(MusicBot bot, Collection<Long> guildIDs) {
			Map<Long, TableManager> tables = new HashMap<>();
			for (long id : guildIDs) {
				try {
					tables.put(id, getTable(bot, id));
				} catch (Exception e) {
					logger.error("Couldn't get backup table for bot "+bot+" and guild id "+id, e);
				}
			}
			return tables;
		}
	}
	
	/* Backup class */
	
	public static class Backup implements Handler {
		private final MusicBot bot;
		private final Map<Long, MusicController> map;
		
		public Backup(MusicBot bot) {
			this.bot = bot;
			map = bot.getControllers();
		}

		@Override
		public Map<Long, TableManager> getGuildTables() {
			return createTableManagers(bot, map.keySet());
		}

		@Override
		public boolean preExecute(long guildID, TableManager table) throws SQLException {
			logger.info("Starting backup for bot {} and guild {}", bot, guildID);
			table.reset();
			return true;
		}
		
		@Override
		public void handlePlaylists(long guildID, TableManager table) {
			logger.info("Backing up playlists for guild {}", guildID);
			MusicController controller = map.get(guildID);
			AudioPlayerManager manager = controller.getPlayerManager();
			TrackScheduler scheduler = controller.getScheduler();
			Map<String, CircularDeque> queues = scheduler.getPlaylists();
			for (Entry<String, CircularDeque> entry : queues.entrySet()) {
				CircularDeque queue = entry.getValue();
				if (queue.size() == 0)
					continue;
				String name = entry.getKey();
				if (name.equals(""))
					continue;
				String tracks = handlePlaylist(guildID, manager, queue);
				try {
					table.insertOrUpdate(PLAYLIST+name, tracks);
				} catch (SQLException e) {
					logger.error("Could not backup queue named : "+name, e);
				}
			}
			try {
				table.insertOrUpdate(CURRENT_PLAYLIST, scheduler.getCurrentPlaylist());
			} catch (SQLException e) {
				logger.error("Could not backup current playlist of guild : "+guildID, e);
			}
		}
		
		private String handlePlaylist(long guildID, AudioPlayerManager manager, CircularDeque queue) {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				MessageOutput output = new MessageOutput(baos);
				for (AudioTrack track : queue) {
					try {
						manager.encodeTrack(output, track);
					} catch (IOException e) {
						logger.error("Could not backup track : "+track, e);
					}
				}
				try {
					output.finish();
				} catch (IOException e) {
					logger.error("Could not finish MessageOutput", e);
				}
				byte[] bytes = baos.toByteArray();
				String playlist = Base64.getEncoder().encodeToString(bytes);
				return playlist;
			} catch (IOException e) {
				logger.error("Couldn't close ByteArrayOutputStream while backup up queue for guild "+guildID, e);
				return null;
			}
		}

		@Override
		public void handleVoiceChannel(long guildID, TableManager table) {
			logger.info("Backing up voice channel for guild {}", guildID);
			AudioManager manager = bot.getManager(guildID);
			if (manager == null) 
				return;
			AudioChannel channel = manager.getConnectedChannel();
			if (channel == null)
				return;
			logger.info("Setting last voice channel to {}", channel.getId());
			try {
				table.insertOrUpdate(LAST_CHANNEL, channel.getIdLong());
			} catch (SQLException e) {
				logger.error("Could not backup voice channel", e);
			}
		}

		@Override
		public void handleSongAndPosition(long guildID, TableManager table) {
			logger.info("Backing up song and track position for guild {}", guildID);
			CircularDeque queue = map.get(guildID).getScheduler().getQueue();
			int current = queue.getCurrent();
			if (current == CircularDeque.UNINITIALISED)
				return;
			try {
				table.insertOrUpdate(CURRENT, current);
				table.insertOrUpdate(POSITION, queue.get(current).getPosition());
			} catch (SQLException e) {
				logger.error("Could not backup current track index + position", e);
			}
		}
		
		@Override
		public void handleVolumeAndPause(long guildID, TableManager table) {
			logger.info("Backing up volume and pause state for guild {}", guildID);
			AudioPlayer player = bot.getPlayer(guildID);
			try {
				table.insertOrUpdate(VOLUME, player.getVolume());
				table.insertOrUpdate(PAUSED, player.isPaused());
			} catch (SQLException e) {
				logger.error("Could not backup volume and pause state", e);
			}
		}
	}
	
	/* Restore class */
	
	public static class Restore implements Handler {
		private final MusicBot bot;
		private Guild guild;
		
		public Restore(MusicBot bot) {
			this.bot = bot;
		}

		@Override
		public Map<Long, TableManager> getGuildTables() {
			String prefix = "Backup" + bot.getBotName();
			List<Long> ids;
			try {
				ids = Query.getGuildTables(prefix + "%")
					.stream()
					.map(s -> s.substring(prefix.length()))
					.map(Long::parseLong)
					.collect(Collectors.toList());
			} catch (Exception e) {
				return new HashMap<>();
			}
			return createTableManagers(bot, ids);
		}

		@Override
		public boolean preExecute(long guildID, TableManager table) throws SQLException {
			logger.info("Started restoring for bot {} and guild {}", bot, guild);
			guild = bot.getJDA().getGuildById(guildID);
			if (guild == null) 
				return false;
			logger.info("Setting up audio for guild {}", guild);
			bot.setupAudio(guildID);
			return true;
		}
		
		@Override
		public void handlePlaylists(long guildID, TableManager table) {
			logger.info("Restoring playlists for guild {}", guild);
			Map<String, String> playlists;
			try {
				playlists = table.select(PLAYLIST+"%");
			} catch (SQLException e) {
				logger.error("Couldn't retrieve playlists from db", e);
				return;
			}
			AudioPlayerManager manager = bot.getPlayerManager(guild);
			TrackScheduler scheduler = bot.getScheduler(guildID);
			Map<String, CircularDeque> queues = scheduler.getPlaylists();
			for (Entry<String, String> entry : playlists.entrySet()) {
				String name = entry.getKey().substring(PLAYLIST.length());
				if (name.equals(""))
					continue;
				String tracks = entry.getValue();
				if (!queues.containsKey(name))
					scheduler.createPlaylist(name);
				handlePlaylist(manager, queues.get(name), tracks);
			}
			try {
				String playlist = table.retrieve(CURRENT_PLAYLIST, "");
				if (playlist.equals(""))
					return;
				scheduler.swapPlaylist(playlist);
			} catch (SQLException e) {
				logger.error("Couldn't retrieve current playlist", e);
			}
		}
		
		private void handlePlaylist(AudioPlayerManager manager, CircularDeque queue, String tracks) {
			byte[] bytes = Base64.getDecoder().decode(tracks);
			try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
				MessageInput input = new MessageInput(bais);
				DecodedTrackHolder holder = null;
				while (true) {
					try {
						holder = manager.decodeTrack(input);
					} catch (IOException e) {
						logger.error("Could not restore a track", e);
					}
					if (holder == null)
						break;
					queue.add(holder.decodedTrack);
				};
			} catch (IOException e) {
				logger.error("Couldn't close ByteArrayInputStream", e);
			}
		}

		@Override
		public void handleVoiceChannel(long guildID, TableManager table) {
			logger.info("Restoring voice channel for guild {}", guild);
			try {
				long channelID = table.retrieveLong(LAST_CHANNEL, -1);
				if (channelID == -1)
					return;
				VoiceChannel channel = bot.getJDA().getVoiceChannelById(channelID);
				if (channel == null)
					return;
				bot.connect(channel);
			} catch (NumberFormatException | SQLException e) {
				logger.error("Could not retrieve or connect to last channel ID", e);
			}
		}

		@Override
		public void handleSongAndPosition(long guildID, TableManager table) {
			logger.info("Restoring song and track position for guild {}", guild);
			Guild guild = bot.getJDA().getGuildById(guildID);
			try {
				int current = table.retrieveInt(CURRENT, -1);
				if (current == -1)
					return;
				bot.play(guild, current);
			} catch (Exception e) {
				logger.error("Couldn't retrieve current song index", e);
			}
			try {
				long position = table.retrieveLong(POSITION, -1);
				if (position == -1 || bot.getPlayer(guild).getPlayingTrack() == null)
					return;
				logger.info("Seeking for {} in song for guild {}", bot, guild);
				bot.seekTo(guild, position);
			} catch (Exception e) {
				logger.error("Could not retrieve last song's position", e);
			}
		}
		
		@Override
		public void handleVolumeAndPause(long guildID, TableManager table) {
			logger.info("Restoring volume and pause state for guild {}", guild);
			Guild guild = bot.getJDA().getGuildById(guildID);
			if (guild == null)
				return;
			try {
				int volume = table.retrieveInt(VOLUME, -1);
				if (volume == -1)
					return;
				bot.setVolume(guild, volume);
			} catch (NumberFormatException | SQLException e) {
				logger.error("Could not retrieve last volume", e);
			}
			try {
				boolean paused = table.retrieveBool(PAUSED, false);
				if (paused)
					bot.pause(guild);
			} catch (NumberFormatException | SQLException e) {
				logger.error("Could not retrieve last pause state", e);
			}
		}
	}
}
