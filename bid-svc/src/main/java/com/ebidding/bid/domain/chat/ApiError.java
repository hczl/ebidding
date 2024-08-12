package com.ebidding.bid.domain.chat;

import lombok.Data;

@Data
public class ApiError {
    private Error error;

    @Data
    public static class Error {
        private String message;
        private String type;
        private String param;
        private String code;
    }
}
