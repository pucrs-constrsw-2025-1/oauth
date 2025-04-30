package com.grupo8.oauth.adapter.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface AuthoritiesConverter extends Converter<Jwt, List<SimpleGrantedAuthority>> {

}