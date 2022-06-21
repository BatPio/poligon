package batpio.poligon.spring.gateway.config;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.*;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(@Value("${routing.destUrl}") String destUrl, @Value("${webclient.threadPoolSize}") int poolSize) {
        HttpClient httpClient = getHttpClient(poolSize);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return WebClient.builder()
                .baseUrl(destUrl)
                .clientConnector(connector)
                .build();
    }

    private HttpClient getHttpClient(int poolSize) {
        ExecutorService executorService = executorService(poolSize);
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(poolSize, executorService);
        return HttpClient
                .create()
                .runOn(loopGroup);
    }

    private ExecutorService executorService(int poolSize) {
        return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()) {
            @Override
            public void execute(Runnable command) {
                Runnable proxy = () -> {
                    System.out.println("Webclient started at: " + Thread.currentThread());
                    command.run();
                };
                super.execute(proxy);
            }
        };
    }

}
