package lib.messages;

import java.util.ArrayList;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.CircularDeque;
import audio.TrackScheduler;
import bot.hierarchy.UserBot;
import lib.StringLib;

public class PagedTracksHandler extends PagedHandler<AudioTrack> implements AudioLoadResultHandler {
	private final boolean listTracks;
	private boolean loopbackIndices;
	private TrackScheduler scheduler;
	
	public PagedTracksHandler(UserBot bot, TrackScheduler scheduler) {
		this(bot, scheduler, false);
	}
	
	public PagedTracksHandler(UserBot bot, TrackScheduler scheduler, boolean listTracks) {
		super(bot, listTracks ? scheduler.getQueue() : new ArrayList<>());
		this.listTracks = listTracks;
		this.scheduler = scheduler;
		loopbackIndices = false;
		scheduler.addObserver(this);
	}
	
	@Override
	protected String parseElement(int index, int queueIndex, AudioTrack track) {
		CircularDeque queue = scheduler.getQueue();
		String prefix = "", postfix = "";
		if (listTracks && queueIndex == queue.getCurrent())
			prefix = postfix = new String(Character.toChars(0x1F3B6));
		return (loopbackIndices ? index : queueIndex) + ". " 
				+ prefix 
				+ track.getInfo().title 
				+ " (" + StringLib.millisToTime(track.getDuration()) + ")" 
				+ postfix;
	}
	
	public PagedTracksHandler loopbackIndices() {
		loopbackIndices = true;
		return this;
	}
	
	@Override
	protected void onSelect(AudioTrack element) {
		scheduler.queue(element);
	}
	
	@Override
	public void trackLoaded(AudioTrack track) {
		data.add(track);
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		data.addAll(playlist.getTracks());
	}

	@Override
	public void noMatches() {
		tracked.editMessage("No matches found").queue();
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		tracked.editMessage("Loading failed : "+exception.getMessage()).queue();
	}
}
