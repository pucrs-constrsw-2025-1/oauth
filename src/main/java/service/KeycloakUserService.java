package service;

import dto.PasswordUpdateDTO;
import dto.UserDTO;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakUserService {

    @Autowired
    private Keycloak keycloak;

    private static final String REALM = "myrealm"; // Substitua com seu realm real

    public UserDTO createUser(UserDTO userDTO) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEnabled(true);

        jakarta.ws.rs.core.Response response = keycloak.realm(REALM).users().create(user);
        if (response.getStatus() == 201) {
            userDTO.setId(response.getLocation().getPath().split("/")[6]);
            return userDTO;
        }
        throw new RuntimeException("Erro ao criar usuário");
    }

    public List<UserDTO> getAllUsers() {
        List<UserRepresentation> users = keycloak.realm(REALM).users().list();
        return users.stream()
                    .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName()))
                    .collect(Collectors.toList());
    }

    public UserDTO getUser(String id) {
        UserRepresentation user = keycloak.realm(REALM).users().get(id).toRepresentation();
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName());
    }

    public void updateUser(String id, UserDTO userDTO) {
        UserRepresentation user = keycloak.realm(REALM).users().get(id).toRepresentation();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        keycloak.realm(REALM).users().get(id).update(user);
    }

    public void updatePassword(String id, PasswordUpdateDTO passwordDTO) {
        keycloak.realm(REALM).users().get(id).resetPassword(passwordDTO.toPasswordRepresentation());
    }

    public void disableUser(String id) {
        UserRepresentation user = keycloak.realm(REALM).users().get(id).toRepresentation();
        user.setEnabled(false);
        keycloak.realm(REALM).users().get(id).update(user);
    }
}
