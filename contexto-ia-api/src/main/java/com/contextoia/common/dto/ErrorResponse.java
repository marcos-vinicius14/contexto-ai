package com.contextoia.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String message,
    String errorCode,
    String path,
    String error,
    List<FieldError> fieldsErrors
) {

    public record FieldError(
        String field,
        String message
    ) {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDateTime timestamp;
        private int status;
        private String message;
        private String errorCode;
        private String path;
        private List<FieldError> fieldsErrors;
        private String error;

        public Builder error(String error) {
            this.error = error;
            return this;
        }


        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder fieldsErrors(List<FieldError> fieldsErrors) {
            this.fieldsErrors = fieldsErrors;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(timestamp, status, message, errorCode, path, error, fieldsErrors);
        }
    }

}
