package batpio.poligon.spring.gateway.config;

import batpio.poligon.spring.gateway.filters.SampleFilter;
import batpio.poligon.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static batpio.poligon.utils.StringUtils.join;

@Configuration
public class RoutingConfig {

    public static final String MERGE_BLOCKING_PATH = "/mergeBlocking";
    public static final String MERGE_NON_BLOCKING_PATH = "/mergeNonBlocking";
    public static final String UPPERCASE_PATH = "/upperCase";
    public static final String COMPLEX_ROUTE_PATH = "/complexRoute";

    @Bean
    public RouteLocator routes(@Value("${routing.destUrl}") String destUrl, RouteLocatorBuilder builder, WebClient webClient) {
        SampleFilter sampleFilter = new SampleFilter();

        return builder.routes()
                .route("upperCase", r -> r.path(UPPERCASE_PATH + "*")
                        .filters(f -> f.modifyResponseBody(String.class, String.class, upperCaseMutator()))
                        .uri(destUrl))
                .route("complexRoute", r -> r.path(COMPLEX_ROUTE_PATH + "/**")
                        .filters(f -> f.filters(sampleFilter))
                        .uri(destUrl))
                .route("mergeBlocking", r -> r.path(MERGE_BLOCKING_PATH + "*")
                        .filters(f -> f.modifyResponseBody(String.class, String.class, responseMergeBlockingMutator()))
                        .uri(destUrl))
                .route("mergeNonBlocking", r -> r.path(MERGE_NON_BLOCKING_PATH + "*")
                        .filters(f -> f.modifyResponseBody(String.class, String.class, responseMergeNIOMutator(webClient)))
                        .uri(destUrl))
                .route("default", r -> r.alwaysTrue().uri(destUrl))
                .build();
    }

    private RewriteFunction<String, String> upperCaseMutator() {
        return (serverWebExchange, body) -> Mono.just(body.toUpperCase());
    }

    private RewriteFunction<String, String> responseMergeNIOMutator(WebClient webClient) {
        return (serverWebExchange, body) -> {
            Mono<String> original = Mono.just(body.toUpperCase());
            return getRequestsZip(original, 5, index -> getHttpNIORequest(webClient));
        };
    }

    private RewriteFunction<String, String> responseMergeBlockingMutator() {
        return (serverWebExchange, body) -> {
            Mono<String> original = Mono.just(body.toUpperCase());
            return getRequestsZip(original, 5, index -> getHttpBlockingRequest());
        };
    }

    private Mono<String> getRequestsZip(Mono<String> original, int sideRequests,
                                        Function<Integer, Mono<String>> sideRequestProvider) {
        List<Mono<String>> requestList = new ArrayList<>();
        requestList.add(original);
        for (int i=0; i<sideRequests; i++) {
            requestList.add(sideRequestProvider.apply(i));
        }
        return Mono.zip(requestList, responses -> join('\n', responses));
    }

    private Mono<String> getHttpBlockingRequest() {
        return Mono.from(subscriber -> {
            ThreadUtils.sleep(TimeUnit.SECONDS, 1);
            System.out.println("Received:" + Thread.currentThread());
            subscriber.onNext("D");
        });
    }

    private Mono<String> getHttpNIORequest(WebClient webClient) {
        return webClient.get()
                .uri("/delayed")
                .retrieve()
                .toEntity(String.class)
                .map(response -> {
                    System.out.println("Received:" + Thread.currentThread());
                    return response.getBody();
                });
    }


}
