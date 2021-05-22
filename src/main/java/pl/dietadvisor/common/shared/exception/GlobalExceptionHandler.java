package pl.dietadvisor.common.shared.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.dietadvisor.common.shared.exception.custom.BadRequestException;
import pl.dietadvisor.common.shared.exception.custom.NotFoundException;

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({
            NoSuchElementException.class,
            NotFoundException.class
    })
    public ResponseEntity<Object> noSuchElementException(Exception exception, WebRequest request) {
        return new ResponseEntity<>(exception.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler({
            NullPointerException.class,
            BadRequestException.class
    })
    public ResponseEntity<Object> nullPointerException(Exception exception, WebRequest request) {
        return new ResponseEntity<>(exception.getMessage(), BAD_REQUEST);
    }
}
