package com.constrsw.oauth.dto;

import org.keycloak.representations.idm.CredentialRepresentation;

public class PasswordUpdateDTO {
    private String password;

    // Getters e Setters

    public CredentialRepresentation toPasswordRepresentation() {
        CredentialRepresentation passwordRep = new CredentialRepresentation();
        passwordRep.setType(CredentialRepresentation.PASSWORD);
        passwordRep.setValue(this.password);
        return passwordRep;
    }
}
