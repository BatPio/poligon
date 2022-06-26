package batpio.poligon.spring.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static batpio.poligon.utils.DataBufferUtils.dataBufferToString;

//@Component
public class FaultyLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("Logging body" + Thread.currentThread().getName());
        ServerHttpRequest request = exchange.getRequest();
        //Nie działa, operacje niekonćzące są ignornowane
        //request.getBody().doOnNext(db -> System.out.println("Request Body: " + dataBufferToString(db)));

        request.getBody().map(db -> {
            System.out.println("Request Body: " + dataBufferToString(db));
            return db;
        });

        //Powoduje błędy
        //System.out.println("Request Body: " + dataBufferToString(request.getBody().blockFirst()));
        //request.getBody().subscribe(db -> System.out.println("Request Body: " + dataBufferToString(db)));

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
