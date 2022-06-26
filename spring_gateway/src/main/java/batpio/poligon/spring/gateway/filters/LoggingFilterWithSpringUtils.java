package batpio.poligon.spring.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

import static batpio.poligon.utils.DataBufferUtils.dataBufferToString;

@Component
public class LoggingFilterWithSpringUtils implements GlobalFilter, Ordered {

    ModifyResponseBodyGatewayFilterFactory modifyResponseBodyGatewayFilterFactory;

    public LoggingFilterWithSpringUtils(ModifyResponseBodyGatewayFilterFactory modifyResponseBodyGatewayFilterFactory) {
        this.modifyResponseBodyGatewayFilterFactory = modifyResponseBodyGatewayFilterFactory;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("LoggingFilter:" + Thread.currentThread().getName());

        RewriteFunction<String, String> responseHookFunction = (ex, body) -> {
            System.out.println("Response Body: " + body + " path: " + path + " th: " + Thread.currentThread().getName());
            return Optional.ofNullable(body).map(Mono::just).orElse(Mono.empty());
        };
        ModifyResponseBodyGatewayFilterFactory.Config config = new ModifyResponseBodyGatewayFilterFactory.Config();
        config.setRewriteFunction(String.class, String.class,responseHookFunction);
        GatewayFilter responseLoggingFilter = modifyResponseBodyGatewayFilterFactory.apply(config);

        Function<ServerHttpRequest, Mono<Void>> mutator = serverHttpRequest -> {
            ServerHttpRequestDecorator requestDecorator = new ServerHttpRequestDecorator(serverHttpRequest) {
                @Override
                public Flux<DataBuffer> getBody() {
                    Flux<DataBuffer> flux = super.getBody();
                    DataBuffer dataBuffer = exchange.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);
                    if (dataBuffer != null) {
                        System.out.println("Request Body: " + dataBufferToString(dataBuffer) + " th: " + Thread.currentThread().getName());
                    }
                    return flux;
                }
            };
            return  responseLoggingFilter.filter(exchange.mutate().request(requestDecorator).build(), chain);
        };

        return ServerWebExchangeUtils.cacheRequestBody(exchange, mutator);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
