package ca.abelaid.url.shortener.rest;

import ca.abelaid.url.shortener.ShortenedUrlNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler({ShortenedUrlNotFoundException.class})
    ResponseEntity<Object> handleShortenedUrlNotFoundException(ShortenedUrlNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }
}
