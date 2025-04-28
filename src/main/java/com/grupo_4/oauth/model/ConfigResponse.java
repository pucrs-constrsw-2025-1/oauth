package com.grupo_4.oauth.model;

public record ConfigResponse(
    String tokenUrl,
    String realm,
    String clientId
) {} 