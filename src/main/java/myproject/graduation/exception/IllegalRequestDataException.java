package myproject.graduation.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

public class IllegalRequestDataException extends AppException {
    public IllegalRequestDataException(String msg) {
        super(HttpStatus.BAD_REQUEST, msg, ErrorAttributeOptions.of(MESSAGE));
    }
}