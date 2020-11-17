package bot.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;

import lib.Restart;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.factory.IAudioSendFactory;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public abstract class UserBot extends ListenerAdapter implements User, Runnable {
	public static final int EXIT_SUCCESS = 0;
	protected JDA jda;
	protected final Bot bot;
	protected final String name;
	protected final Logger logger;
	protected boolean exitOnKill;
	private IAudioSendFactory audioSendFactory;
	private List<OnLoadListener> loadListeners;
	
	public UserBot(Bot bot) {
		this.bot = bot;
		exitOnKill = false;
		name = getClass().getName();
		audioSendFactory = new NativeAudioSendFactory(); 		// By default uses JDA-NAS
		loadListeners = new ArrayList<>();
		logger = LoggerFactory.getLogger(name);
		logger.debug("Created %s.", name);
	}

	public abstract String[] getPrefixes();
	public abstract String prefixHelp();
	
	@Override
	public void run() {
		logger.debug("Starting thread for bot %s..", name);
		try { jda = attachListeners(buildJDA()).build(); } 
		catch (LoginException e) { logger.info("Can't login with %s", name); }
		onLoad();
	}
	
	@Override
	public void onShutdown(ShutdownEvent event) {
		logger.info(bot.toString()+" has shutdown");
		logger.info(event.getResponseNumber()+" "+event.getTimeShutdown()+" "+event.getCloseCode());
	}
	
	private JDABuilder buildJDA() {
		return JDABuilder.createDefault(bot.getToken())
				.enableIntents(EnumSet.allOf(GatewayIntent.class))
				.enableCache(EnumSet.allOf(CacheFlag.class))
				.setAudioSendFactory(getAudioSendFactory())
				.setCompression(getCompression())
                .setChunkingFilter(getChunkingFilter())
                .setActivity(getActivity())
                .setBulkDeleteSplittingEnabled(false)
                .setEnableShutdownHook(true)
                .setAutoReconnect(true);
	}
	
	// children can override and: return super.attachListeners(builder.addEventListeers(...))
	protected JDABuilder attachListeners(JDABuilder builder) {
		return builder.addEventListeners(this, new ReplyListener(this));
	}
	
	public UserBot addOnLoadListener(OnLoadListener...listeners) {
		loadListeners.addAll(Arrays.asList(listeners));
		return this;
	}

	public Activity getActivity() {
		return Activity.listening("for commands");
	}
	
	protected @Nullable IAudioSendFactory getAudioSendFactory() {
		return audioSendFactory;
	}
	
	protected Compression getCompression() {	// to disable, children override+return Compression.None
		return Compression.ZLIB;
	}
	
	public ChunkingFilter getChunkingFilter() {
		return ChunkingFilter.NONE;		// enables lazy loading
	}

	protected void onLoad() {
		for (OnLoadListener listener : loadListeners)
			listener.onLoad(this);
	}
	
	/* Kill methods */
	
	protected void preKill(boolean now) throws Exception {}
	
	public final void kill(boolean now) {
		logger.info("Starting pre-kill procedures (top-bottom) for %s.", name);
		try { preKill(now); }
		catch (Exception e) {
			logger.error("Error during %s's preKill", e);
		} finally {
			logger.info("Killing %s..", name);
			if (now) jda.shutdownNow();			// stop jda 
			else jda.shutdown();
			if (exitOnKill) exit();
		}
	}
	
	protected void exit() {
		if (Restart.flag.get()) 
			Restart.now();
		logger.info("Exiting program..");
		System.exit(EXIT_SUCCESS);
	}
	
	/* Utility methods */
	
	public UserBot delay(long milli) throws InterruptedException {
		logger.debug("%s sleepig for %d ms", name, milli);
		Thread.sleep(milli);
		return this;
	}
	
	/* JDA bot methods */
	
	public boolean isBot(String id) {
		return id.equals(getAccount().getId());
	}
	
	protected boolean isSelf(GuildVoiceUpdateEvent event) {
    	return isBot(event.getEntity().getId());
    }
	
	/* User methods */
	
	@Override
	public JDA getJDA() {
		return jda;
	}
	
	@Override
	public boolean isBot() {
		return true;
	}

	public SelfUser getAccount() {
		return jda.getSelfUser();
	}
	
	@Override
	public String getAsMention() {
		return getAccount().getAsMention();
	}

	@Override
	public String getId() {
		return getAccount().getId();
	}
	
	@Override
	public long getIdLong() {
		return getAccount().getIdLong();
	}

	@Override
	public boolean isFake() {
		return true;
	}

	@Override
	public String getName() {
		return getAccount().getName();
	}

	@Override
	public String getDiscriminator() {
		return getAccount().getDiscriminator();
	}

	@Override
	public String getAvatarId() {
		return getAccount().getAvatarId();
	}

	@Override
	public String getDefaultAvatarId() {
		return getAccount().getDefaultAvatarId();
	}

	@Override
	public String getAsTag() {
		return getAccount().getAsTag();
	}

	@Override
	public boolean hasPrivateChannel() {
		return getAccount().hasPrivateChannel();
	}

	@Override
	public RestAction<PrivateChannel> openPrivateChannel() {
		return getAccount().openPrivateChannel();
	}

	@Override
	public List<Guild> getMutualGuilds() {
		return getAccount().getMutualGuilds(); 
	}

		@Override
	public EnumSet<UserFlag> getFlags() {
		return getAccount().getFlags();
	}

	@Override
	public int getFlagsRaw() {
		return getAccount().getFlagsRaw();
	}
	
	/* Interfaces */
	
	public static interface OnLoadListener {
		void onLoad(UserBot bot);
	}
}