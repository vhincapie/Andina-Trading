// exceptions/BadRequestException.java
package co.edu.unbosque.foresta.exceptions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
