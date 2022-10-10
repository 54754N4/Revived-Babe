package bot.hierarchy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.CircularDeque;
import audio.MusicController;
import audio.TrackScheduler;
import audio.track.handlers.TrackLoadHandler;
import backup.MusicState;
import commands.model.ThreadSleep;
import lambda.StatusUpdater;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * A single bot can run in multiple guilds/servers, so we need to keep track of 
 * all the different audio managers, players etc which are unique to each one. 
 * This class does that while also offering convenience methods for simple 
 * music/voice handling from commands.
 */
public abstract class MusicBot extends UserBot {
	private static final long DEFAULT_TIMEOUT = 5*1000;
	private final Map<Long, MusicController> controllers;
	
	public MusicBot(Bot bot) {
		super(bot);
		controllers = new ConcurrentHashMap<>();
	}
	
	public Map<Long, MusicController> getControllers() {
		return controllers;
	}
	
	private MusicController controller(Guild guild) {
		return controllers.get(guild.getIdLong());
	}
	
	private MusicController controller(long id) {
		return controllers.get(id);
	}
	
	/* Child classes can override defaults */ 
	
	public AudioSendHandler getAudioSendHandler() {
		return null;
	}

	public AudioReceiveHandler getAudioReceiveHandler() {
		return null;
	}
	
	public MusicBot setupAudio(long id) {
		return setupAudio(jda.getGuildById(id));
	}
	
	// pays cost once, further calls cost 0
	public MusicBot setupAudio(Guild guild) {
		long id = guild.getIdLong();
		if (guild == null || controllers.get(id) != null)
			return this;
		controllers.put(id, new MusicController(this, guild));
		return this;
	}
	
	@Override
	protected void preKill(boolean now) throws Exception {
		safeRunChain(
			() -> MusicState.backup(this),
			() -> controllers.values()
				.stream()
				.map(MusicController::getPlayer)
				.forEach(AudioPlayer::destroy),
			() -> controllers.values()
				.stream()
				.map(MusicController::getManager)
				.forEach(AudioManager::closeAudioConnection),
			controllers::clear);
		super.preKill(now);
	}
	
	@Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		if (isSelf(event)) handleVoiceChannelUpdate(event);
    }
    
    private void handleVoiceChannelUpdate(GuildVoiceUpdateEvent event) {
    	String id = event.getChannelJoined().getId();
    	logger.info("Updated {} last channel id in music state to {}", name, id);
	}
	
	/* Convenience accessors */
	
	public boolean isConnected(Guild guild) {
		return setupAudio(guild)
				.controller(guild)
				.getManager()
				.isConnected();
	}

	public CircularDeque getPlaylist(Guild guild) {
		return setupAudio(guild)
				.controller(guild)
				.getScheduler()
				.getQueue();
	}
	
	public AudioPlayer getPlayer(Guild guild) {
		return setupAudio(guild)
				.controller(guild)
				.getPlayer();
	}
	
	public AudioManager getManager(Guild guild) {
		return setupAudio(guild)
				.controller(guild)
				.getManager();
	}
	
	public AudioPlayerManager getPlayerManager(Guild guild) {
		return setupAudio(guild)
				.controller(guild)
				.getPlayerManager();
	}
	
	public AudioSendHandler getAudioSendHandler(Guild guild) {
		return setupAudio(guild)
				.controller(guild)
				.getSender();
	}
	
	public TrackScheduler getScheduler(Guild guild) {
		return setupAudio(guild)
				.controller(guild)
				.getScheduler();
	}
	
	public AudioPlayer getPlayer(long guildID) {
		return setupAudio(guildID)
				.controller(guildID)
				.getPlayer();
	}
	
	public AudioManager getManager(long guildID) {
		return setupAudio(guildID)
				.controller(guildID)
				.getManager();
	}
	
	public AudioPlayerManager getPlayerManager(long guildID) {
		return setupAudio(guildID)
				.controller(guildID)
				.getPlayerManager();
	}
	
	public AudioSendHandler getAudioSendHandler(long guildID) {
		return setupAudio(guildID)
				.controller(guildID)
				.getSender();
	}
	
	public TrackScheduler getScheduler(long guildID) {
		return setupAudio(guildID)
				.controller(guildID)
				.getScheduler();
	}
	
	public CircularDeque getPlaylist(long guildID) {
		return setupAudio(guildID)
				.controller(guildID)
				.getScheduler()
				.getQueue();
	}
	
	/* Music methods */
	
	public MusicBot waitForTrack(Guild guild) throws Exception {
		return waitForTrack(guild, DEFAULT_TIMEOUT);
	}
	
	public MusicBot waitForTrack(Guild guild, long timeout) throws Exception {
		final AudioPlayer player = getPlayer(guild);
		ThreadSleep.nonBlocking(timeout, () -> player.getPlayingTrack() == null).call();
		return this;
	}
	
	public MusicBot waitForTracks(Guild guild, long timeout, int tracks) throws Exception {
		final CircularDeque queue = getPlaylist(guild);
		ThreadSleep.nonBlocking(timeout, () -> queue.size() != tracks).call();
		return this;
	}

	public MusicBot waitForAllTracks(Guild guild, int tracks) throws Exception {
		final CircularDeque queue = getPlaylist(guild);
		ThreadSleep.blocking(() -> queue.size() != tracks).call();
		return this;
	}
	
	/* Joining & leaving */
	
	public MusicBot connect(AudioChannel channel) {
		setupAudio(channel.getGuild())
			.getManager(channel.getGuild())
			.openAudioConnection(channel);
		return this;
	}
	
	public AudioChannel disconnect(Guild guild) {
		AudioManager manager = setupAudio(guild)
			.getManager(guild);
		AudioChannel channel = manager.getConnectedChannel();
		manager.closeAudioConnection();
		return channel;
	}
	
	/* Deafening */
	
	public MusicBot deafen(Guild guild) {
		setupAudio(guild)
			.getManager(guild)
			.setSelfDeafened(true);
		return this;
	}
	
	public MusicBot undeafen(Guild guild) {
		setupAudio(guild)
			.getManager(guild)
			.setSelfDeafened(false);
		return this;
	}
	
	public MusicBot toggleDeafen(Guild guild) {
		setupAudio(guild);
		AudioManager manager = getManager(guild);
		manager.setSelfDeafened(!manager.isSelfDeafened());
		return this;
	}

	public boolean isDeafened(Guild guild) {
		return setupAudio(guild)
				.getManager(guild)
				.isSelfDeafened();
	}
	
	public MusicBot deafen(Member member) {
		Guild guild = member.getGuild();
		setupAudio(guild);
		member.deafen(true)
			.queue();
		return this;
	}
	
	public MusicBot undeafen(Member member) {
		Guild guild = member.getGuild();
		setupAudio(guild);
		member.deafen(false)
			.queue();
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
		return member.getVoiceState()
				.isDeafened();
	}
	
	/* Muting */
	
	public MusicBot mute(Guild guild) {
		setupAudio(guild)
			.getManager(guild)
			.setSelfMuted(true);
		return this;
	}
	
	public MusicBot unmute(Guild guild) {
		setupAudio(guild)
			.getManager(guild)
			.setSelfMuted(false);
		return this;
	}
	
	public MusicBot toggleMute(Guild guild) {
		setupAudio(guild);
		AudioManager manager = getManager(guild);
		manager.setSelfMuted(!manager.isSelfMuted());
		return this;
	}
	
	public boolean isMuted(Guild guild) {
		return setupAudio(guild)
				.getManager(guild)
				.isSelfMuted();
	}
	
	public MusicBot mute(Member member) {
		setupAudio(member.getGuild());
		member.mute(true)
			.queue();
		return this;
	}
	
	public MusicBot unmute(Member member) {
		setupAudio(member.getGuild());
		member.mute(false)
			.queue();
		return this;
	}
	
	public MusicBot toggleMute(Member member) {
		setupAudio(member.getGuild());
		member.mute(!member.getVoiceState().isMuted())
			.queue();
		return this;
	}
	
	public boolean isMuted(Member member) {
		setupAudio(member.getGuild());
		return member.getVoiceState()
				.isMuted();
	}
	
	/* Pausing */
	
	public MusicBot pause(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.setPause(true);
		return this;
	}
	
	public MusicBot unpause(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.setPause(false);
		return this;
	}
	
	public MusicBot togglePause(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.togglePause();
		return this;
	}
	
	public boolean isPaused(Guild guild) {
		return setupAudio(guild)
				.getScheduler(guild)
				.isPaused();
	}
	
	/* Repeating */
	
	public MusicBot repeat(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.setRepeating(true);
		return this;
	}
	
	public MusicBot stopRepeat(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.setRepeating(false);
		return this;
	}
	
	public MusicBot toggleRepeating(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.toggleRepeating();
		return this;
	}
	
	public boolean isRepeating(Guild guild) {
		return setupAudio(guild)
				.getScheduler(guild)
				.isRepeating();
	}
	
	/* Looping */
	
	public MusicBot looping(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.setLooping(true);
		return this;
	}
	
	public MusicBot stopLooping(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.setLooping(false);
		return this;
	}
	
	public MusicBot toggleLooping(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.toggleLooping();
		return this;
	}
	
	public boolean isLooping(Guild guild) {
		return setupAudio(guild)
				.getScheduler(guild)
				.isLooping();
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
		setupAudio(guild)
			.getScheduler(guild)
			.increaseVolume();
		return getPlayer(guild).getVolume();
	}
	
	public int decreaseVolume(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.decreaseVolume();
		return getPlayer(guild).getVolume();
	}
	
	/* Track handling */
	
	public MusicBot stop(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.stop();
		return this;
	}
	
	public MusicBot seekTo(Guild guild, long position) {
		setupAudio(guild)
			.getScheduler(guild)
			.seekTo(position);
		return this;
	}
	
	public MusicBot clear(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.clear();
		return this;
	}
	
	public MusicBot next(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.nextTrack();
		return this;
	}
	
	public MusicBot previous(Guild guild) {
		setupAudio(guild)
			.getScheduler(guild)
			.previousTrack();
		return this;
	}
	
	public AudioTrack getCurrentTrack(Guild guild) {
		return setupAudio(guild)
				.getPlayer(guild)
				.getPlayingTrack();
	}
	
	/* Play */
	
	public int play(Guild guild, int index) {
		setupAudio(guild)
			.getScheduler(guild)
			.play(index);
		return index;
	}
	
	public Future<Void> play(Guild guild, String identifier, AudioLoadResultHandler handler) {
		return setupAudio(guild)
				.getPlayerManager(guild)
				.loadItem(identifier, handler);
	}
	
	public Future<Void> play(Guild guild, String identifier, boolean top, boolean next, int count, boolean playlist, StatusUpdater callback) {
		setupAudio(guild);
		AudioPlayerManager manager = getPlayerManager(guild); 
		return manager.loadItemOrdered(
					manager, 
					identifier, 
					new TrackLoadHandler(top, next, count, playlist, getScheduler(guild), callback));
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
	
	/* Remove */
	
	public Stream<AudioTrack> remove(Guild guild, Collection<Integer> indices) {
		return remove(guild.getIdLong(), indices);
	}
	
	public Stream<AudioTrack> remove(long guildID, Collection<Integer> indices) {
		return remove(guildID, indices.toArray(new Integer[0]));
	}
	
	public Stream<AudioTrack> remove(Guild guild, Integer...indices) {
		return remove(guild.getIdLong(), indices);
	}
	
	public Stream<AudioTrack> remove(long guildID, Integer...indices) {
		return remove(guildID, Arrays.stream(indices));
	}
	
	public Stream<AudioTrack> remove(Guild guild, Stream<Integer> indices) {
		return remove(guild.getIdLong(), indices);
	}
	
	public Stream<AudioTrack> remove(long guildID, Stream<Integer> indices) {
		return remove(guildID, indices.mapToInt(i -> i).toArray());
	}
	
	public Stream<AudioTrack> remove(Guild guild, IntStream indices) {
		return remove(guild.getIdLong(), indices);
	}
	
	public Stream<AudioTrack> remove(long guildID, IntStream indices) {
		return remove(guildID, indices.toArray());
	}
	
	public Stream<AudioTrack> remove(Guild guild, int...indices) {
		return remove(guild.getIdLong(), indices);
	}
	
	/* If we don't reverse sort, the indices would
	 * end up mapping to wrong tracks the further we 
	 * remove. However, removing from biggest to smallest,
	 * allows us to avoid removing the wrong tracks, all 
	 * the while ignoring the initial (potentially 
	 * corrupting) ordering.								*/
	public Stream<AudioTrack> remove(long guildID, int...indices) {
		TrackScheduler scheduler = setupAudio(guildID)
			.controller(guildID)
			.getScheduler();
		int max = scheduler.getQueue().size();
		return Arrays.stream(indices)
				.filter(i -> 0 <= i && i < max)	// in queue range
				.boxed()	// convert from primitive to Integer
				.sorted(Comparator.reverseOrder())
				.map(scheduler::remove);
	}
	
}