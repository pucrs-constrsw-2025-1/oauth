package com.constrsw.oauth.dto;

import org.keycloak.representations.idm.CredentialRepresentation;

public class PasswordUpdateDTO {
    private String password;

    // Getter adicionado
    public String getPassword() {
        return password;
    }

    // Setter (opcional, mas boa prática se precisar de definir o valor de fora)
    public void setPassword(String password) {
        this.password = password;
    }

    // O seu método existente
    public CredentialRepresentation toPasswordRepresentation() {
        CredentialRepresentation passwordRep = new CredentialRepresentation();
        passwordRep.setType(CredentialRepresentation.PASSWORD);
        passwordRep.setValue(this.password);
        // Considerar se a password deve ser temporária ou não aqui também
        // passwordRep.setTemporary(false); // ou true, dependendo da lógica desejada
        return passwordRep;
    }
}