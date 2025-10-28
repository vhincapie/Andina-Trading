package co.edu.unbosque.foresta.controller.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/fallback")
public interface IFallbackController {

    @GetMapping("/generic")
    ResponseEntity<Map<String, Object>> generic();

    @GetMapping("/{service}")
    ResponseEntity<Map<String, Object>> byService(
            @PathVariable("service") String service,
            @RequestHeader(value = "X-Forwarded-Uri", required = false) String forwardedUri,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId
    );
}
