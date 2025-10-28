package co.edu.unbosque.foresta.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfiguration {

    private static SimpleClientHttpRequestFactory factoryWithTimeouts(Duration connect, Duration read) {
        var f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout((int) connect.toMillis());
        f.setReadTimeout((int) read.toMillis());
        return f;
    }

    @Primary
    @Bean("internalRestTemplate")
    public RestTemplate internalRestTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(() -> factoryWithTimeouts(Duration.ofSeconds(5), Duration.ofSeconds(15)))
                .additionalInterceptors((request, body, execution) -> {
                    var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attrs != null) {
                        var incoming = attrs.getRequest();
                        String auth = incoming.getHeader(HttpHeaders.AUTHORIZATION);
                        if (auth != null && !auth.isBlank()) {
                            request.getHeaders().add(HttpHeaders.AUTHORIZATION, auth);
                        }
                        String internal = incoming.getHeader("X-Internal-Api-Key");
                        if (internal != null && !internal.isBlank()) {
                            request.getHeaders().add("X-Internal-Api-Key", internal);
                        }
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    @Bean("externalRestTemplate")
    public RestTemplate externalRestTemplate(RestTemplateBuilder builder) {
        RestTemplate rt = builder
                .requestFactory(() -> factoryWithTimeouts(Duration.ofSeconds(5), Duration.ofSeconds(15)))
                .build();

        ObjectMapper om = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<HttpMessageConverter<?>> convs = new ArrayList<>(rt.getMessageConverters());
        boolean replaced = false;
        for (int i = 0; i < convs.size(); i++) {
            HttpMessageConverter<?> c = convs.get(i);
            if (c instanceof MappingJackson2HttpMessageConverter mj) {
                MappingJackson2HttpMessageConverter nj = new MappingJackson2HttpMessageConverter(om);
                nj.setSupportedMediaTypes(mj.getSupportedMediaTypes());
                convs.set(i, nj);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            convs.add(new MappingJackson2HttpMessageConverter(om));
        }
        rt.setMessageConverters(convs);

        return rt;
    }
}
