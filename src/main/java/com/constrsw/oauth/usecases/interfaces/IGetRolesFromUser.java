package com.constrsw.oauth.usecases.interfaces;

import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface IGetRolesFromUser {
    public List<RoleRepresentation> execute(String userId);
}
