package org.firpy.keycloakwrapper.users;

import domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController("/users")
public class UsersController
{
    /**
     * Consumir a rota do Keycloak que recupera todos os usuários
     * @param accessToken
     * @return
     */
    @GetMapping()
    public ResponseEntity<User[]> getUsers(@RequestHeader("Authorization") String accessToken)
    {
        return ResponseEntity.ok(new User[0]);
    }

    /**
     * Consumir a rota do Keycloak que recupera um usuário a partir do seu id
     * @param id
     * @param accessToken
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") String id, @RequestHeader("Authorization") String accessToken)
    {
        return ResponseEntity.ok(new User());
    }

    /**
     * Consumir a rota do Keycloak que cria um novo usuário
     * @param accessToken
     * @return
     */
    @PostMapping()
    public ResponseEntity<User> createUser(@RequestHeader("Authorization") String accessToken)
    {
        return ResponseEntity.created(URI.create("http://localhost:8081/users/1")).body(new User());
    }

    /**
     * Consumir a rota do Keycloak que atualiza um usuário (método PUT)
     * @param accessToken
     * @return
     */
    @PutMapping()
    public ResponseEntity<Object> updateUser(@RequestHeader("Authorization") String accessToken)
    {
        return ResponseEntity.ok(new Object());
    }

    @PatchMapping()
    public ResponseEntity<Object> updateUserPassword(@RequestHeader("Authorization") String accessToken)
    {
        return ResponseEntity.ok(new UpdateUserPasswordRequest("password"));
    }

    @DeleteMapping()
    public String deleteUser(@RequestHeader("Authorization") String accessToken)
    {
        return "test";
    }
}
