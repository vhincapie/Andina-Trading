package co.edu.unbosque.foresta.auth.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

@Configuration
public class WebClientAuditoriaConfig {

    @Value("${auditoria.base-url}")
    private String auditoriaBaseUrl;

    @Bean
    public WebClient auditoriaWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(c -> {
                    c.addHandlerLast(new ReadTimeoutHandler(10));
                    c.addHandlerLast(new WriteTimeoutHandler(10));
                });
        return WebClient.builder()
                .baseUrl(auditoriaBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
