package com.grupo1.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Structured error response")
public class ErrorResponse {

    @Schema(example = "400", description = "Error code returned by the API")
    public String error_code;

    @Schema(example = "Invalid request.", description = "Description of the error")
    public String error_description;

    @Schema(example = "OAuthAPI", description = "Source/origin of the error")
    public String error_source;

    @Schema(description = "List of error stack details")
    public List<Map<String, Object>> error_stack;
}
