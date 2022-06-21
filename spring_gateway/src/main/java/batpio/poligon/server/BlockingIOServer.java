package batpio.poligon.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockingIOServer {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ExecutorService service = Executors.newFixedThreadPool(3000);

        // Listen for TCP links coming in from port 8080
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        RequestHandler requestHandler = new RequestHandler();
        while (true) {

            // This will block until a connection to a request comes in.
            SocketChannel socketChannel = serverSocketChannel.accept();

            // Open a new thread to process the request and continue listening on port 8080 in the while loop
            Runnable handler = () -> {
                try {
                    requestHandler.handle(socketChannel);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            //new Thread(handler).start();
            service.execute(handler);
        }
    }
}