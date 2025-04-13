package org.firpy.keycloakwrapper.adapters.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import feign.FeignException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.IntrospectionResponse;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.firpy.keycloakwrapper.setup.ClientConfig;
import org.firpy.keycloakwrapper.utils.LoginUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Base64;

@RestController()
@RequestMapping("login")
public class AuthenticationController
{
	public AuthenticationController(KeycloakAuthClient keycloakClient, LoginUtils loginUtils, ClientConfig clientConfig)
	{
		this.keycloakAuthClient = keycloakClient;
		this.loginUtils = loginUtils;
		this.clientConfig = clientConfig;
	}

	/**
     * Consumir a rota POST {{base-keycloak-url}}/auth/realms/{{realm}}/protocol/openid-connect/token da REST API
     * do Keycloak para autenticação de usuário, gerando o access_token e o refresh_token a partir do
     * client_id,
     * client_secret,
     * username,
     * newPassword,
     * grant_type: newPassword.
     * @param request
     * @return
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
            description = "Invalid username or password",
            content = @Content(schema = @Schema(implementation = String.class, defaultValue = "Invalid username or password"))
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
			return ResponseEntity.status(401).body("Invalid username or password");
		}
    }

	@PostMapping("/introspect")
	@ApiResponses(value = {
		@ApiResponse
		(
			responseCode = "200",
			description = "Introspect successful",
			content = @Content(schema = @Schema(implementation = IntrospectionResponse.class))
		),
		@ApiResponse
		(
			responseCode = "401",
			description = "Invalid access token",
			content = @Content(schema = @Schema(implementation = String.class, defaultValue = "Invalid access token"))
		),
		@ApiResponse
		(
			responseCode = "500",
			description = "An unexpected error occurred",
			content = @Content(schema = @Schema(implementation = String.class, format = "An unexpected error occurred: {error message}"))
		)
	})
	public ResponseEntity<?> introspectToken(@JsonProperty(required = true) String accessTokenToInspect) throws IOException
	{
		byte[] basicAuthBytes = ("%s:%s".formatted(clientConfig.getClientId(), clientConfig.getClientSecret())).getBytes();
		try
		{
			return ResponseEntity.ok(keycloakAuthClient.introspectToken("Basic %s".formatted(Base64.getEncoder().encodeToString(basicAuthBytes)), loginUtils.getIntrospectParameters(accessTokenToInspect)));
		}
		catch (FeignException.Unauthorized unauthorized)
		{
			return ResponseEntity.status(401).body("Invalid access token");
		}
		catch (Exception exception)
		{
			return ResponseEntity.internalServerError().body("An unexpected error occurred: %s".formatted(exception.getMessage()));
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
			content = @Content(schema = @Schema(implementation = String.class, allowableValues = {"Could not parse refresh token", "Refresh token is required"}))
		),
		@ApiResponse
		(
			responseCode = "401",
			description = "Invalid refresh token",
			content = @Content(schema = @Schema(implementation = String.class, defaultValue = "Invalid refresh token"))
		),
		@ApiResponse
		(
			responseCode = "500",
			description = "An unexpected error occurred",
			content = @Content(schema = @Schema(implementation = String.class, format = "An unexpected error occurred: {error message}"))
		)
	})
	public ResponseEntity<?> loginWithRefreshToken(RefreshTokenRequest request)
	{
		if (request.refreshToken() == null)
		{
			return ResponseEntity.badRequest().body("Refresh token is required");
		}
		try
		{
			return ResponseEntity.ok(keycloakAuthClient.getAccessTokenWithRefreshToken(loginUtils.getRefreshParameters(request)));
		}
		catch (ParseException parseException)
		{
			return ResponseEntity.badRequest().body("Could not parse refresh token");
		}
		catch (FeignException.Unauthorized unauthorized)
		{
			return ResponseEntity.status(401).body("Invalid refresh token");
		}
		catch (Exception exception)
		{
			return ResponseEntity.internalServerError().body("An unexpected error occurred: %s".formatted(exception.getMessage()));
		}
	}

    private final KeycloakAuthClient keycloakAuthClient;
	private final LoginUtils loginUtils;
	private final ClientConfig clientConfig;

	@Value("${keycloak.realm}")
	private String realmName;
}
