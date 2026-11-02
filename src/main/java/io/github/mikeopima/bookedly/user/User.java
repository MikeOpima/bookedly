package io.github.mikeopima.bookedly.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * An account that can sign in. "user" is a reserved word in most databases,
 * so the table is called "users".
 */
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Always stored lower-cased so lookups are case-insensitive. */
	@Column(nullable = false, unique = true, length = 254)
	private String email;

	/** BCrypt hash. The raw password is never stored. */
	@Column(nullable = false)
	private String passwordHash;

	@Column(nullable = false, length = 100)
	private String fullName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Role role = Role.CUSTOMER;

	@Column(nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	protected User() {
		// for JPA
	}

	public User(String email, String passwordHash, String fullName) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.fullName = fullName;
	}

	public Long getId() { return id; }
	public String getEmail() { return email; }
	public String getPasswordHash() { return passwordHash; }
	public String getFullName() { return fullName; }
	public Role getRole() { return role; }
	public Instant getCreatedAt() { return createdAt; }
}
