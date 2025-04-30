package com.constrsw.oauth.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String error_code;
    private String error_description;
    private String error_source;

    @Builder.Default
    private List<StackError> error_stack = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StackError {
        private String code;
        private String message;
        private String source;
    }
}
