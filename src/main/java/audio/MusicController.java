package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import audio.handlers.MirroredSendHandler;
import bot.hierarchy.MusicBot;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicController {
	public static int MAX_LOADER_THREAD_POOL = 20, 
			BUFFER_DURATION = 30*1000, 		// 30s
			CLEANUP_THRESHOLD = 60*1000;	// 1 min
	public static boolean DEFAULT_DEAFENED = true;
	private final AudioPlayerManager playerManager;
	private final AudioManager manager;
	private final AudioPlayer player;
	private final AudioSendHandler sender;
	private final TrackScheduler scheduler;
	
	public MusicController(MusicBot bot, Guild guild) {
		manager = guild.getAudioManager();
		playerManager = initializeManager();
		player = playerManager.createPlayer();
		AudioSendHandler handler = bot.getAudioSendHandler();
		sender = (handler == null) ? new MirroredSendHandler(player) : handler;
		manager.setSendingHandler(sender);
		manager.setReceivingHandler(bot.getAudioReceiveHandler());
		manager.setSelfDeafened(DEFAULT_DEAFENED);
		scheduler = new TrackScheduler(player);
		player.addListener(scheduler);
	}
	
	private static final AudioPlayerManager initializeManager() {
		AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
		playerManager.setItemLoaderThreadPoolSize(MAX_LOADER_THREAD_POOL);
		playerManager.setPlayerCleanupThreshold(CLEANUP_THRESHOLD);
		playerManager.setFrameBufferDuration(BUFFER_DURATION);
		playerManager.setUseSeekGhosting(true); 	// buffers current track until new seek position is available
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);
		return playerManager;
	}
	
	public MirroredSendHandler getMirroredSendHandler() {
		if (MirroredSendHandler.class.isInstance(sender))
			return MirroredSendHandler.class.cast(sender);
		return null;
	}

	public AudioPlayerManager getPlayerManager() {
		return playerManager;
	}

	public AudioManager getManager() {
		return manager;
	}

	public AudioPlayer getPlayer() {
		return player;
	}

	public AudioSendHandler getSender() {
		return sender;
	}

	public TrackScheduler getScheduler() {
		return scheduler;
	}
}
