package audio;

import java.util.ArrayList;
import java.util.Collections;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lambda.IndexedConsumer;

public class CircularDeque extends ArrayList<AudioTrack> {
	private static final long serialVersionUID = 1221847728186248254L;
	public static final int UNINITIALISED = -1;
	
	private int current;
	private boolean circular, repeatSong;
	
	public CircularDeque() {
		this(true);
	}
	
	public CircularDeque(boolean circular) {
		super(new ArrayList<>());
		this.circular = circular;
		current = UNINITIALISED;
		repeatSong = false;
	}
	
	public boolean isValid(int index) {
		return index >= 0 && index < size();
	}
	
	@Override
	public synchronized boolean add(AudioTrack data) {
		if (current == UNINITIALISED) current = 0;
		return super.add(data);
	}
	
	@Override
	public synchronized void add(int index, AudioTrack data) {
		if (current == UNINITIALISED) current = index;
		else if (index <= current) current++;
		super.add(index, data);
	}
	
	@Override
	public synchronized void clear() {
		current = UNINITIALISED;
		super.clear();
	}
	
	@Override
	public synchronized AudioTrack remove(int i) {
		if (i<current) current--;
		return super.remove(i);
	}
	
	/**
	 * Since can't play the same instance of a track, so just
	 * make a clone if we ever need it again and update list
	 * @param i
	 * @return track
	 */
	public synchronized AudioTrack getAndUpdate(int i) { 
		if (i < 0 || i >= size()) return null;
		set(i, get(i).makeClone());
		return get(i);
	}
	
	public synchronized AudioTrack next() {
		if (repeatSong) return getAndUpdate(current);
		else if (current + 1 < size()) return getAndUpdate(++current);
		else {
			if (circular) return getAndUpdate(current = 0);
			current = UNINITIALISED;
			return null;
		}
	}

	public synchronized AudioTrack previous() {
		if (current - 1 >= 0) return getAndUpdate(--current);
		else if (circular) return getAndUpdate(current = size() - 1);
		current = UNINITIALISED;
		return null;
	}
	
	public synchronized void stopTrack() {
		current = UNINITIALISED;
	}
	
	public synchronized void shuffle() {
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
	
	public synchronized void clearExceptCurrent() {
		if (current != UNINITIALISED) {
			AudioTrack track = get(current);	// store current
			clear();							// delete all
			add(track);							// re-add current
		} else clear();
	}
	
	public synchronized boolean setLooping(boolean loop) {
		return this.circular = loop;
	}
	
	public synchronized boolean setRepeating(boolean repeat) {
		return this.repeatSong = repeat;	
	}

	public synchronized boolean isLooping() {
		return circular;
	}
	
	public synchronized boolean isRepeating() {
		return repeatSong;
	}
	
	public synchronized boolean toggleLooping() {
		return circular = !circular;
	}
	
	public synchronized boolean toggleRepeating() {
		return repeatSong = !repeatSong;
	}
	
	public synchronized int getCurrent() {
		return current;
	}

	public synchronized void setCurrent(int i) {
		current = i;
	}
	
	public void forEachIndexed(IndexedConsumer<? super AudioTrack> indexedAction) {
		for (int i=0,size=size(); i<size; i++) 
			indexedAction.accept(i, get(i));
	}
}