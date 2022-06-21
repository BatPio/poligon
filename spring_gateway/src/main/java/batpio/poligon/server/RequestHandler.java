package batpio.poligon.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class RequestHandler {

    public void handle(SocketChannel socketChannel) throws IOException, InterruptedException {
        // Data Readable
        // The Socket Channel that monitors OP_READ events is registered in the above if branch
        ByteBuffer readBuffer = ByteBuffer.allocate(128);
        int num = socketChannel.read(readBuffer);
        if (num > 0) {
            // Processing incoming data...
            String received = new String(readBuffer.array()).trim();
            //Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            ByteBuffer buffer = ByteBuffer.wrap(("echo:" + received).getBytes());
            socketChannel.write(buffer);
        } else if (num == -1) {
            // - 1 represents that the connection has been closed
            socketChannel.close();
        }
    }

}
