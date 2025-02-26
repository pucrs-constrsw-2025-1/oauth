package org.firpy.keycloakwrapper.adapters.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("login")
public class LoginController
{
    /**
     * Consumir a rota POST {{base-keycloak-url}}/auth/realms/{{realm}}/protocol/openid-connect/token da REST API
     * do Keycloak para autenticação de usuário, gerando o access_token e o refresh_token a partir do
     * client_id,
     * client_secret,
     * username,
     * password,
     * grant_type: password.
     * @param loginRequest
     * @return
     */
    @PostMapping()
    public LoginResponse login(@RequestBody LoginRequest loginRequest)
    {
        return new LoginResponse("bearer", "token", 3600, "refreshToken", 3600);
    }
}
