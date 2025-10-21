package io.hhplus.tdd;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException{
    private final HttpStatus code;
    private final String message;

    public CustomException(HttpStatus code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
