package co.edu.unbosque.foresta.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class BearerResolver {
    public String currentBearerOrNull() {
        var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        HttpServletRequest req = attrs.getRequest();
        String h = req.getHeader(HttpHeaders.AUTHORIZATION);
        return (h != null && h.startsWith("Bearer ")) ? h : null;
    }
}
