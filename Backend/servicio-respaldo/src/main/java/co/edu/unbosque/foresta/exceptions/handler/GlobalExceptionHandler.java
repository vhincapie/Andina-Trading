package co.edu.unbosque.foresta.exceptions.handler;

import co.edu.unbosque.foresta.exceptions.exceptions.BackupException;
import co.edu.unbosque.foresta.model.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<BaseResponse> body(HttpStatus s, String m, HttpServletRequest r) {
        return ResponseEntity.status(s).body(new BaseResponse(m, s.value(), r.getRequestURI()));
    }

    @ExceptionHandler(BackupException.class)
    public ResponseEntity<Map<String, Object>> handleBackupException(BackupException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("ok", false, "message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("ok", false, "message", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("ok", false, "message", "Error interno del servidor: " + ex.getMessage()));
    }
}