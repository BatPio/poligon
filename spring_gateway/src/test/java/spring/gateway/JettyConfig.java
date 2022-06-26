package spring.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.JettyResourceFactory;

import java.util.concurrent.Executors;

@Configuration
public class JettyConfig {

    @Bean
    JettyResourceFactory jettyClientResourceFactory() {
        JettyResourceFactory factory = new JettyResourceFactory();
        factory.setExecutor(Executors.newFixedThreadPool(10));
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
