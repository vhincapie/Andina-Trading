package co.edu.unbosque.foresta.configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class InternalApiKeyFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(InternalApiKeyFilter.class);

    @Value("${internal.api.key}")
    private String expected;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        String key = req.getHeader("X-INTERNAL-API-KEY");
        log.info("[INTERNAL-KEY] {} {} header='{}'", req.getMethod(), req.getRequestURI(), key);

        if (expected != null && !expected.isBlank() && !expected.equals(key)) {
            ((jakarta.servlet.http.HttpServletResponse) response).sendError(401, "Unauthorized");
            return;
        }
        chain.doFilter(request, response);
    }
}
