package co.edu.unbosque.foresta.exceptions.handler;

import co.edu.unbosque.foresta.exceptions.exceptions.*;
import co.edu.unbosque.foresta.model.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<BaseResponse> body(HttpStatus s, String m, HttpServletRequest r) {
        return ResponseEntity.status(s).body(new BaseResponse(m, s.value(), r.getRequestURI()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse> badRequest(BadRequestException ex, HttpServletRequest r) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage(), r);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<BaseResponse> conflict(ConflictException ex, HttpServletRequest r) {
        return body(HttpStatus.CONFLICT, ex.getMessage(), r);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseResponse> notFound(NotFoundException ex, HttpServletRequest r) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage(), r);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> invalid(MethodArgumentNotValidException ex, HttpServletRequest r) {
        return body(HttpStatus.BAD_REQUEST, "Datos inválidos", r);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse> dataIntegrity(DataIntegrityViolationException ex, HttpServletRequest r) {
        return body(HttpStatus.CONFLICT, "Conflicto de datos", r);
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<BaseResponse> rse(org.springframework.web.server.ResponseStatusException ex, HttpServletRequest r) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String msg = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return body(status, msg, r);
    }

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<BaseResponse> feign(feign.FeignException ex, HttpServletRequest r) {
        int s = ex.status();
        if (s == 409) return body(HttpStatus.CONFLICT, "Conflicto con servicio externo", r);
        if (s == 400) return body(HttpStatus.BAD_REQUEST, "Solicitud inválida en servicio externo", r);
        if (s == 404) return body(HttpStatus.NOT_FOUND, "No encontrado en servicio externo", r);
        return body(HttpStatus.BAD_REQUEST, "Error al llamar servicio externo (" + s + ")", r);
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
