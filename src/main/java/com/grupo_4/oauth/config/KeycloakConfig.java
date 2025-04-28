package com.grupo_4.oauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class KeycloakConfig {

    @Value("${keycloak.external.host}")
    private String keycloakExternalHost;

    @Value("${keycloak.external.port}")
    private String keycloakExternalPort;

    @Value("${keycloak.internal.host}")
    private String keycloakInternalHost;

    @Value("${keycloak.internal.port}")
    private String keycloakInternalPort;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    @Value("${keycloak.grant.type}")
    private String grantType;

    public String getKeycloakHost() {
        return keycloakExternalHost;
    }

    public String getKeycloakPort() {
        return keycloakExternalPort;
    }

    public String getRealm() {
        return realm;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getTokenUrl() {
        return String.format("http://%s:%s/realms/%s/protocol/openid-connect/token", 
                keycloakInternalHost, keycloakInternalPort, realm);
    }

    public String getUserInfoUrl() {
        return String.format("http://%s:%s/realms/%s/protocol/openid-connect/userinfo", 
                keycloakInternalHost, keycloakInternalPort, realm);
    }
} 