package com.grupo1.oauth.service;

import com.grupo1.oauth.dto.LoginRequest;
import com.grupo1.oauth.dto.UserRequest;
import com.grupo1.oauth.dto.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class KeycloakServiceTest {

    @Mock
    private WebClient mockWebClient;

    @Mock
    private RequestBodyUriSpec mockRequestBodyUriSpec;

    @Mock
    private RequestHeadersSpec mockRequestHeadersSpec;

    @Mock
    private ResponseSpec mockResponseSpec;

    private KeycloakService keycloakService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        keycloakService = new KeycloakService(mockWebClient);

        ReflectionTestUtils.setField(keycloakService, "keycloakUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(keycloakService, "realm", "my-realm");
        ReflectionTestUtils.setField(keycloakService, "clientId", "client-id");
        ReflectionTestUtils.setField(keycloakService, "clientSecret", "client-secret");
    }

    @Test
    @DisplayName("Should create user successfully when location header is present")
    void createUser_shouldSendCorrectPayloadAndSucceed_whenLocationIsReturned() {
        // Arrange
        UserRequest user = new UserRequest();
        user.setUsername("user@email.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost/user-id"));
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(headers, HttpStatus.CREATED);

        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.header(anyString(), anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.bodyValue(any())).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.toBodilessEntity()).thenReturn(Mono.just(responseEntity));

        // Act + Assert
        assertDoesNotThrow(() -> keycloakService.createUser(user, "Bearer token"));
    }

    @Test
    @DisplayName("Should throw exception when location header is missing")
    void createUser_shouldThrowException_whenLocationHeaderIsMissing() {
        // Arrange
        UserRequest user = new UserRequest();
        user.setUsername("user@email.com");
        user.setPassword("password123");
        user.setFirstName("Jane");
        user.setLastName("Doe");

        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.header(anyString(), anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.bodyValue(any())).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                keycloakService.createUser(user, "Bearer token")
        );
        assertEquals("User created, but could not retrieve ID.", exception.getMessage());
    }

    @Test
    @DisplayName("Should return token when login is successful")
    void login_shouldReturnTokenResponse_whenCredentialsAreValid() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin@pucrs.br");
        loginRequest.setPassword("a12345678");

        TokenResponse mockToken = new TokenResponse();
        mockToken.setAccessToken("token123");

        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(TokenResponse.class)).thenReturn(Mono.just(mockToken));

        // Act
        TokenResponse response = keycloakService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("token123", response.getAccessToken());
    }

    @Test
    @DisplayName("Should return null if token is not returned by WebClient")
    void login_shouldReturnNull_whenWebClientReturnsEmpty() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin@pucrs.br");
        loginRequest.setPassword("a12345678");

        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(TokenResponse.class)).thenReturn(Mono.empty());

        // Act
        TokenResponse response = keycloakService.login(loginRequest);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("Should update user data correctly")
    void updateUser_shouldSucceed_whenWebClientReturns2xx() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername("john@doe.com");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(mockWebClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.header(anyString(), anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.bodyValue(any())).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

        // Act + Assert
        assertDoesNotThrow(() -> keycloakService.updateUser("Bearer token", "123", request));
    }

    @Test
    @DisplayName("Should update user password correctly")
    void updatePassword_shouldSucceed_whenWebClientReturns2xx() {
        // Arrange
        when(mockWebClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.header(anyString(), anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.bodyValue(any())).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

        // Act + Assert
        assertDoesNotThrow(() -> keycloakService.updatePassword("Bearer token", "123", "new_pass"));
    }

    @Test
    @DisplayName("Should disable user correctly")
    void disableUser_shouldSucceed_whenWebClientReturns2xx() {
        // Arrange
        when(mockWebClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.header(anyString(), anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.bodyValue(any())).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

        // Act + Assert
        assertDoesNotThrow(() -> keycloakService.disableUser("Bearer token", "123"));
    }
}
