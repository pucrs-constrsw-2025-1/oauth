package com.grupo8.oauth.application.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    @JsonProperty("rsid")
    private String id;
    @JsonProperty("rsname")
    private String name;
}
