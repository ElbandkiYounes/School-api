package com.school_1.api.User.models;

import jakarta.validation.constraints.*;

public class SignupPayload {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^[A-Za-z0-9@#$%^&+=]{8,}$",
            message = "Password must be at least 8 characters long and contain only letters, digits, or the special characters @#$%^&+="
    )
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid format")
    private String email;

    @NotNull(message = "Role cannot be null")
    private UserRole role;

    public SignupPayload() {
    }

    public SignupPayload(String username, String email, String password, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.trim() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public User toUser() {
        return new User(username, password, email, role);
    }

}
