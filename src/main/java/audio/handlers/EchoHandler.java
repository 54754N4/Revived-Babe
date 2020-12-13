package audio.handlers;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;

public class EchoHandler implements AudioSendHandler, AudioReceiveHandler {
	private static final int BUFFER_LIMIT = 20;
    private final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();
    private float volume = 1.0f;
    
    /* Receive Handling */

    @Override
    public boolean canReceiveCombined() {
        return queue.size() < BUFFER_LIMIT;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        if (combinedAudio.getUsers().isEmpty()) 
        	return;
        byte[] data = combinedAudio.getAudioData(volume); 
        if (data != null) queue.offer(data);
    }

    /* Send Handling */

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
    	return false;
    }
}