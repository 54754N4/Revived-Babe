package audio;

import java.util.ArrayList;
import java.util.Collections;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class CircularDeque extends ArrayList<AudioTrack> {
	private static final long serialVersionUID = 1221847728186248254L;
	public static final int UNINITIALISED = -1;
	
	private int current;
	private boolean autoplay, circular, repeatSong;
//	private AudioManager manager;
	
	public CircularDeque() {	// AudioManager manager
		this(true);  // ConfigManager.getInstance().retrieveBool("DEFAULT_REPEAT_QUEUE", true), manager
	}
	
	public CircularDeque(boolean circular) {	// , AudioManager manager
		super(new ArrayList<>());
//		this.manager = manager;
		this.circular = circular;
		current = UNINITIALISED;
		autoplay = false;
		repeatSong = false; // ConfigManager.getInstance().retrieveBool("DEFAULT_REPEAT_SONG", false);
	}
	
	public boolean isValid(int index) {
		return index >= 0 && index < size();
	}
	
	@Override
	public boolean add(AudioTrack data) {
		if (current == UNINITIALISED) current = 0;
		return super.add(data);
	}
	
	@Override
	public void add(int index, AudioTrack data) {
		if (current == UNINITIALISED) current = index;
		else if (index <= current) current++;
		super.add(index, data);
	}
	
	@Override
	public void clear() {
		current = UNINITIALISED;
		super.clear();
	}
	
	@Override
	public AudioTrack remove(int i) {
		if (i<current) current--;
		return super.remove(i);
	}
	
	/**
	 * Since can't play the same instance of a track, so just
	 * make a clone if we ever need it again and update list
	 * @param i
	 * @return
	 */
	public AudioTrack getAndUpdate(int i) { 
		if (i < 0 || i >= size()) return null;
		set(i, get(i).makeClone());	// 
		return get(i);
	}
	
	public AudioTrack next() {
		if (repeatSong) return getAndUpdate(current);
		else if (current + 1 < size()) return getAndUpdate(++current);
		else {
			if (isAutoplay()) return getNextAutoplay();
			else if (circular) return getAndUpdate(current = 0);
			current = UNINITIALISED;
			return null;
		}
	}
	
	public boolean isAutoplay() {
		return autoplay; 	// ConfigManager.getInstance().retrieveBool("AUTOPLAY", true);
	}
	
	private AudioTrack getNextAutoplay() {
//		String current = get(this.current).getInfo().uri;
//		try {
//			AutoplayResult next = YoutubeScraper.retrieveAutoplayOf(current);
//			AutoplayTrackLoadHandler atlh = new AutoplayTrackLoadHandler(next.url, bot); 
//			manager.loadItemOrdered(manager, next.url, atlh);
//			atlh.load(true);
//			this.current++;
//			AudioTrack track;
//			add(track = atlh.nextTrack());
//			return track;
			return null;
//		} catch (IOException | InterruptedException e) { 
//			this.current = UNINITIALISED;
//			return null; 
//		}
	}

	public AudioTrack previous() {
		if (current - 1 >= 0) return getAndUpdate(--current);
		else if (circular) return getAndUpdate(current = size() - 1);
		current = UNINITIALISED;
		return null;
	}
	
	public void stopTrack() {
		current = UNINITIALISED;
	}
	
	public void shuffle() {
		int hash = get(getCurrent()).hashCode();
		if (size() != 0) 
			Collections.shuffle(this);
		for (int i=0; i<size(); i++) { 
			if (get(i).hashCode() == hash) {
				setCurrent(i); 					// updates current index after shuffle
				break;
			}
		}
	}
	
	public void clearExceptCurrent() {
		if (current != UNINITIALISED) {
			AudioTrack track = get(current);	// store current
			clear();							// delete all
			add(track);							// re-add current
		} else clear();
	}
	
	public boolean setLooping(boolean loop) {
		return this.circular = loop;
	}
	
	public boolean setRepeating(boolean repeat) {
		return this.repeatSong = repeat;	
	}

	public boolean isLooping() {
		return circular;
	}
	
	public boolean isRepeating() {
		return repeatSong;
	}
	
	public boolean toggleLooping() {
		return circular = !circular;
	}
	
	public boolean toggleRepeating() {
		return repeatSong = !repeatSong;
	}
	
	public int getCurrent() {
		return current;
	}

	public void setCurrent(int i) {
		current = i;
	}
	
	public void forEachIndexed(IndexedConsumer<? super AudioTrack> indexedAction) {
		for (int i=0; i<size(); i++) indexedAction.accept(i, get(i));
	}
	
	@FunctionalInterface
	public static interface IndexedConsumer<E> {
		void accept(int i, E item);
	}
}