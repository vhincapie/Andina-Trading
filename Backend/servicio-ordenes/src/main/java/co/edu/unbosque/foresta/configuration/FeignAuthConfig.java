package co.edu.unbosque.foresta.configuration;

import feign.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.*;
import org.springframework.web.context.request.*;

@Configuration
public class FeignAuthConfig {
    @Bean
    public RequestInterceptor authForwardingInterceptor() {
        return template -> {
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            if (ra instanceof ServletRequestAttributes attrs) {
                HttpServletRequest req = attrs.getRequest();
                String auth = req.getHeader("Authorization");
                if (auth != null && !auth.isBlank()) template.header("Authorization", auth);
            }
        };
    }
}
