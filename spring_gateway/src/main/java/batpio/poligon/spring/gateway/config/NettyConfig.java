package batpio.poligon.spring.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.http.client.reactive.JettyResourceFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;

import java.util.concurrent.Executors;
import java.util.function.Function;

@Configuration
public class NettyConfig {

    @Bean
    JettyResourceFactory jettyClientResourceFactory() {
        JettyResourceFactory factory = new JettyResourceFactory();
        factory.setExecutor(Executors.newFixedThreadPool(100));
        return factory;
    }

    /*@Bean
    public ReactorResourceFactory reactorResourceFactory() {
        return new ReactorResourceFactory();
    }

    @Bean
    public ReactorClientHttpConnector reactorClientHttpConnector(
            ReactorResourceFactory reactorResourceFactory) {
        return new ReactorClientHttpConnector(reactorResourceFactory,
                Function.identity());
    }*/

}
