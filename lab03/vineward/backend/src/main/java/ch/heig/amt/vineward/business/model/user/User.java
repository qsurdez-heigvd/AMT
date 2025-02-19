package ch.heig.amt.vineward.business.model.user;

import ch.heig.amt.vineward.business.model.Canton;
import ch.heig.amt.vineward.business.model.ColumnSize;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User entity.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Entity
@Table(name = "app_user", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_user_username", columnNames = "username"),
})
@Getter
@Setter
@Accessors(chain = true)
public class User implements UserDetails {

    // ----- ID COLUMN
    // > user_id UUID NOT NULL UNIQUE PRIMARY KEY
    @Id
    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    // ----- USERNAME COLUMN
    // > username CHARACTER VARYING(48) UNIQUE NOT NULL
    @Column(name = "username", length = ColumnSize.USERNAME_LENGTH, nullable = false)
    public String displayName;

    // ----- EMAIL COLUMN
    // > email CHARACTER VARYING(255) UNIQUE NOT NULL
    @Column(name = "email", nullable = false)
    public String email;

    // ----- PASSWORD COLUMN
    // > password CHARACTER VARYING(324) UNIQUE NOT NULL
    @Column(name = "password", length = ColumnSize.PASSWORD_LENGTH, nullable = false)
    public String password;

    // ----- USER GRANTED AUTHORITIES
    // > user_role(user_fk UUID, user_role CHARACTER VARYING(255) NOT NULL)
    // > FOREIGN KEY(user_fk) REFERENCES app_user(user_id)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", length = ColumnSize.ROLE_LENGTH, nullable = false)
    @JoinTable(name = "app_user_role", joinColumns = @JoinColumn(
        name = "user_fk",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_user_role__user")
    ))
    private Set<UserRole> authorities;

    // ----- ORIGIN COLUMN
    // > origin CHARACTERS VARYING(255) NOT NULL
    @Enumerated(EnumType.STRING)
    @Column(name = "origin", length = ColumnSize.CANTON_LENGTH, nullable = false)
    public Canton origin;

    // ----- TOKENS COLLECTION
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserToken> tokens;

    public User addToken(UserToken token) {
        if (Objects.isNull(tokens)) {
            tokens = new HashSet<>();
        }

        token.setUser(this);
        tokens.add(token);

        return this;
    }

    /**
     * @return the user email address
     * @apiNote for Spring security, we consider the username to be the email address and not the
     *     display name
     */
    @Override
    @Transient
    public String getUsername() {
        return email;
    }
}
