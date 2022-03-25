package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

//This class schedules tracks for the audio player. It contains the queue of tracks.
public class TrackScheduler extends AudioEventAdapter {
	public static final int MAX_VOLUME = 200, 	 // in %
		DEFAULT_VOLUME = 100,
		DEFAULT_VOLUME_STEP = 5;
	private final AudioPlayer player;
	private final CircularDeque queue;
	private long nextSeek; 
	private int nextTrack;
	private boolean nextPaused;
	private int volumeStep;
	
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new CircularDeque();
		player.addListener(this);
		player.setVolume(DEFAULT_VOLUME);		// ConfigManager.getInstance().retrieveInt("DEFAULT_VOLUME", 50)
		volumeStep = DEFAULT_VOLUME_STEP;		// ConfigManager.getInstance().retrieveInt("DEFAULT_VOLUME_STEP", 5)
		nextSeek = 0;
		nextTrack = -1;
		nextPaused = false;
	}
	
	public CircularDeque getQueue() {
		return queue;
	}
	
	public int getCurrent() {
		return queue.getCurrent();
	}
	
	public void setNextSeek(long position) {	
		nextSeek = position;					// used to return to where we were if bot speaks while song is playing
	}
	
	public void setNextTrack(int position) {
		nextTrack = position;
	}
	
	public void setNextPaused(boolean paused) {
		nextPaused = paused;
	}
	
	public boolean toggleRepeating() {
		return queue.toggleRepeating();
	}
	
	public boolean toggleLooping() {
		return queue.toggleLooping();
	}
	
	public boolean seekTo(long position) {
		if (position < player.getPlayingTrack().getDuration()) {
			player.getPlayingTrack().setPosition(position);
			return true;
		} else return false;
	}
	
	public boolean setVolume(int volume) {
		if (0 <= volume && volume <= MAX_VOLUME) {
			player.setVolume(volume);
			return true;
		} else return false;
	}
	
	public void decreaseVolume() {
		int current = player.getVolume();
		if (current == 0) return;
		else if (current - volumeStep <= 0) player.setVolume(0);
		else setVolume(current - volumeStep);
	}
	
	public void increaseVolume() {
		int current = player.getVolume();
		if (current == MAX_VOLUME) return;
		else if (current + volumeStep >= MAX_VOLUME) player.setVolume(MAX_VOLUME);
		else setVolume(current + volumeStep);
	}
	
	public boolean togglePause() {
		setPause(!player.isPaused());
		return player.isPaused();
	}
	
	public boolean setPause(boolean pause) {
		player.setPaused(pause);
		return player.isPaused();
	}
	
	public boolean isPaused() {
		return player.isPaused();
	}
	
	public void stop() {
		player.stopTrack();
		queue.stopTrack();
	}
	
	public void clear() {
//		BabeBot.musicState.clear();
		if (player.getPlayingTrack() != null) queue.clearExceptCurrent();
		else queue.clear();
	}
	
	public boolean setLooping(boolean repeat) {
		return queue.setLooping(repeat);
	}
	
	public boolean setRepeating(boolean repeat) {
		return queue.setRepeating(repeat);
	}
	
	public boolean isLooping() {
		return queue.isLooping();              
	}
	
	public boolean isRepeating() {
		return queue.isRepeating();
	}
	
	public int play(int i) {
		if (i<0 || i>=queue.size()) return -1;
		if (player.isPaused()) player.setPaused(false);
		player.startTrack(queue.getAndUpdate(i), false);
		queue.setCurrent(i);
		return i;
	}
	
	public void lastToTop() {
		swap(0, queue.size()-1);
	}
	
	private void swap(int from, int to) {
		AudioTrack temp = queue.get(from);
		queue.set(from, queue.get(to));
		queue.set(to, temp);
	}
	
	public void queue(AudioTrack track) {	// Add the next track to queue or play right away if nothing is in the queue.
		player.startTrack(track, true);
		queue.add(track);					//the track to add or play
	}
	
	public void queueNext(AudioTrack track) {
		player.startTrack(track, true);
		queue.add(queue.getCurrent()+1, track);
	}
	
	public void queueTop(AudioTrack track) {
		player.startTrack(track, true);
		queue.add(0, track);
	}
	
	public void playThenBacktrack(AudioTrack track) { 
		if (player.getPlayingTrack() != null) {							// if a song was playing
			if (player.isPaused()) {									// if player was paused
				player.setPaused(false);								// remove pause state to play current
				setNextPaused(true);									// and make it pause on next track
			}
			setNextTrack(getCurrent());									// then queue previous song index
			setNextSeek(player.getPlayingTrack().getPosition());		// and seek position
		}
		player.playTrack(track);
	}
	
	public void nextTrack() {	// Start the next track, stopping the current one if it is playing.
		if (nextTrack != -1) {	// if asked for a specific index for next track
			play(nextTrack);
			nextTrack = -1;
		} else player.startTrack(queue.next(), false);		// queue.poll() can be null = stop
		if (nextSeek != 0 && nextSeek != -1) {	// if asked for specific seek for next track
			seekTo(nextSeek);
			nextSeek = 0;
		} if (nextPaused) {
			setPause(true);
			nextPaused = false;
		}
	}
	
	public void previousTrack() {
		player.startTrack(queue.previous(), false);
	}

	public AudioTrack remove(int i) {
		if (i == queue.getCurrent()) nextTrack();
		return queue.remove(i);
	}
	
	public void shuffle() {
		queue.shuffle();
	}
	
	// Events
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		// Only start the next track if the end reason is (FINISHED or LOAD_FAILED)
		if (endReason.mayStartNext) nextTrack();
	}
}