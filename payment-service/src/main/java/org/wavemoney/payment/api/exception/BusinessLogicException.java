package org.wavemoney.payment.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessLogicException extends RuntimeException {

    private final String code;
    private final ErrorCategory category;
    private final HttpStatus status;

    public BusinessLogicException(String code, String message, ErrorCategory category, HttpStatus status) {
        super(message);
        this.code = code;
        this.category = category;
        this.status = status;
    }

    public static BusinessLogicException notFound(String code, String message) {
        return new BusinessLogicException(code, message, ErrorCategory.NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public static BusinessLogicException business(String code, String message) {
        return new BusinessLogicException(code, message, ErrorCategory.BUSINESS, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    public static BusinessLogicException auth(String code, String message) {
        return new BusinessLogicException(code, message, ErrorCategory.AUTH, HttpStatus.UNAUTHORIZED);
    }

    public static BusinessLogicException validation(String code, String message) {
        return new BusinessLogicException(code, message, ErrorCategory.VALIDATION, HttpStatus.BAD_REQUEST);
    }

}
