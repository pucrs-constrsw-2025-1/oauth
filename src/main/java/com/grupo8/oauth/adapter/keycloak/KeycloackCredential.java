package Group7.OAuth.adapter.keycloak;

public record KeycloackCredential(
        String type,
        String value,
        Boolean temporary
) {
}
