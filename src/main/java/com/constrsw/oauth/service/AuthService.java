package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.AuthRequest;
import com.constrsw.oauth.dto.AuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Serviço de autenticação que delega ao KeycloakService.
 * Segue o princípio de responsabilidade única e o padrão de fachada.
 */
@Service
@Slf4j
public class AuthService {

    private final KeycloakService keycloakService;

    public AuthService(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    /**
     * Autentica um usuário delegando ao serviço Keycloak
     * @param authRequest Requisição com credenciais
     * @return Resposta de autenticação com tokens
     */
    public AuthResponse authenticate(AuthRequest authRequest) {
        log.info("Iniciando processo de autenticação para usuário: {}", authRequest.getUsername());
        AuthResponse response = keycloakService.authenticate(authRequest);
        log.info("Autenticação realizada com sucesso para: {}", authRequest.getUsername());
        return response;
    }
    
    /**
     * Recupera informações de configuração do Keycloak
     * @return String formatada com informações de configuração
     */
    public String getConfig() {
        return keycloakService.getConfigInfo();
    }
}