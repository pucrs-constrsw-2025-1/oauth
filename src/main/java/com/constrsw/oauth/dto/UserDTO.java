package com.constrsw.oauth.dto;

// Lombok can be used here for @Data or @Getter/@Setter @NoArgsConstructor @AllArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    private String password; // Only used for creation, not typically returned

    public UserDTO() {
    }

    public UserDTO(String id, String username, String email, String firstName, String lastName, Boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}