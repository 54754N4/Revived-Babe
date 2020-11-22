package lib.messages;

import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.TrackScheduler;
import lib.StringLib;

public class PagedTracksHandler extends PagedHandler<AudioTrack> implements AudioLoadResultHandler {
	private TrackScheduler scheduler;
	
	public PagedTracksHandler(List<AudioTrack> tracks) {
		super(tracks);
	}
	
	public PagedTracksHandler(TrackScheduler scheduler) {
		super(new ArrayList<>());	// initialize data
		this.scheduler = scheduler;
	}
	
	@Override
	protected String parseElement(AudioTrack track) {
		return track.getInfo().title + " (" + StringLib.millisToTime(track.getDuration()) + ")";
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
