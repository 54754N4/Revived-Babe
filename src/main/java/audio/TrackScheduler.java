package audio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import lib.messages.PagedHandler;

//This class schedules tracks for the audio player. It contains the queue of tracks.
public class TrackScheduler extends AudioEventAdapter {
	public static final String DEFAULT_PLAYLIST = "default";
	public static final int MAX_VOLUME = 200, 	 // in %
		DEFAULT_VOLUME = 100,
		DEFAULT_VOLUME_STEP = 5;
	private final AudioPlayer player;
	private final Map<String, CircularDeque> queues;
	private String playlist;
	private CircularDeque queue;
	private long nextSeek; 
	private int nextTrack;
	private boolean nextPaused;
	private int volumeStep;
	
	private List<PagedHandler<?>> observers;
	
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		queues = new ConcurrentHashMap<>();
		queue = new CircularDeque();
		playlist = DEFAULT_PLAYLIST;
		queues.put(playlist, queue);
		player.addListener(this);
		player.setVolume(DEFAULT_VOLUME);
		volumeStep = DEFAULT_VOLUME_STEP;
		nextSeek = 0;
		nextTrack = -1;
		nextPaused = false;
		observers = new ArrayList<>();
	}

	/* Queue/playlist swapping methods */
	
	public Set<String> getPlaylistNames() {
		return queues.keySet();
	}
	
	public String getCurrentPlaylist() {
		return playlist;
	}
	
	public TrackScheduler swapPlaylist(String name) {
		if (queues.containsKey(name)) {
			playlist = name;
			queue = queues.get(name);
			if (queue.size() == 0)
				stop();
			else {
				int current = queue.getCurrent();
				if (current == CircularDeque.UNINITIALISED)
					current = 0;
				play(current);
			}
		}
		return this;
	}
	
	public TrackScheduler createPlaylist(String name) {
		queues.putIfAbsent(name, new CircularDeque());
		return this;
	}
	
	public TrackScheduler clearPlaylists() {
		queue = queues.get(DEFAULT_PLAYLIST);
		queues.clear();
		queues.put(DEFAULT_PLAYLIST, queue);
		playlist = DEFAULT_PLAYLIST;
		return this;
	}
	
	public boolean hasPlaylist(String name) {
		return queues.containsKey(name);
	}
	
	public TrackScheduler removePlaylist(String name) {
		if (name.equals(DEFAULT_PLAYLIST))
			return this;
		if (queues.containsKey(name))
			queues.remove(name);
		return this;
	}
	
	public Map<String, CircularDeque> getPlaylists() {
		return queues;
	}
	
	/* Observer handling methods */
	
	public TrackScheduler addObserver(PagedHandler<?> observer) {
		observers.add(observer);
		return this;
	}
	
	public TrackScheduler removeObserver(PagedHandler<?> observer) {
		observers.remove(observer);
		return this;
	}
	
	public TrackScheduler notifyObservers() {
		for (PagedHandler<?> observer : observers)
			observer.update();
		return this;
	}
	
	/* Queuing methods */
	
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
	
	/* Track handling methods */
	
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
		boolean repeat = queue.toggleRepeating();
		notifyObservers();
		return repeat;
	}
	
	public boolean toggleLooping() {
		boolean loop = queue.toggleLooping();
		notifyObservers();
		return loop;
	}
	
	public boolean seekTo(long position) {
		if (position < player.getPlayingTrack().getDuration()) {
			player.getPlayingTrack().setPosition(position);
			return true;
		} else return false;
	}
	
	public boolean setVolume(int volume) {
		boolean output;
		if (0 <= volume && volume <= MAX_VOLUME) {
			player.setVolume(volume);
			output = true;
		} else 
			output = false;
		notifyObservers();
		return output;
	}
	
	public void decreaseVolume() {
		int current = player.getVolume();
		if (current == 0) 
			return;
		else if (current - volumeStep <= 0) { 
			player.setVolume(0);
			notifyObservers();
		} else
			setVolume(current - volumeStep);
	}
	
	public void increaseVolume() {
		int current = player.getVolume();
		if (current == MAX_VOLUME) 
			return;
		else if (current + volumeStep >= MAX_VOLUME) {
			player.setVolume(MAX_VOLUME);
			notifyObservers();
		} else 
			setVolume(current + volumeStep);
	}
	
	public boolean togglePause() {
		setPause(!player.isPaused());
		return player.isPaused();
	}
	
	public boolean setPause(boolean pause) {
		player.setPaused(pause);
		notifyObservers();
		return player.isPaused();
	}
	
	public boolean isPaused() {
		return player.isPaused();
	}
	
	public void stop() {
		player.stopTrack();
		queue.stopTrack();
		notifyObservers();
	}
	
	public void clear() {
		if (player.getPlayingTrack() != null) 
			queue.clearExceptCurrent();
		else 
			queue.clear();
		notifyObservers();
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
		if (i<0 || i>=queue.size())
			return -1;
		if (player.isPaused())
			player.setPaused(false);
		player.startTrack(queue.getAndUpdate(i), false);
		queue.setCurrent(i);
		notifyObservers();
		return i;
	}
	
	public void lastToTop() {
		swap(0, queue.size()-1);
	}
	
	private void swap(int from, int to) {
		AudioTrack temp = queue.get(from);
		queue.set(from, queue.get(to));
		queue.set(to, temp);
		notifyObservers();
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
		notifyObservers();
	}
	
	public void previousTrack() {
		player.startTrack(queue.previous(), false);
		notifyObservers();
	}

	public AudioTrack remove(int i) {
		if (i == queue.getCurrent()) 
			nextTrack();
		AudioTrack removed = queue.remove(i);
		notifyObservers();
		return removed;
	}
	
	public void shuffle() {
		queue.shuffle();
		notifyObservers();
	}
	
	/* Events */
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		// Only start the next track if the end reason is (FINISHED or LOAD_FAILED)
		if (endReason.mayStartNext) {
			nextTrack();
			notifyObservers();
		}
	}
}