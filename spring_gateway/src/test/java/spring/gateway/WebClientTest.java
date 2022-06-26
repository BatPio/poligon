package spring.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import batpio.poligon.spring.gateway.Application;
import batpio.poligon.spring.gateway.config.RoutingConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.extension.PostServeAction;
import com.github.tomakehurst.wiremock.http.DelayDistribution;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.UniformDistribution;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class, properties = {
        "routing.destUrl=http://localhost:9000",
        "webclient.threadPoolSize=2"
})
@ContextConfiguration(classes = JettyConfig.class)
//@AutoConfigureWireMock(port = 0)
//@WireMockTest(httpPort = 9000)
public class WebClientTest {

    private final String DEFAULT_RESPONSE = "<response>DEFAULT</response>";
    private final String DELAYED_PATH_RESPONSE = "<response>DELAYED</response>";

    @Autowired
    private WebTestClient webClient;

    @RegisterExtension
    static WireMockExtension wm =
            WireMockExtension.newInstance()
                    .options(wireMockConfig().port(9000)
                            .extensions(new PostServeAction() {
                                @Override
                                public String getName() {
                                    return "RequestLogger";
                                }

                                @Override
                                public void doGlobalAction(ServeEvent serveEvent, Admin admin) {
                                    System.out.println("WireMock request at URL: "+ serveEvent.getRequest().getAbsoluteUrl() + " body " + serveEvent.getRequest().getBodyAsString());
                                }
                            }))
                    .build();

    @BeforeEach
    public void setupWireMock() {
        wm.stubFor(get("/delayed")
                .willReturn(ok().withFixedDelay(3000)
                        .withBody(DELAYED_PATH_RESPONSE)));
        wm.stubFor(get(urlMatching("/complexRoute/.*"))
                .willReturn(ok()
                        .withBody(DEFAULT_RESPONSE)));
        wm.stubFor(post(urlMatching("/complexRoute/.*"))
                .willReturn(ok()
                        //.withRandomDelay(new UniformDistribution(100, 2000))
                        .withBody(DEFAULT_RESPONSE)));
        wm.stubFor(get(UrlPattern.ANY)
                .atPriority(10)
                .willReturn(ok()
                        .withBody(DEFAULT_RESPONSE)));
    }

    @Test
    public void defaultPathTest() {
        webClient
                .get().uri("/anyUrl")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(DEFAULT_RESPONSE);
    }

    @Test
    public void upperCasePathTest() {
        webClient
                .get().uri(RoutingConfig.UPPERCASE_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(DEFAULT_RESPONSE.toUpperCase());
    }

    @Test
    public void complexRoutePathTest() {
        webClient
                .get().uri(RoutingConfig.COMPLEX_ROUTE_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(DEFAULT_RESPONSE);
    }

    @Test
    public void complexRoutePathParallelTest() throws InterruptedException {
        int requestCounter = 200;
        ExecutorService service = Executors.newFixedThreadPool(200);
        CountDownLatch latch = new CountDownLatch(requestCounter);
        for(int i=0; i< requestCounter; i++) {
            final int requestNo = i;
            Runnable sender = () -> {
                webClient
                        .post().uri(RoutingConfig.COMPLEX_ROUTE_PATH + "/" + requestNo)
                        .body(BodyInserters.fromObject("Body of request" + requestNo))
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(String.class)
                        .isEqualTo(DEFAULT_RESPONSE)
                        .consumeWith(entity -> {
                            latch.countDown();
                        });
            };
            service.execute(sender);
        }

        latch.await();
    }

}
