package audio.handlers;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class MirroredSendHandler implements AudioSendHandler {
	private final Map<Integer, MirrorSendHandler> mirrors;
	private final AudioPlayer audioPlayer;
	private AudioFrame lastFrame;
	
	private static int count = 0;
	
	public MirroredSendHandler(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
		mirrors = new ConcurrentHashMap<>();
	}
	
	public MirrorSendHandler newMirrorHandler() {
		MirrorSendHandler mirror = new MirrorSendHandler(count); 
		mirrors.put(count++, mirror);
		return mirror;
	}
	
	public MirrorSendHandler getMirrorHandler(int i) {
		return mirrors.get(i);
	}
	
	public MirrorSendHandler removeMirrorHandler(int i) {
		return mirrors.remove(i);
	}
	
	private void mirror(byte[] data) {
		if (data != null)
			for (MirrorSendHandler mirror : mirrors.values())
				mirror.queue.add(data);
	}

	@Override
	public boolean canProvide() {
		if (lastFrame == null)
			lastFrame = audioPlayer.provide();
		return lastFrame != null;
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		if (lastFrame == null)
			lastFrame = audioPlayer.provide();
		byte[] data = lastFrame != null ? lastFrame.getData() : null;
		mirror(data);	// mirror data provided
		lastFrame = null;
		return ByteBuffer.wrap(data);
	}

	@Override
	public boolean isOpus() {
		return true;
	}
	
	public static class MirrorSendHandler implements AudioSendHandler {
		public final int id;
		public final Queue<byte[]> queue;
		
		private MirrorSendHandler(int id) {
			this.id = id;
			queue = new ConcurrentLinkedQueue<>();
		}
		
		@Override
		public boolean canProvide() {
			 return !queue.isEmpty();
		}

		@Override
		public ByteBuffer provide20MsAudio() {
	        byte[] data = queue.poll();
	        return data == null ? null : ByteBuffer.wrap(data);
		}

		@Override
		public boolean isOpus() {
			return true;
		}
	}
}