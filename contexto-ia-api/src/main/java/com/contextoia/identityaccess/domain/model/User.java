package com.contextoia.identityaccess.domain.model;

import com.contextoia.common.exceptions.InvalidDataException;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_users")
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
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

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "locked", nullable = false)
    private boolean locked;

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

    private User(UUID id, String username, String email) {
        validateEmail(email);
        validateUsername(username);
        this.id = id;
        this.username = username;
        this.email = email;
    }

    private User(String username, String email, String rawPassword, PasswordEncoder passwordEncoder) {
        validateUsername(username);
        validateEmail(email);
        Assert.hasText(rawPassword, "rawPassword must not be empty");

        this.username = username;
        this.email = email;
        this.passwordHash = passwordEncoder.encode(rawPassword);
        this.enabled = true;
        this.locked = false;
    }

    public static User create(String username, String email, String rawPassword, PasswordEncoder passwordEncoder) {
        return new User(username, email, rawPassword, passwordEncoder);
    }

    public  static User createMinimal(UUID id, String username, String email) {
        return  new User(id, username, email);
    }



    /**
     * Changes the user's password by encoding the provided raw password and updating the passwordHash field.
     *
     * @param newRawPassword the new raw password to be set; it must not be empty
     * @param passwordEncoder the*/
    public void changePassword(String newRawPassword, PasswordEncoder passwordEncoder) {
        Assert.hasText(newRawPassword, "New password cannot be empty");
        this.passwordHash = passwordEncoder.encode(newRawPassword);
    }

    /**
     * Updates the email address of the current user.
     *
     * @param newEmail the new email address to be set. The email address must be valid, non-empty,
     *                 and follow the standard email format (e.g., username@example.com).
     *                 This method validates the provided email and assigns*/
    public void changeEmail(String newEmail) {
        validateEmail(newEmail);
        this.email = newEmail;
    }

    /**
     * Verifies if the provided raw password matches the stored hashed password for the user.
     *
     * @param rawPassword the plain text password to check; must not be null or empty.
     * @param passwordEncoder the PasswordEncoder instance to use for password comparison; must not be null.
     * @return true if the raw password matches the stored hashed password, otherwise false.
     */
    public boolean isPasswordCorrect(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.passwordHash);
    }

    /**
     * Validates the provided username based on specific business rules.
     * Ensures the username is not null or empty, and that its length
     * falls within the allowed range (3 to 50 characters).
     *
     * @param username the username to validate; it must not be null or empty
     *                 and must have a length between 3 and 50 characters
     * @throws IllegalArgumentException if the username does not meet the specified criteria
     */
    private void validateUsername(String username) {
        Assert.hasText(username, "username must not be empty");
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
    }

    /**
     * Validates the provided email string to ensure it is non-empty and follows a valid email format.
     * If the validation fails, an exception is thrown.
     *
     * @param email the email string to validate; must not be empty and must conform to the standard email pattern.
     *              Throws {@link InvalidDataException} if the email format is invalid.
     */
    private void validateEmail(String email) {
        Assert.hasText(email, "email must not be empty");
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidDataException("Formato de email inválido!");
        }
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isEnabled() { return enabled; }
    public boolean isLocked() { return locked; }
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
