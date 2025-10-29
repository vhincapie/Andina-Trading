package co.edu.unbosque.foresta.configuration;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignInternalConfig {

    @Value("${internal.api.key}")
    private String internalApiKey;

    @Bean(name = "globalInternalApiKeyInterceptor")
    public RequestInterceptor globalInternalApiKeyInterceptor() {
        return template -> {
            if (internalApiKey != null && !internalApiKey.isBlank()) {
                final String canonical = "X-INTERNAL-API-KEY";

                if (template.headers().containsKey("X-Internal-Api-Key")) {
                    template.headers().remove("X-Internal-Api-Key");
                }

                if (!template.headers().containsKey(canonical)) {
                    template.header(canonical, internalApiKey);
                }
            }
        };
    }
}
