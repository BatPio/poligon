package batpio.poligon.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NonBlockingIOServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Selector selector = Selector.open();

        ServerSocketChannel server = ServerSocketChannel.open();
        server.socket().bind(new InetSocketAddress(8080));

        // Register it in Selector to listen for OP_ACCEPT events
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            // ergodic
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            RequestHandler requestHandler = new RequestHandler();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    // There are accepted new connections to the server
                    SocketChannel socketChannel = server.accept();

                    // A new connection does not mean that the channel has data.
                    // Here, register this new Socket Channel with Selector, listen for OP_READ events, and wait for data
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    // Data Readable
                    // The Socket Channel that monitors OP_READ events is registered in the above if branch
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    requestHandler.handle(socketChannel);
                }
            }
        }
    }
}