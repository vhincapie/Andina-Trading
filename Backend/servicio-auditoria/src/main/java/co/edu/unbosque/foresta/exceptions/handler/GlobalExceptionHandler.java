package co.edu.unbosque.foresta.exceptions.handler;

import co.edu.unbosque.foresta.model.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<BaseResponse> body(HttpStatus s, String m, HttpServletRequest r) {
        return ResponseEntity.status(s).body(new BaseResponse(m, s.value(), r.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> invalid(MethodArgumentNotValidException ex, HttpServletRequest r) {
        return body(HttpStatus.BAD_REQUEST, "Datos inválidos", r);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse> dataIntegrity(DataIntegrityViolationException ex, HttpServletRequest r) {
        return body(HttpStatus.CONFLICT, "Conflicto de datos", r);
    }

    @ExceptionHandler(org.springframework.web.client.HttpClientErrorException.class)
    public ResponseEntity<BaseResponse> restTemplate(org.springframework.web.client.HttpClientErrorException ex, HttpServletRequest r) {
        int code = ex.getStatusCode().value();
        HttpStatus status = HttpStatus.valueOf(code);
        String msg = ex.getResponseBodyAsString();
        if (msg == null || msg.isBlank()) msg = "Error al llamar servicio externo (" + code + ")";
        return ResponseEntity.status(status).body(new BaseResponse(msg, status.value(), r.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> generic(Exception ex, HttpServletRequest r) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", r);
    }
}
