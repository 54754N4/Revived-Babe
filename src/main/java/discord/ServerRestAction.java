package discord;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import lib.Consumers;
import net.dv8tion.jda.api.managers.GuildManager;

public abstract class ServerRestAction {
	protected final GuildManager manager;
	
	public ServerRestAction(GuildManager manager) {
		this.manager = manager;	
	}
	
	public void applyChanges() {
		applyChanges(Consumers::ignore, Consumers::ignore);
	}
	
	public void applyChanges(@Nullable Consumer<? super Void> onSuccess) {
		applyChanges(onSuccess, Consumers::ignore);
	}
	
	public void applyChanges(
			@Nullable Consumer<? super Void> onSuccess, 
			@Nullable Consumer<? super Throwable> onFailure) {
		manager.queue(onSuccess, onFailure);
	}
}