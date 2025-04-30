package com.constrsw.oauth.controller;
import java.util.List;
import com.constrsw.oauth.model.UserRequest;
import com.constrsw.oauth.model.UserResponse;
import com.constrsw.oauth.usecases.interfaces.ICreateUserUseCase;
import com.constrsw.oauth.usecases.interfaces.IGetAllUsersUseCase;
import com.constrsw.oauth.usecases.interfaces.IGetUserByIdUseCase;
import com.constrsw.oauth.usecases.interfaces.IUpdateUserUseCase;
import com.constrsw.oauth.usecases.interfaces.IUpdatePasswordUseCase;
import com.constrsw.oauth.usecases.user.DeleteUserUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final ICreateUserUseCase createUserUseCase;
    private final IGetAllUsersUseCase getAllUsersUseCase;
    private final IGetUserByIdUseCase getUserByIdUseCase;
    private final IUpdateUserUseCase updateUserUseCase;
    private final IUpdatePasswordUseCase updatePasswordUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserRequest userRequest) {
        String userId = createUserUseCase.execute(userRequest, false);
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> listUsers(
            @RequestParam(required = false) Boolean enabled) {
        List<UserResponse> users = getAllUsersUseCase.execute(enabled);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        UserResponse user = getUserByIdUseCase.execute(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable String id, @Valid @RequestBody UserRequest userRequest) {
        updateUserUseCase.execute(id, userRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable String id, @RequestBody String newPassword) {
        updatePasswordUseCase.execute(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        deleteUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}