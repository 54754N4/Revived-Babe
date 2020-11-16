package audio.track.handlers;

import java.util.Collections;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.TrackScheduler;
import lib.ListUtil;
import lib.StringLib;

public class TrackLoadHandler implements AudioLoadResultHandler {
	private final int count, size;
	private final boolean next, top, playlist;
	private final TrackScheduler scheduler;
	private StatusUpdater callback;
	
	public TrackLoadHandler(boolean top, boolean next, int count, boolean playlist, TrackScheduler scheduler, StatusUpdater callback) {
		this.top = top;
		this.next = next;
		this.count = count;
		this.playlist = playlist;
		this.scheduler = scheduler;
		this.callback = callback;
		size = scheduler.getQueue().size();
	}

	@Override
	public void trackLoaded(AudioTrack track) {
		if (callback != null) callback.print(scheduler.getQueue().size()+". Adding to queue " + track.getInfo().title);
		if (top) scheduler.queueTop(track);
		else if (next) scheduler.queueNext(track);
		else scheduler.queue(track);
	}

	@Override
	public void playlistLoaded(AudioPlaylist pl) {
		if (callback != null) callback.print("Playlist name : "+pl.getName());
		List<AudioTrack> tracks = pl.getTracks();
		if (next) Collections.reverse(tracks);
		int i = 0;
		if (!playlist) loadSongs(tracks);
		else for (AudioTrack track : tracks) handle(track, i++);
	}

	@Override
	public void noMatches() {
		if (callback != null) callback.print("Track search returned nothing.");
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		if (callback != null) callback.print("Load track(s) failed because: " + exception.getMessage());
	}
	
	private void loadSongs(List<AudioTrack> tracks) {
		if (count < tracks.size()) tracks = ListUtil.subset(tracks, 0, count);
		int i=0;
		for (AudioTrack track : tracks)
			handle(track, i++);
	}
	
	private void handle(AudioTrack track, int num) {
		if (top) scheduler.queueTop(track);
		else if (next) scheduler.queueNext(track);
		else scheduler.queue(track);
		if (callback != null) {
			callback.print((size + num)
					+ ".\tQueuing " + track.getInfo().title
					+ " (" + StringLib.millisToTime(track.getDuration()) + ")");
		}
	}
	
	@FunctionalInterface
	public static interface StatusUpdater {
		void print(String message);
	}
}
