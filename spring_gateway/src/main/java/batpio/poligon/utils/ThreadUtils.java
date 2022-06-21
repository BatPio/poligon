package batpio.poligon.utils;

import java.util.concurrent.TimeUnit;

public class ThreadUtils {

    public static void sleep(TimeUnit timeUnit, long duration) {
        try {
            Thread.sleep(timeUnit.toMillis(duration));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
