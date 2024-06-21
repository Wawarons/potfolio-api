package me.podsialdy.api.Error;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.DTO.ResponseDto;

/**
 * GlobalExceptionHandle class handles global exceptions for the API.
 * It is annotated with @ControllerAdvice to provide centralized exception
 * handling across all @Controller classes.
 * This class logs and handles MethodArgumentNotValidException by returning a
 * ResponseEntity with a custom ResponseDto.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

    /**
     * <p>Handles MethodArgumentNotValidException by returning a
     * ResponseEntity with a custom ResponseDto.</p>
     * @param ex the MethodArgumentNotValidException to handle
     * @return a ResponseEntity with a custom ResponseDto
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        List<String> violations = ex.getFieldErrors()
                .stream()
                .map((error) -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message(violations.toString())
                        .build());

    }

}
