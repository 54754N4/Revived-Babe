package bot.model;

import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import audio.TrackScheduler;
import audio.handlers.MirroredSendHandler;
import audio.track.handlers.TrackLoadHandler;
import audio.track.handlers.TrackLoadHandler.StatusUpdater;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public abstract class MusicBot extends UserBot {
	public static int MAX_LOADER_THREAD_POOL = 20, 
			BUFFER_DURATION = 30*1000, 		// 30s
			CLEANUP_THRESHOLD = 60*1000;	// 1 min
	private final Map<Long, AudioPlayerManager> playerManagers;
	private final Map<Long, AudioManager> managers;
	private final Map<Long, AudioPlayer> players;
	private final Map<Long, AudioSendHandler> senders;
	private final Map<Long, TrackScheduler> schedulers;
	
	
	public MusicBot(Bot bot) {
		super(bot);
		playerManagers = new HashMap<>();
		managers = new HashMap<>();
		players = new HashMap<>();
		senders = new HashMap<>();
		schedulers = new HashMap<>();
	}
	
	protected AudioSendHandler getAudioSendHandler() {
		return null;
	}

	protected AudioReceiveHandler getAudioReceiveHandler() {
		return null;
	}
	
	private MusicBot setupAudio(Guild guild) {	// pays cost once, further calls cost 0
		long id = guild.getIdLong();
		AudioManager am = guild.getAudioManager();
		if (playerManagers.get(id) == null)
			playerManagers.put(id, initializeManager());
		if (players.get(id) == null)
			players.put(id, playerManagers.get(id).createPlayer());
		AudioPlayer player = players.get(id);
		if (managers.get(id) == null) {
			managers.put(id, am);
			AudioSendHandler handler = getAudioSendHandler(); 
			if (handler == null && senders.get(id) == null)
				senders.put(id, handler = new MirroredSendHandler(player));
			am.setSendingHandler(handler);
			am.setReceivingHandler(getAudioReceiveHandler());
			am.setSelfDeafened(true);		// saves resources
		}
		if (schedulers.get(id) == null) {
			schedulers.put(id, new TrackScheduler(player));
			player.addListener(schedulers.get(id));
		}
		return this;
	}
	
	private final AudioPlayerManager initializeManager() {
		AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
		playerManager.setItemLoaderThreadPoolSize(MAX_LOADER_THREAD_POOL);
		playerManager.setPlayerCleanupThreshold(CLEANUP_THRESHOLD);
		playerManager.setFrameBufferDuration(BUFFER_DURATION);
		playerManager.setUseSeekGhosting(true); 	// buffers current track until new seek position is available
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);
		return playerManager;
	}
	
	@Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		if (isSelf(event)) handleVoiceChannelUpdate(event);
    }
    
    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
    	if (isSelf(event)) handleVoiceChannelUpdate(event);
    }
    
    private void handleVoiceChannelUpdate(GuildVoiceUpdateEvent event) {
    	String id = event.getChannelJoined().getId();
    	logger.info("Updated {} last channel id in music state to {}", name, id);
	}
	
	/* Convenience accessors */
	
	public boolean isDeafened(Guild guild) {
		return getManager(guild).isSelfDeafened();
	}
	
	public boolean isMuted(Guild guild) {
		return getManager(guild).isSelfMuted();
	}
	
	public boolean isConnected(Guild guild) {
		return getManager(guild).isConnected();
	}

	public AudioPlayer getPlayer(Guild guild) {
		return players.get(guild.getIdLong());
	}
	
	public AudioManager getManager(Guild guild) {
		return managers.get(guild.getIdLong());
	}
	
	public AudioSendHandler getAudioSendHandler(Guild guild) {
		return senders.get(guild.getIdLong());
	}
	
	public TrackScheduler getScheduler(Guild guild) {
		return schedulers.get(guild.getIdLong());
	}
	
	public AudioPlayer getPlayer(long guildID) {
		return players.get(guildID);
	}
	
	public AudioManager getManager(long guildID) {
		return managers.get(guildID);
	}
	
	public AudioSendHandler getAudioSendHandler(long guildID) {
		return senders.get(guildID);
	}
	
	public TrackScheduler getScheduler(long guildID) {
		return schedulers.get(guildID);
	}
	
	/* Music methods */
	
	public void kill() {
		for (AudioPlayer player : players.values())
			player.destroy();
		for (AudioManager manager : managers.values())
			manager.closeAudioConnection();
		// Allow GC
		managers.clear();
		senders.clear();
		schedulers.clear();
		players.clear();
	}
	
	public boolean managerExists(Guild guild) {
		return managers.containsKey(guild.getIdLong());
	}
	
	public MusicBot toggleDeafen(Guild guild) {
		AudioManager manager = getManager(guild);
		if (manager != null)
			manager.setSelfDeafened(!manager.isSelfDeafened());
		return this;
	}
	
	public MusicBot toggleMute(Guild guild) {
		AudioManager manager = getManager(guild);
		if (manager != null)
			manager.setSelfMuted(!manager.isSelfMuted());
		return this;
	}
	
	public MusicBot connectTo(VoiceChannel channel) {
		setupAudio(channel.getGuild());
		if (managerExists(channel.getGuild()))
			getManager(channel.getGuild()).openAudioConnection(channel);
		return this;
	}
	
	public MusicBot leave(Guild guild) {
		setupAudio(guild);
		if (managerExists(guild))
			getManager(guild).closeAudioConnection();
		return this;
	}
	
	public void play(Guild guild, String identifier, AudioLoadResultHandler handler) {
		setupAudio(guild);
		playerManagers.get(guild.getIdLong()).loadItem(identifier, handler);
	}
	
	public void play(Guild guild, String identifier, boolean top, boolean next, int count, boolean playlist, StatusUpdater callback) {
		long id = guild.getIdLong();
		setupAudio(guild);
		playerManagers.get(id)
			.loadItem(identifier, new TrackLoadHandler(top, next, count, playlist, schedulers.get(id), callback));
	}
	
	public void playPlaylist(Guild guild, String identifier, StatusUpdater callback) {
		play(guild, identifier, false, false, Integer.MAX_VALUE, true, callback);
	}
	
	public void playPlaylist(Guild guild, String identifier) {
		playPlaylist(guild, identifier, null);
	}
	
	public void playNext(Guild guild, String identifier, int count, StatusUpdater callback) {
		play(guild, identifier, false, true, count, false, callback);
	}
	
	public void playNext(Guild guild, String identifier, int count) {
		play(guild, identifier, false, true, count, false, null);
	}
	
	public void playNext(Guild guild, String identifier, StatusUpdater callback) {
		playNext(guild, identifier, 1, callback);
	}
	
	public void playNext(Guild guild, String identifier) {
		playNext(guild, identifier, 1, null);
	}
	
	public void playTop(Guild guild, String identifier, int count, StatusUpdater callback) {
		play(guild, identifier, true, false, count, false, callback);
	}
	
	public void playTop(Guild guild, String identifier, int count) {
		play(guild, identifier, true, false, count, false, null);
	}
	
	public void playTop(Guild guild, String identifier, StatusUpdater callback) {
		playTop(guild, identifier, 1, callback);
	}
	
	public void playTop(Guild guild, String identifier) {
		playTop(guild, identifier, null);
	}
}