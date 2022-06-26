package batpio.poligon.utils;

import org.springframework.core.io.buffer.DataBuffer;

public class DataBufferUtils {

    public static String dataBufferToString(DataBuffer dataBuffer) {
        byte[] buffer = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(buffer);
        String str = new String(buffer);
        dataBuffer.readPosition(0);
        return str;
    }

}
