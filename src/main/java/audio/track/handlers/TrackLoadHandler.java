package audio.track.handlers;

import java.util.Collections;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.TrackScheduler;
import lambda.StatusUpdater;
import lib.ListUtil;
import lib.StringLib;

public class TrackLoadHandler implements AudioLoadResultHandler {
	private final int count, size;
	private final boolean next, top, playlist;
	private final TrackScheduler scheduler;
	private boolean notify;
	private int toggleNotify, toggleCount;
	
	private StatusUpdater callback;
	
	public TrackLoadHandler(TrackScheduler scheduler) {
		this(false, false, 1, false, scheduler, null);
	}
	
	public TrackLoadHandler(boolean top, boolean next, int count, boolean playlist, TrackScheduler scheduler, StatusUpdater callback) {
		this.top = top;
		this.next = next;
		this.count = count;
		this.playlist = playlist;
		this.scheduler = scheduler;
		this.callback = callback;
		notify = true;
		toggleNotify = -1;
		toggleCount = 0;
		size = scheduler.getQueue().size();
	}

	@Override
	public void trackLoaded(AudioTrack track) {
		incrementToggle();
		if (callback != null) 
			callback.println(scheduler.getQueue().size()+". Adding to queue " + track.getInfo().title);
		if (top) 
			scheduler.queueTop(track);
		else if (next) 
			scheduler.queueNext(track);
		else 
			scheduler.queue(track);
		if (notify) 
			scheduler.notifyObservers();
	}

	@Override
	public void playlistLoaded(AudioPlaylist pl) {
		if (callback != null) 
			callback.println("Playlist name : "+pl.getName());
		List<AudioTrack> tracks = pl.getTracks();
		if (next || top) 
			Collections.reverse(tracks);
		int i = 0;
		if (!playlist) 
			loadSongs(tracks);
		else 
			for (AudioTrack track : tracks) 
				handle(track, i++);
		if (notify) 
			scheduler.notifyObservers();
	}

	@Override
	public void noMatches() {
		if (callback != null) callback.println("Track search returned nothing.");
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		if (callback != null) callback.println("Load track(s) failed because: " + exception.getMessage());
	}
	
	private void loadSongs(List<AudioTrack> tracks) {
		if (count < tracks.size()) 
			tracks = ListUtil.subset(tracks, 0, count);
		int i=0;
		for (AudioTrack track : tracks)
			handle(track, i++);
	}
	
	private void handle(AudioTrack track, int num) {
		incrementToggle();
		if (top) 
			scheduler.queueTop(track);
		else if (next) 
			scheduler.queueNext(track);
		else 
			scheduler.queue(track);
		if (callback != null) {
			callback.println((size + num)
					+ ".\tQueuing " + track.getInfo().title
					+ " (" + StringLib.millisToTime(track.getDuration()) + ")");
		}
	}
	
	public TrackLoadHandler setNotify(boolean notify) {
		this.notify = notify;
		return this;
	}
	
	public TrackLoadHandler setToggleCount(int toggleNotify) {
		this.toggleNotify = toggleNotify;
		setNotify(false);
		return this;
	}
	
	private void incrementToggle() {
		if (++toggleCount == toggleNotify)
			setNotify(true);
	}
}
