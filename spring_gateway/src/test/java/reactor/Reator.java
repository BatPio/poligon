package reactor;

import batpio.poligon.utils.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Reator {

    @Test
    public void test() throws InterruptedException {
        Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(20));
        Scheduler scheduler1 = Schedulers.fromExecutor(Executors.newFixedThreadPool(20));

        List<Mono<String>> monosList = new ArrayList<>();
        for(int i=0;i<100;i++) {
            Mono.just(i)

                    .map(str -> {
                        //try {
                        //    Thread.sleep(100);
                        //} catch (InterruptedException e) {
                        //    throw new RuntimeException(e);
                        //}
                        return str + "step1:" + Thread.currentThread();
                    })
                    .flatMapMany(str -> {
                        return new Publisher<String>() {
                            @Override
                            public void subscribe(Subscriber<? super String> subscriber) {
                                subscriber.onNext(str + ",step2:" + Thread.currentThread());
                            }
                        };
                        //try {
                        //    Thread.sleep(100);
                        //} catch (InterruptedException e) {
                        //    throw new RuntimeException(e);
                        //}

                    })
                    //.publishOn(scheduler)
                    .map(str -> {
                       // try {
                       //     Thread.sleep(100);
                       // } catch (InterruptedException e) {
                        //    throw new RuntimeException(e);
                       // }
                        return str + ",step3:" + Thread.currentThread();
                    })
                    .subscribeOn(Schedulers.parallel())
                    .subscribe(str -> System.out.println(str));
            //monosList.add(mono);
        }
        Mono<String> allMonos = Mono.zip(monosList, strings -> {
            StringBuilder sb = new StringBuilder();
            for(Object o : strings) {
                sb.append(o);
                sb.append('\n');
            }
            return sb.toString();
        });
        System.out.println(allMonos.block());
        Thread.sleep(10000);
    }

    @Test
    public void test2() throws InterruptedException {
        Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(5));
        Scheduler scheduler1 = Schedulers.fromExecutor(Executors.newFixedThreadPool(5));

        List<Integer> indices = new ArrayList<>();
        for(int i=0;i<100;i++) {
            indices.add(i);
        }

        Flux.fromIterable(indices)
                //.publishOn(scheduler)
                .parallel(20,10)
               // .subscribeOn(scheduler)
                .runOn(scheduler)
                .map(str -> {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(str + "step1:" + Thread.currentThread());
                    return str + "step1:" + Thread.currentThread();
                })
                .flatMap(str -> {
                    return new Publisher<String>() {

                        @Override
                        public void subscribe(Subscriber<? super String> subscriber) {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println(str + "step2:" + Thread.currentThread());
                            subscriber.onNext(str + ",step2:" + Thread.currentThread());
                        }
                    };
                    /*return new Publisher<String>() {

                    };*/

                })
                .map(str -> {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(str + "step3:" + Thread.currentThread());
                    return str + ",step3:" + Thread.currentThread();
                })
                //.subscribeOn(scheduler1)
                .subscribe(str -> System.out.println(str));
        Thread.sleep(20000);
    }

    @Test
    public void test3() throws InterruptedException {
        //Scheduler single = Schedulers.newSingle("single-scheduler");
        Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(5));
        Scheduler scheduler1 = Schedulers.fromExecutor(Executors.newFixedThreadPool(5));
        Scheduler scheduler2 = Schedulers.fromExecutor(Executors.newFixedThreadPool(5));
        List<Integer> indices = new ArrayList<>();
        for(int i=0;i<100;i++) {
            indices.add(i);
        }

        Flux.fromIterable(indices)
                .parallel(5)
                .runOn(scheduler2)
                .flatMap(x -> {
                    System.out.println(String.format(
                            "Saving person %s from thread %s", x, Thread.currentThread().getName()));
                    return Mono.just(x + Thread.currentThread().getName()).publishOn(scheduler);
                })
                .flatMap(x -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(String.format(
                            "Finding person %s from thread %s", x,  Thread.currentThread().getName()));
                    return Mono.just(x + Thread.currentThread().getName()).publishOn(scheduler1);
                })
                .flatMap(x -> {
                    System.out.println(String.format(
                            "Deleting person %s from thread %s", x, Thread.currentThread().getName()));
                    return Mono.just(x + Thread.currentThread().getName());
                })
                .subscribe(aVoid -> System.out.println(String.format(
                        "Subscription from thread %s %s", aVoid, Thread.currentThread().getName())));
        Thread.sleep(20000);
    }

    @Test
    public void test4() throws InterruptedException {
        //Scheduler single = Schedulers.newSingle("single-scheduler");
        Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(5));
        Scheduler scheduler1 = Schedulers.fromExecutor(Executors.newFixedThreadPool(5));
        Scheduler scheduler2 = Schedulers.fromExecutor(Executors.newFixedThreadPool(5));
        List<Integer> indices = new ArrayList<>();
        for(int i=0;i<100;i++) {
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
                .subscribe(aVoid -> System.out.println(String.format(
                        "Subscription from thread %s %s", aVoid, Thread.currentThread().getName())));
        Thread.sleep(20000);
    }


}
