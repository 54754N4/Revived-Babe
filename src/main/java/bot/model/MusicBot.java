package bot.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.CircularDeque;
import audio.TrackScheduler;
import audio.handlers.MirroredSendHandler;
import audio.track.handlers.TrackLoadHandler;
import audio.track.handlers.TrackLoadHandler.StatusUpdater;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * A single bot can run in multiple guilds/servers, so we need to keep track of 
 * all the different audio managers, players etc which are unique to each one. 
 * This class does that while also offering convenience methods for simple 
 * music/voice handling from commands.
 */
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
	
	public boolean isConnected(Guild guild) {
		setupAudio(guild);
		return getManager(guild).isConnected();
	}

	public CircularDeque getPlaylist(Guild guild) {
		setupAudio(guild);
		return getScheduler(guild).getQueue();
	}
	
	public AudioPlayer getPlayer(Guild guild) {
		setupAudio(guild);
		return players.get(guild.getIdLong());
	}
	
	public AudioManager getManager(Guild guild) {
		setupAudio(guild);
		return managers.get(guild.getIdLong());
	}
	
	public AudioPlayerManager getPlayerManager(Guild guild) {
		setupAudio(guild);
		return playerManagers.get(guild.getIdLong());
	}
	
	public AudioSendHandler getAudioSendHandler(Guild guild) {
		setupAudio(guild);
		return senders.get(guild.getIdLong());
	}
	
	public TrackScheduler getScheduler(Guild guild) {
		setupAudio(guild);
		return schedulers.get(guild.getIdLong());
	}
	
	public AudioPlayer getPlayer(long guildID) {
		return players.get(guildID);
	}
	
	public AudioManager getManager(long guildID) {
		return managers.get(guildID);
	}
	
	public AudioPlayerManager getPlayerManager(long guildID) {
		return playerManagers.get(guildID);
	}
	
	public AudioSendHandler getAudioSendHandler(long guildID) {
		return senders.get(guildID);
	}
	
	public TrackScheduler getScheduler(long guildID) {
		return schedulers.get(guildID);
	}
	
	public CircularDeque getPlaylist(long guildID) {
		return getScheduler(guildID).getQueue();
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
	
	/* Joining & leaving */
	
	public MusicBot connectTo(VoiceChannel channel) {
		setupAudio(channel.getGuild());
		if (managerExists(channel.getGuild()))
			getManager(channel.getGuild()).openAudioConnection(channel);
		return this;
	}
	
	public MusicBot leaveVoice(Guild guild) {
		setupAudio(guild);
		if (managerExists(guild))
			getManager(guild).closeAudioConnection();
		return this;
	}
	
	/* Deafening */
	
	public MusicBot deafen(Guild guild) {
		setupAudio(guild);
		getManager(guild).setSelfDeafened(true);
		return this;
	}
	
	public MusicBot undeafen(Guild guild) {
		setupAudio(guild);
		getManager(guild).setSelfDeafened(false);
		return this;
	}
	
	public MusicBot toggleDeafen(Guild guild) {
		setupAudio(guild);
		AudioManager manager = getManager(guild);
		manager.setSelfDeafened(!manager.isSelfDeafened());
		return this;
	}

	public boolean isDeafened(Guild guild) {
		setupAudio(guild);
		return getManager(guild).isSelfDeafened();
	}
	
	public MusicBot deafen(Member member) {
		Guild guild = member.getGuild();
		setupAudio(guild);
		member.deafen(true).queue();
		return this;
	}
	
	public MusicBot undeafen(Member member) {
		Guild guild = member.getGuild();
		setupAudio(guild);
		member.deafen(false).queue();
		return this;
	}
	
	public MusicBot toggleDeafen(Member member) {
		Guild guild = member.getGuild();
		setupAudio(guild);
		member.deafen(!member.getVoiceState().isDeafened());
		return this;
	}
	
	public boolean isDeafened(Member member) {
		setupAudio(member.getGuild());
		return member.getVoiceState().isDeafened();
	}
	
	/* Muting */
	
	public MusicBot mute(Guild guild) {
		setupAudio(guild);
		getManager(guild).setSelfMuted(true);
		return this;
	}
	
	public MusicBot unmute(Guild guild) {
		setupAudio(guild);
		getManager(guild).setSelfMuted(false);
		return this;
	}
	
	public MusicBot toggleMute(Guild guild) {
		setupAudio(guild);
		AudioManager manager = getManager(guild);
		manager.setSelfMuted(!manager.isSelfMuted());
		return this;
	}
	
	public boolean isMuted(Guild guild) {
		setupAudio(guild);
		return getManager(guild).isSelfMuted();
	}
	
	public MusicBot mute(Member member) {
		setupAudio(member.getGuild());
		member.mute(true).queue();
		return this;
	}
	
	public MusicBot unmute(Member member) {
		setupAudio(member.getGuild());
		member.mute(false).queue();
		return this;
	}
	
	public MusicBot toggleMute(Member member) {
		setupAudio(member.getGuild());
		member.mute(!member.getVoiceState().isMuted()).queue();
		return this;
	}
	
	public boolean isMuted(Member member) {
		setupAudio(member.getGuild());
		return member.getVoiceState().isMuted();
	}
	
	/* Pausing */
	
	public MusicBot pause(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).setPause(true);
		return this;
	}
	
	public MusicBot unpause(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).setPause(false);
		return this;
	}
	
	public MusicBot togglePause(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).togglePause();
		return this;
	}
	
	public boolean isPaused(Guild guild) {
		setupAudio(guild);
		return getScheduler(guild).isPaused();
	}
	
	/* Repeating */
	
	public MusicBot repeat(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).setRepeating(true);
		return this;
	}
	
	public MusicBot stopRepeat(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).setRepeating(false);
		return this;
	}
	
	public MusicBot toggleRepeating(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).toggleRepeating();
		return this;
	}
	
	public boolean isRepeating(Guild guild) {
		setupAudio(guild);
		return getScheduler(guild).isRepeating();
	}
	
	/* Looping */
	
	public MusicBot looping(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).setLooping(true);
		return this;
	}
	
	public MusicBot stopLooping(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).setLooping(false);
		return this;
	}
	
	public MusicBot toggleLooping(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).toggleLooping();
		return this;
	}
	
	public boolean isLooping(Guild guild) {
		setupAudio(guild);
		return getScheduler(guild).isLooping();
	}
	
	/* Volume */
	
	public int getVolume(Guild guild) {
		setupAudio(guild);
		AudioPlayer player = getPlayer(guild);
		return player != null ? player.getVolume() : -1;	
	}
	
	public int setVolume(Guild guild, int volume) {
		setupAudio(guild);
		TrackScheduler scheduler = getScheduler(guild);
		if (scheduler != null && volume >= 0 && volume <= 200)
			scheduler.setVolume(volume);
		return volume;
	}
	
	public int increaseVolume(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).increaseVolume();
		return getPlayer(guild).getVolume();
	}
	
	public int decreaseVolume(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).decreaseVolume();
		return getPlayer(guild).getVolume();
	}
	
	/* Track handling */
	
	public MusicBot stop(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).stop();
		return this;
	}
	
	public MusicBot seekTo(Guild guild, long position) {
		setupAudio(guild);
		getScheduler(guild).seekTo(position);
		return this;
	}
	
	public MusicBot clear(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).clear();
		return this;
	}
	
	public MusicBot next(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).nextTrack();
		return this;
	}
	
	public MusicBot previous(Guild guild) {
		setupAudio(guild);
		getScheduler(guild).previousTrack();
		return this;
	}
	
	public AudioTrack getCurrentTrack(Guild guild) {
		setupAudio(guild);
		return getPlayer(guild).getPlayingTrack();
	}
	
	/* Play */
	
	public int play(Guild guild, int index) {
		setupAudio(guild);
		getScheduler(guild).play(index);
		return index;
	}
	
	public Future<Void> play(Guild guild, String identifier, AudioLoadResultHandler handler) {
		setupAudio(guild);
		return playerManagers.get(guild.getIdLong()).loadItem(identifier, handler);
	}
	
	public Future<Void> play(Guild guild, String identifier, boolean top, boolean next, int count, boolean playlist, StatusUpdater callback) {
		long id = guild.getIdLong();
		setupAudio(guild);
		return playerManagers.get(id)
			.loadItemOrdered(
					getPlayerManager(guild), 
					identifier, 
					new TrackLoadHandler(top, next, count, playlist, schedulers.get(id), callback));
	}
	
	public Future<Void> playPlaylist(Guild guild, String identifier, StatusUpdater callback) {
		return play(guild, identifier, false, false, Integer.MAX_VALUE, true, callback);
	}
	
	public Future<Void> playPlaylist(Guild guild, String identifier) {
		return playPlaylist(guild, identifier, null);
	}
	
	public Future<Void> playNext(Guild guild, String identifier, int count, StatusUpdater callback) {
		return play(guild, identifier, false, true, count, false, callback);
	}
	
	public Future<Void> playNext(Guild guild, String identifier, int count) {
		return play(guild, identifier, false, true, count, false, null);
	}
	
	public Future<Void> playNext(Guild guild, String identifier, StatusUpdater callback) {
		return playNext(guild, identifier, 1, callback);
	}
	
	public Future<Void> playNext(Guild guild, String identifier) {
		return playNext(guild, identifier, 1, null);
	}
	
	public Future<Void> playTop(Guild guild, String identifier, int count, StatusUpdater callback) {
		return play(guild, identifier, true, false, count, false, callback);
	}
	
	public Future<Void> playTop(Guild guild, String identifier, int count) {
		return play(guild, identifier, true, false, count, false, null);
	}
	
	public Future<Void> playTop(Guild guild, String identifier, StatusUpdater callback) {
		return playTop(guild, identifier, 1, callback);
	}
	
	public Future<Void> playTop(Guild guild, String identifier) {
		return playTop(guild, identifier, null);
	}
}