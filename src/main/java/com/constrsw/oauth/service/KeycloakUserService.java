package com.constrsw.oauth.service;

import com.constrsw.oauth.model.UserRequest;
import com.constrsw.oauth.model.UserResponse;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

@Service
public class KeycloakUserService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public KeycloakUserService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    private UsersResource usersResource() {
        return keycloak.realm(realm).users();
    }

    /**
     * Cria um novo usuário no Keycloak
     * 
     * @param userRequest Dados do usuário a ser criado
     * @param isTemporary Se a senha é temporária
     * @return ID do usuário criado ou null em caso de erro
     */
    public String createUser(UserRequest userRequest, boolean isTemporary) {
        try {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(userRequest.getPassword());
            credential.setTemporary(isTemporary);

            UserRepresentation user = new UserRepresentation();
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setEnabled(true);
            user.setEmailVerified(true);
            user.setCredentials(Collections.singletonList(credential));
            user.setRealmRoles(userRequest.getRoles());

            Response response = getUsersResource().create(user);
            System.out.println("Response: " + response.getStatus());
            System.out.println("Response: " + response.getLocation());
            System.out.println("Response: " + response.getStatusInfo());

            if (response.getStatus() == 201) {
                String locationPath = response.getLocation().getPath();
                String userId = locationPath.substring(locationPath.lastIndexOf('/') + 1);

                return userId;
            } else {
                System.err.println("Falha ao criar usuário: " + response.getStatusInfo().getReasonPhrase());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UserRepresentation> getUserByUsername(String username) {
        return keycloak.realm(realm).users().search(username, true);
    }

    public void assignRolesToUser(String userId, List<String> roles) {
        RealmResource realmResource = keycloak.realm(realm);
        roles.forEach(roleName -> {
            realmResource.users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(Collections.singletonList(
                            realmResource.roles().get(roleName).toRepresentation()));
        });
    }

    public void deleteUser(String userId) {
        keycloak.realm(realm).users().get(userId).remove();
    }

    private UsersResource getUsersResource() {
        RealmResource realmResource = keycloak.realm(realm);
        return realmResource.users();
    }

    /**
     * -----------------------------------------------------------
     * Lista usuários do realm.
     * 
     * @param enabled null ➜ sem filtro
     *                true ➜ só habilitados
     *                false➜ só desabilitados
     * @return lista de UserRepresentation
     *         -----------------------------------------------------------
     */
    public List<UserRepresentation> listUsers(Boolean enabled) {
        var users = usersResource().list();
        if (enabled != null) {
            users = users.stream()
                    .filter(u -> enabled.equals(u.isEnabled()))
                    .toList();
        }
        return users;
    }

    /**
     * -----------------------------------------------------------
     * Recupera UM usuário pelo id.
     * 
     * @throws NotFoundException se não existir
     *                           -----------------------------------------------------------
     */
    public UserRepresentation getUserById(String id) {
        try {
            return usersResource().get(id).toRepresentation();
        } catch (NotFoundException e) {
            throw new RuntimeException("Usuário não encontrado", e);
        }
    }

    public void updateUser(String id, UserRequest req) {
        var existing = getUserById(id);
        existing.setUsername(req.getUsername());
        existing.setEmail(req.getEmail());
        existing.setFirstName(req.getFirstName());
        existing.setLastName(req.getLastName());
        existing.setRealmRoles(req.getRoles());
        usersResource().get(id).update(existing);
    }

    public void updatePassword(String id, String newPwd) {
        var cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(newPwd);
        cred.setTemporary(false);
        usersResource().get(id).resetPassword(cred);
    }

    public UserResponse toDto(UserRepresentation u) {
        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .enabled(u.isEnabled())
                .roles(u.getRealmRoles())
                .build();
    }
}