package lib.messages;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.CircularDeque;
import audio.TrackScheduler;
import audio.track.handlers.TrackLoadHandler;
import bot.hierarchy.UserBot;
import lib.StringLib;

public class PagedTracksHandler extends PagedHandler<AudioTrack> implements AudioLoadResultHandler {
	public static final String NOTES_EMOJI = new String(Character.toChars(0x1F3B6));
	
	private final boolean listTracks;
	private boolean loopbackIndices, player;
	private TrackScheduler scheduler;
	private TrackLoadHandler handler;
	
	public PagedTracksHandler(UserBot bot, TrackScheduler scheduler, TrackLoadHandler handler, List<AudioTrack> list) {
		super(bot, () -> list);
		this.scheduler = scheduler;
		this.handler = handler;
		listTracks = false;
		loopbackIndices = false;
		scheduler.addObserver(this);
	}
	
	public PagedTracksHandler(UserBot bot, TrackScheduler scheduler, TrackLoadHandler handler) {
		super(bot, scheduler::getQueue);
		this.scheduler = scheduler;
		this.handler = handler;
		listTracks = true;
		loopbackIndices = false;
		scheduler.addObserver(this);
	}
	
	@Override
	protected String parseElement(int index, int queueIndex, AudioTrack track) {
		CircularDeque queue = scheduler.getQueue();
		String prefix = "", postfix = "";
		if (listTracks && queueIndex == queue.getCurrent()) {
			prefix = NOTES_EMOJI + NOTES_EMOJI;
			postfix = NOTES_EMOJI + NOTES_EMOJI;
		}
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
	
	public PagedTracksHandler isPlayer() {
		player = true;
		return this;
	}
	
	@Override
	protected void onSelect(AudioTrack element) {
		if (handler != null)
			handler.trackLoaded(element);
		else
			scheduler.queue(element);
		scheduler.notifyObservers();
	}
	
	@Override
	public void trackLoaded(AudioTrack track) {
		data().add(track);
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		data().addAll(playlist.getTracks());
	}

	@Override
	public void noMatches() {
		tracked.editMessage("No matches found").queue();
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		tracked.editMessage("Loading failed : "+exception.getMessage()).queue();
	}
	
	@Override
	public void update(boolean stayOnCurrent) {
		if (stayOnCurrent && player) {
			int actual = scheduler.getCurrent()/count,
				increment = page < actual ? 1 : -1;
			while (page != actual)
				page += increment;
		}
		super.update(stayOnCurrent);
	}
}
