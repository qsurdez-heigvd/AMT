package ch.heig.amt.vineward.business.model.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * User token holder.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Entity
@Table(name = "app_user_token")
@Getter
@Setter
@Accessors(chain = true)
public class UserToken {

    // ----- ID COLUMN
    // > user_token_id INTEGER NOT NULL UNIQUE PRIMARY KEY
    @Id
    @Column(name = "user_token_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ----- USER FOREIGN KEY
    // > app_user_fk INTEGER NOT NULL FOREIGN KEY REFERENCES app_user(user_id)
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, optional = false)
    @JoinColumn(name = "app_user_fk", nullable = false, foreignKey = @ForeignKey(name = "fk_token__user"))
    private User user;

    // ----- TOKEN COLUMN
    // > token CHARACTERS VARYING(255) NOT NULL
    @Column(name = "token", nullable = false)
    private String token;

    // ----- REFRESH TOKEN COLUMN
    // > refresh_token CHARACTERS VARYING(255) NOT NULL
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    // ----- EXPIRED COLUMN
    // > expired BOOLEAN NOT NULL
    @Column(name = "expired", nullable = false)
    private boolean expired = false;

    // ----- REVOKED COLUMN
    // > revoked BOOLEAN NOT NULL
    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    public void revoke() {
        this.expired = true;
        this.revoked = true;
    }
}
