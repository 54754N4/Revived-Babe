package audio.track.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.TrackScheduler;

public class SpeakTrackHandler implements AudioLoadResultHandler {
	private static Logger logger = LoggerFactory.getLogger(SpeakTrackHandler.class);
	private TrackScheduler scheduler;
	
	public SpeakTrackHandler(TrackScheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	@Override
	public void trackLoaded(AudioTrack track) {
		scheduler.playThenBacktrack(track);
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		logger.info("Loaded playlist during speak ? Wtf, report to my master plz.");
	}

	@Override
	public void noMatches() {
		logger.info("Could not load wav file. Not found.");
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		logger.error("Failed to play wav file", exception);				
	}
}
