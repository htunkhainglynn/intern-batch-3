package org.wavemoney.payment.api.dto.response;

import org.wavemoney.payment.api.exception.ErrorCategory;

import java.time.Instant;

public record ApiResponse<T>(
        Instant timestamp,
        int statusCode,
        String message,
        Body<T> body
) {

    public sealed interface Body<T> permits Success, Error, Timeout, SystemFailure {}

    public record Success<T>(T data) implements Body<T> {}

    public record Error<T>(String code, ErrorCategory category) implements Body<T> {}

    public record Timeout<T>(String service) implements Body<T> {}

    public record SystemFailure<T>(String errorId) implements Body<T> {}

    public static <T> ApiResponse<T> success(T data, int statusCode, String message) {
        return new ApiResponse<>(Instant.now(), statusCode, message, new Success<>(data));
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return success(data, 200, message);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, 200, "OK");
    }

    public static <T> ApiResponse<T> error(int statusCode, String message, String code, ErrorCategory category) {
        return new ApiResponse<>(Instant.now(), statusCode, message, new Error<>(code, category));
    }

    public static <T> ApiResponse<T> timeout(int statusCode, String message, String service) {
        return new ApiResponse<>(Instant.now(), statusCode, message, new Timeout<>(service));
    }

    public static <T> ApiResponse<T> systemFailure(int statusCode, String message, String errorId) {
        return new ApiResponse<>(Instant.now(), statusCode, message, new SystemFailure<>(errorId));
    }
}
