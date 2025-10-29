package co.edu.unbosque.foresta.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean("alpacaMarketClient")
    public RestTemplate alpacaMarketClient(
            RestTemplateBuilder builder,
            @Value("${alpaca.market.api-key-id}") String keyId,
            @Value("${alpaca.market.api-secret-key}") String secretKey
    ) {
        return builder
                .defaultHeader("APCA-API-KEY-ID", keyId)
                .defaultHeader("APCA-API-SECRET-KEY", secretKey)
                .build();
    }
}
