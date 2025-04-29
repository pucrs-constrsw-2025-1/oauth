package com.constrsw.oauth.controller;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.constrsw.oauth.model.CreateRoleRequest;
import com.constrsw.oauth.model.RenameRoleRequest;
import com.constrsw.oauth.model.RoleAssignmentRequest;
import com.constrsw.oauth.model.RoleDescriptionRequest;

import java.util.List;
import jakarta.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final Keycloak keycloak;
    private final String realm;

    public RoleController(Keycloak keycloak,
            @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    /**
     * Lista todos os roles do realm.
     * GET /api/roles
     */
    @GetMapping
    public ResponseEntity<List<RoleRepresentation>> listRoles() {
        var roles = keycloak.realm(realm).roles().list();
        return ResponseEntity.ok(roles);
    }

    /**
     * Recupera um role pelo nome.
     * GET /api/roles/{roleName}
     */
    @GetMapping("/{roleName}")
    public ResponseEntity<RoleRepresentation> getRole(
            @PathVariable String roleName) {
        var role = keycloak.realm(realm)
                .roles()
                .get(roleName)
                .toRepresentation();
        return ResponseEntity.ok(role);
    }

    /**
     * Cria um novo role.
     * POST /api/roles
     * Body: { "name": "NOVO_ROLE" }
     */
    @PostMapping
    public ResponseEntity<Void> createRole(
            @RequestBody @Valid CreateRoleRequest payload) {
        var r = new RoleRepresentation();
        r.setName(payload.getName());
        keycloak.realm(realm).roles().create(r);
        return ResponseEntity.status(201).build();
    }

    /**
     * Renomeia um role.
     * PUT /api/roles/{roleName}
     * Body: { "newName": "OUTRO_ROLE" }
     */
    @PutMapping("/{roleName}")
    public ResponseEntity<Void> updateRole(
            @PathVariable String roleName,
            @RequestBody @Valid RenameRoleRequest payload) {
        var r = new RoleRepresentation();
        r.setName(payload.getNewName());
        keycloak.realm(realm)
                .roles()
                .get(roleName)
                .update(r);
        return ResponseEntity.ok().build();
    }

    /**
     * Atualiza apenas a descrição de um role.
     * PATCH /api/roles/{roleName}
     * Body: { "description": "Descrição aqui" }
     */
    @PatchMapping("/{roleName}")
    public ResponseEntity<Void> patchRole(
            @PathVariable String roleName,
            @RequestBody @Valid RoleDescriptionRequest payload) {
        var existing = keycloak.realm(realm)
                .roles()
                .get(roleName)
                .toRepresentation();
        existing.setDescription(payload.getDescription());
        keycloak.realm(realm)
                .roles()
                .get(roleName)
                .update(existing);
        return ResponseEntity.ok().build();
    }

    /**
     * Exclui logicamente um role.
     * DELETE /api/roles/{roleName}
     */
    @DeleteMapping("/{roleName}")
    public ResponseEntity<Void> deleteRole(
            @PathVariable String roleName) {
        keycloak.realm(realm)
                .roles()
                .deleteRole(roleName);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atribui um role a um usuário.
     * POST /api/roles/assign
     * Body: { "userId":"...", "roleName":"..." }
     */
    @PostMapping("/assign")
    public ResponseEntity<Void> assignRoleToUser(
            @RequestBody @Valid RoleAssignmentRequest payload) {
        var realmRes = keycloak.realm(realm);
        var roleRep = realmRes.roles()
                .get(payload.getRoleName())
                .toRepresentation();
        realmRes.users()
                .get(payload.getUserId())
                .roles()
                .realmLevel()
                .add(List.of(roleRep));
        return ResponseEntity.ok().build();
    }

    /**
     * Remove um role de um usuário.
     * POST /api/roles/unassign
     * Body: { "userId":"...", "roleName":"..." }
     */
    @PostMapping("/unassign")
    public ResponseEntity<Void> removeRoleFromUser(
            @RequestBody @Valid RoleAssignmentRequest payload) {
        var realmRes = keycloak.realm(realm);
        var roleRep = realmRes.roles()
                .get(payload.getRoleName())
                .toRepresentation();
        realmRes.users()
                .get(payload.getUserId())
                .roles()
                .realmLevel()
                .remove(List.of(roleRep));
        return ResponseEntity.ok().build();
    }
}