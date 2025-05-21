package com.grupo_4.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenValidationResponse {
    @JsonProperty("active")
    private boolean active;
    
    @JsonProperty("exp")
    private long expirationTime;
    
    @JsonProperty("iat")
    private long issuedAt;
    
    @JsonProperty("jti")
    private String tokenId;
    
    @JsonProperty("iss")
    private String issuer;
    
    @JsonProperty("sub")
    private String subject;
    
    @JsonProperty("typ")
    private String tokenType;
    
    @JsonProperty("azp")
    private String authorizedParty;
    
    @JsonProperty("session_state")
    private String sessionState;
    
    @JsonProperty("acr")
    private String authenticationContextClass;
    
    @JsonProperty("realm_access")
    private RealmAccess realmAccess;
    
    @JsonProperty("scope")
    private String scope;
    
    @JsonProperty("sid")
    private String sessionId;
    
    @JsonProperty("email_verified")
    private boolean emailVerified;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("preferred_username")
    private String preferredUsername;
    
    @JsonProperty("given_name")
    private String givenName;
    
    @JsonProperty("family_name")
    private String familyName;
    
    @JsonProperty("email")
    private String email;
    
    @Data
    @NoArgsConstructor
    public static class RealmAccess {
        private String[] roles;
    }
} 