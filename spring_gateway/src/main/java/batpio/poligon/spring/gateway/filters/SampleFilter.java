package batpio.poligon.spring.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class SampleFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("Pipeline building, thread: " + Thread.currentThread().getName() + " " + path);
        return chain
                .filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    System.out.println("Post filtering, thread: " + Thread.currentThread().getName() + " " + path);
                }));
    }
}
