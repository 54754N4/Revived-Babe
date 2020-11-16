package audio.handlers;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;

public class EchoHandler implements AudioSendHandler, AudioReceiveHandler {
    private final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();

    /* Receive Handling */

    @Override
    public boolean canReceiveUser() {
    	return true;
    }
    
    @Override
    public void handleUserAudio(UserAudio userAudio) {
    	byte[] data = userAudio.getAudioData(1.0f);
    	if (data != null) queue.offer(data);
    }
    
    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
//        if (combinedAudio.getUsers().isEmpty()) return;
        byte[] data = combinedAudio.getAudioData(1.0f); 
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