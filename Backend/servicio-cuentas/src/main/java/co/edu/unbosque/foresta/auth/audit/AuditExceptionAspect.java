package co.edu.unbosque.foresta.auth.audit;

import co.edu.unbosque.foresta.auth.dto.AuditLogRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
public class AuditExceptionAspect {

    private final AuditSender auditSender;

    public AuditExceptionAspect(AuditSender auditSender) {
        this.auditSender = auditSender;
    }

    @AfterThrowing(pointcut = "within(co.edu.unbosque.foresta.controller..*)", throwing = "ex")
    public void onControllerException(JoinPoint jp, Throwable ex) {
        HttpServletRequest req = currentRequest();
        String path = (req != null ? req.getRequestURI() : "");
        String method = (req != null ? req.getMethod() : "");
        String action = guessAction(method, jp.getSignature().getName());

        String resourceCode = toResourceCode(path);
        String eventCode = (resourceCode != null ? resourceCode + "_" + action + "_ERR" : "CATALOGS_ERR");

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("exception", ex.getClass().getSimpleName());
        details.put("message", shorten(ex.getMessage(), 500));
        details.put("path", path);
        details.put("method", method);
        if (req != null && !req.getParameterMap().isEmpty()) {
            details.put("params", req.getParameterMap());
        }

        auditSender.log("", new AuditLogRequest(
                eventCode, path, "Error en " + (action != null ? action : "operación"), details
        ));
    }

    private HttpServletRequest currentRequest() {
        var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attrs != null ? attrs.getRequest() : null);
    }

    private String guessAction(String method, String handlerName) {
        if (method == null) return "OPERACION";
        if (HttpMethod.POST.matches(method)) return "CREATE";
        if (HttpMethod.PUT.matches(method) || HttpMethod.PATCH.matches(method)) return "UPDATE";
        if (HttpMethod.DELETE.matches(method)) return "DELETE";
        if (HttpMethod.GET.matches(method)) return "READ";
        return "OPERACION";
    }

    private String toResourceCode(String path) {
        if (path == null) return null;
        String p = path.toLowerCase();
        if (p.contains("/catalogos/ciudades")) return "CAT_CITY";
        if (p.contains("/catalogos/paises"))  return "CAT_COUNTRY";
        if (p.contains("/catalogos/situaciones-economicas")) return "CAT_ECO";
        return "CATALOGS";
    }

    private String shorten(String s, int max) {
        if (s == null) return null;
        return (s.length() > max ? s.substring(0, max) + "…" : s);
    }
}