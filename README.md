Wrapper de autenticação e autorização com o Keycloak

- [ ] Autocreate realm if not present
- [ ] Token introspection for admin accounts
- [ ] Endpoint authorization through Keycloak's OAuth2 endpoint (given a token and a resource, check if the user is authorized to access it)
    - We can probably use the token introspection endpoint to get all authorized resources for a user
- [ ] Recreate and store our client's secret (client is already created in the realm, we just need to autocreate the secret now)
- [ ] OAuth2 Swagger UI authorization? maybe..?
