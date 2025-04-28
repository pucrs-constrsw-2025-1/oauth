package org.firpy.oauth.adapters.login;

import feign.FeignException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.firpy.oauth.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.firpy.oauth.errors.OAuthError;
import org.firpy.oauth.utils.LoginUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;

@RestController()
@RequestMapping("login")
public class AuthenticationController
{
    public AuthenticationController(KeycloakAuthClient keycloakClient, LoginUtils loginUtils)
    {
        this.keycloakAuthClient = keycloakClient;
        this.loginUtils = loginUtils;
    }

    /**
     * Consumir a rota POST {{base-keycloak-url}}/auth/realms/{{realm}}/protocol/openid-connect/token da REST API
     * do Keycloak para autenticação de usuário, gerando o access_token e o refresh_token a partir do
     * client_id,
     * client_secret,
     * email,
     * newPassword,
     * grant_type: newPassword.
     */
    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Login successful",
                            content = @Content(schema = @Schema(implementation = AccessToken.class))
                    ),
            @ApiResponse
                    (
                            responseCode = "401",
                            description = "Invalid email or password",
                            content = @Content(schema = @Schema(implementation = OAuthError.class))
                    )
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws IOException
    {
        try
        {
            AccessToken accessToken = keycloakAuthClient.getAccessTokenWithPassword(loginUtils.getLoginParameters(request), realmName);
            return ResponseEntity.ok(accessToken);
        }
        catch (FeignException.Unauthorized unauthorized)
        {
            return ResponseEntity.status(401).body(OAuthError.keycloakError("Invalid email or password"));
        }
    }

    @PostMapping("/refresh")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Login with refresh token successful",
                            content = @Content(schema = @Schema(implementation = AccessToken.class))
                    ),
            @ApiResponse
                    (
                            responseCode = "400",
                            description = "Could not parse refresh token",
                            content = @Content(schema = @Schema(implementation = OAuthError.class))
                    ),
            @ApiResponse
                    (
                            responseCode = "401",
                            description = "Invalid refresh token",
                            content = @Content(schema = @Schema(implementation = OAuthError.class))
                    ),
            @ApiResponse
                    (
                            responseCode = "500",
                            description = "An unexpected error occurred",
                            content = @Content(schema = @Schema(implementation = OAuthError.class))
                    )
    })
    public ResponseEntity<?> loginWithRefreshToken(RefreshTokenRequest request)
    {
        if (request.refreshToken() == null)
        {
            return ResponseEntity.badRequest().body(OAuthError.internalError("Refresh token is required"));
        }
        try
        {
            return ResponseEntity.ok(keycloakAuthClient.getAccessTokenWithRefreshToken(loginUtils.getRefreshParameters(request)));
        }
        catch (ParseException parseException)
        {
            return ResponseEntity.badRequest().body(OAuthError.keycloakError("Could not parse refresh token"));
        }
        catch (FeignException.Unauthorized unauthorized)
        {
            return ResponseEntity.status(401).body(OAuthError.keycloakError("Invalid refresh token"));
        }
        catch (Exception exception)
        {
            return ResponseEntity.internalServerError().body(OAuthError.keycloakError("An unexpected error occurred: %s".formatted(exception.getMessage())));
        }
    }

    private final KeycloakAuthClient keycloakAuthClient;
    private final LoginUtils loginUtils;

    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;
}
