package com.contextoia.identityaccess.domain.model;

import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = Instant.now();
    }

    @PreUpdate
    protected void preUpdate() {
        updatedAt = Instant.now();
    }

    protected User() {}

    private User(String username, String email, String rawPassword, PasswordEncoder passwordEncoder) {
        validateUsername(username);
        validateEmail(email);
        Assert.hasText(rawPassword, "rawPassword must not be empty");

        this.username = username;
        this.email = email;
        this.passwordHash = passwordEncoder.encode(rawPassword);
    }

    public static User create(String username, String email, String rawPassword, PasswordEncoder passwordEncoder) {
        return new User(username, email, rawPassword, passwordEncoder);
    }

    public void changePassword(String newRawPassword, PasswordEncoder passwordEncoder) {
        Assert.hasText(newRawPassword, "New password cannot be empty");
        this.passwordHash = passwordEncoder.encode(newRawPassword);
    }

    public void changeEmail(String newEmail) {
        validateEmail(newEmail);
        this.email = newEmail;
    }

    public boolean isPasswordCorrect(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.passwordHash);
    }

    private void validateUsername(String username) {
        Assert.hasText(username, "username must not be empty");
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
    }

    private void validateEmail(String email) {
        Assert.hasText(email, "email must not be empty");
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
