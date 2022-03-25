package lambda;

import bot.hierarchy.UserBot;

@FunctionalInterface
public interface OnLoadListener {
	void onLoad(UserBot bot);
}