package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.LoginRequestDTO;
import com.constrsw.oauth.dto.RegisterRequestDTO;
import com.constrsw.oauth.dto.TokenResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final Keycloak keycloakAdmin;
    private final RestTemplate restTemplate;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    public void registerUser(RegisterRequestDTO registerRequest) {
        // Criar representação de usuário para o Keycloak
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmailVerified(true);

        // Configurar credenciais (senha)
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(registerRequest.getPassword());
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        // Adicionar atributos personalizados se necessário
        if (registerRequest.getAttributes() != null) {
            Map<String, List<String>> attributes = new HashMap<>();
            registerRequest.getAttributes().forEach((key, value) -> 
                attributes.put(key, Collections.singletonList(value)));
            user.setAttributes(attributes);
        }

        // Obter recurso de usuários do Keycloak
        UsersResource usersResource = keycloakAdmin.realm(realm).users();

        // Criar usuário
        try {
            jakarta.ws.rs.core.Response userResource = usersResource.create(user);
            if (userResource == null) {
                log.error("Erro ao criar usuário no Keycloak");
                throw new RuntimeException("Erro ao registrar usuário");
            }
        } catch (Exception e) {
            log.error("Erro ao criar usuário no Keycloak: " + e.getMessage());
            throw new RuntimeException("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    public TokenResponseDTO login(LoginRequestDTO loginRequest) {
        // Preparar o corpo da requisição para obter o token
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", loginRequest.getUsername());
        formData.add("password", loginRequest.getPassword());

        // Preparar headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Criar a entidade HTTP
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
        
        // Fazer a requisição para obter o token
        ResponseEntity<TokenResponseDTO> response = restTemplate.exchange(
            keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token",
            HttpMethod.POST,
            request,
            TokenResponseDTO.class
        );
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Falha na autenticação: " + response.getStatusCode());
        }
        
        return response.getBody();
    }
}