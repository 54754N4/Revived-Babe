package bot.hierarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;

import commands.level.normal.Restarter;
import commands.model.ThreadsManager;
import lambda.OnLoadListener;
import lib.messages.ReactionsDispatcher;
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
	public static final int EXIT_SUCCESS = 0, RESTART_CODE = 1;
	protected JDA jda;
	protected final Bot bot;
	protected final String name;
	protected final Logger logger;
	protected boolean exitOnKill;
	private List<OnLoadListener> loadListeners;
	private Object[] eventListeners;
	private ReactionsDispatcher reactionsTracker;
	
	public UserBot(Bot bot) {
		this.bot = bot;
		exitOnKill = false;
		name = getClass().getName();
		loadListeners = new ArrayList<>();
		logger = LoggerFactory.getLogger(name);
		logger.debug("Created %s.", name);
		reactionsTracker = new ReactionsDispatcher();
	}

	public abstract String[] getPrefixes();
	public abstract String prefixHelp();
	
	public UserBot start() {
		ThreadsManager.newNativeThread(this).start();
		return this;
	}
	
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
		return JDABuilder.create(bot.getToken(), EnumSet.allOf(GatewayIntent.class))
				.enableCache(EnumSet.allOf(CacheFlag.class))
				.setAudioSendFactory(getAudioSendFactory())	// By default uses JDA-NAS
				.setCompression(getCompression())
                .setChunkingFilter(getChunkingFilter())
                .setActivity(getActivity())
                .setBulkDeleteSplittingEnabled(false)
                .setEnableShutdownHook(false)
                .setAutoReconnect(true);
	}
	
	public String getBotName() {
		return bot.name();
	}
	
	// children can override and: return super.attachListeners(builder.addEventListeners(...))
	protected JDABuilder attachListeners(JDABuilder builder) {
		return builder.addEventListeners(setListeners(getListeners()));
	}
	
	public UserBot addOnLoadListener(OnLoadListener...listeners) {
		loadListeners.addAll(Arrays.asList(listeners));
		return this;
	}
	
	public Object[] getListeners() {
		return new Object[] {this, new ReplyListener(this), reactionsTracker};
	}
	
	public Object[] setListeners(Object...objects) {	// keep track for deletions
		return eventListeners = objects;
	}
	
	public ReactionsDispatcher getReactionsTracker() {
		return reactionsTracker;
	}

	public Activity getActivity() {
		return Activity.listening("mentions or commands");
	}
	
	protected @Nullable IAudioSendFactory getAudioSendFactory() {
		return new NativeAudioSendFactory();
	}
	
	protected Compression getCompression() {	// to disable, children override+return Compression.None
		return Compression.ZLIB;
	}
	
	public ChunkingFilter getChunkingFilter() {
		return ChunkingFilter.ALL;
	}

	protected void onLoad() {
		for (OnLoadListener listener : loadListeners)
			listener.onLoad(this);
	}
	
	/* Kill methods */
	
	protected void preKill(boolean now) throws Exception {}
	
	public final void kill(boolean now) {
		logger.info("Starting pre-kill procedures (top-bottom) for {}.", name);
		try { 
			jda.removeEventListener(eventListeners);
			preKill(now);
		} catch (Exception e) {
			logger.error("Error during {}'s preKill", e);
		} finally {
			logger.info("Killing {}..", name);
			try {
				if (now) jda.shutdownNow();			// stop jda
				else jda.shutdown();
			} catch (Exception e) {
				logger.error("Failed to shutdown jda", e);
			} finally { if (exitOnKill) exit(); }
		}
	}
	
	public final void kill() {
		kill(false);
	}
	
	protected void exit() {
		boolean flag = Restarter.FLAG.get(); 
		logger.info(flag ? "Restarting program" : "Exiting program..");
		System.exit(flag ? RESTART_CODE : EXIT_SUCCESS);
	}
	
	/* Utility methods */
	
	public UserBot delay(long milli) throws InterruptedException {
		logger.debug("{} sleepig for {} ms", name, milli);
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
	
	@Override
	public RestAction<Profile> retrieveProfile() {
		return getAccount().retrieveProfile();
	}

	@Override
	public boolean isSystem() {
		return getAccount().isSystem();
	}
	
	@Override
	public String toString() {
		return bot.toString();
	}
}