package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.AuthRequest;
import com.constrsw.oauth.dto.AuthResponse;

/**
 * Interface para serviços de comunicação com o Keycloak
 * Define operações disponíveis para autenticação e gestão de tokens
 */
public interface KeycloakService {

    /**
     * Autentica um usuário no Keycloak
     * @param authRequest Requisição com credenciais de usuário
     * @return Resposta contendo tokens de acesso e refresh
     */
    AuthResponse authenticate(AuthRequest authRequest);
    
    /**
     * Recupera informações de configuração do Keycloak
     * @return String formatada com informações de configuração
     */
    String getConfigInfo();
}