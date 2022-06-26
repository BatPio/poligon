package reactor;

import batpio.poligon.utils.ThreadUtils;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReactorThreadsTest {

    @Test
    public void threadsPoolSwitchingTest() throws InterruptedException {
        int requestCounter = 50;
        int threadsCounter = 5;
        Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(threadsCounter));
        Scheduler scheduler1 = Schedulers.fromExecutor(Executors.newFixedThreadPool(threadsCounter));
        Scheduler scheduler2 = Schedulers.fromExecutor(Executors.newFixedThreadPool(threadsCounter));
        CountDownLatch latch = new CountDownLatch(requestCounter);
        List<Integer> indices = new ArrayList<>();
        for(int i=0;i<requestCounter;i++) {
            indices.add(i);
        }

        Flux.fromIterable(indices)
                .map(x -> {
                    System.out.println(String.format(
                            "Saving person %s from thread %s", x, Thread.currentThread().getName()));
                    return x + Thread.currentThread().getName();
                })
                .publishOn(scheduler)
                .map(x -> {
                    ThreadUtils.sleep(TimeUnit.SECONDS, 1);
                    System.out.println(String.format(
                            "Finding person %s from thread %s", x,  Thread.currentThread().getName()));
                    return x + Thread.currentThread().getName();
                })
                .publishOn(scheduler1)
                .map(x -> {
                    System.out.println(String.format(
                            "Deleting person %s from thread %s", x, Thread.currentThread().getName()));
                    return x + Thread.currentThread().getName();
                })
                .subscribeOn(scheduler2)
                .subscribe(aVoid -> {
                    latch.countDown();
                    System.out.println(String.format("Subscription from thread %s %s", aVoid, Thread.currentThread().getName()));
                });

        latch.await();
    }


}
