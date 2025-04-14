Wrapper de autenticação e autorização com o Keycloak

- [X] Use client roles instead of realm roles
- [X] Double-check if roles are correctly configured (Santiago)
- [X] Add read and write scopes support and update permissions (Santiago)
- [X] Login endpoint (Santiago)
  - [X] Ensure that the login endpoint returns 400 on a bad request
  - [X] Ensure that the login endpoint returns 401 on an invalid username or password
- [X] Create user endpoint (Lucas)
  - [X] Return the user id using the LOCATION header from the create user keycloak endpoint
  - [X] Ensure that the response body has the correct format
  - [X] Ensure that it returns 400 on a bad request or a bad email
  - [X] Ensure that it returns 401 if the access token is invalid
  - [X] Ensure that it returns 403 if the access token doesn't have the necessary scopes (not admin)
  - [X] Ensure that it returns 409 if the user already exists
- [X] Get users endpoint (Lucas)
    - [X] Ensure that it returns a list of JSON objects with the id, username, firstName, lastName, and enabled fields
    - [X] Ensure that it returns 400 on a bad request
    - [X] Ensure that it returns 401 if the access token is invalid
    - [X] Ensure that it returns 403 if the access token doesn't have the necessary scopes (not admin)
    - [X] Add query parameters to filter the results
        - [X] username
        - [X] firstName
        - [X] lastName
        - [X] email
        - [X] enabled
- Get user endpoint (Henrique)
    - [X] Ensure that it returns a JSON object with the id, username, firstName, lastName, and enabled fields
    - [X] Ensure that it returns 400 on a bad request
    - [X] Ensure that it returns 401 if the access token is invalid
    - [X] Ensure that it returns 403 if the access token doesn't have the necessary scopes (not admin)
    - [X] Ensure that it returns 404 if the user doesn't exist
- Update user endpoint (Henrique)
    - [X] Ensure that it receives a JSON object with the id, username, firstName, lastName, and enabled fields
    - [X] Ensure that it returns 400 on a bad request
    - [X] Ensure that it returns 401 if the access token is invalid
    - [X] Ensure that it returns 403 if the access token doesn't have the necessary scopes (not admin)
    - [X] Ensure that it returns 404 if the user doesn't exist
    - [X] Ensure that it updates the user's fields
- Update user password endpoint (Leonardo)
    - [ ] Ensure that it receives a JSON object with the newPassword field
    - [ ] Ensure that it returns 400 on a bad request
    - [ ] Ensure that it returns 401 if the access token is invalid
    - [ ] Ensure that it returns 403 if the access token doesn't have the necessary scopes (not admin)
    - [ ] Ensure that it returns 404 if the user doesn't exist
    - [ ] Ensure that it updates the user's password
- Delete user endpoint (Leonardo)
    - [ ] Ensure that it returns 400 on a bad request
    - [ ] Ensure that it returns 401 if the access token is invalid
    - [ ] Ensure that it returns 403 if the access token doesn't have the necessary scopes (not admin)
    - [ ] Ensure that it returns 404 if the user doesn't exist
    - [ ] Ensure that it deletes the user
- General error handling (Santiago)
    - [ ] Document errors on Swagger UI
    - [ ] Ensure that all endpoints have the following response body on errors:
        - errorCode: não havendo uma instrução em sentido contrário, repassar o response code do Keycloak
        - errorDescription: descrição do erro provida pelo desenvolvedor (grupo)
        - errorSource: origem do erro final (exemplos: OAuthAPI, CoursesAPI, BuildingsAPI etc.)
        - errorStack: pilha de todos os erros até o erro final 
- [X] Autocreate realm if not present
- [X] Token introspection for admin accounts
- [X] Endpoint authorization through Keycloak's OAuth2 endpoint (given a token and a resource, check if the user is authorized to access it) (Santiago)
    - We can probably use the token introspection endpoint to get all authorized resources for a user
    - [X] Return 200 if the user is authorized to access the resource
    - [X] Return 403 if the user is not authorized to access the resource
- [X] Recreate and store our client's secret (client is already created in the realm, we just need to autocreate the secret now)
