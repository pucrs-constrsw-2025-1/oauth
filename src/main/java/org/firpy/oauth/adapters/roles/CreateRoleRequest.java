package org.firpy.oauth.adapters.roles;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.keycloak.representations.idm.RoleRepresentation;

public record CreateRoleRequest
        (
                @JsonProperty(required = true) String name,
                @JsonProperty(required = true) String description
        )
{
    public RoleRepresentation toRoleRepresentation()
    {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(name);
        role.setDescription(description);

        return role;
    }
}
