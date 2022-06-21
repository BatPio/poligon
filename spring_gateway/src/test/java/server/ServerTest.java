package server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerTest {

    @Test
    public void exampleTest() throws IOException, InterruptedException {
        int requestCounter = 20_000;
        ExecutorService service = Executors.newFixedThreadPool(3000);

        List<SocketChannel> channels = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(requestCounter);
        for(int i=0; i< requestCounter; i++) {
            final int requestNo = i;
            Runnable sender = () -> {
                try {
                    SocketChannel socketChannel = SocketChannel.open();
                    socketChannel.connect(new InetSocketAddress("localhost", 8080));

                    // Send requests
                    ByteBuffer buffer = ByteBuffer.wrap(("Request" + requestNo).getBytes());
                    Thread.sleep(5000);
                    socketChannel.write(buffer);

                    channels.add(socketChannel);
                    latch.countDown();
                } catch (Exception e) {
                    new RuntimeException(e);
                }
            };
            service.execute(sender);
        }

        latch.await();

        // Read response
        ByteBuffer readBuffer = ByteBuffer.allocate(128);
        for (SocketChannel socketChannel : channels) {
            readBuffer.clear();
            int num;

            if ((num = socketChannel.read(readBuffer)) > 0) {
                readBuffer.flip();

                byte[] re = new byte[num];
                readBuffer.get(re);

                String result = new String(re, "UTF-8");
                System.out.println("Return value: " + result);
            }
        }
    }
}
