package com.grupo8.oauth.adapter.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface AuthenticationConverter extends Converter<Jwt, JwtAuthenticationToken> {
}