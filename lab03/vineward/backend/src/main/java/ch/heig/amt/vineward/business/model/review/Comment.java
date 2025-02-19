package ch.heig.amt.vineward.business.model.review;

import ch.heig.amt.vineward.business.model.user.User;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Review comment entity.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Entity
@Table(name = "review_comment")
@Getter
@Setter
@Accessors(chain = true)
public class Comment {

    // ----- ID COLUMN
    // > comment_id INTEGER NOT NULL UNIQUE GENERATED ALWAYS AS IDENTITY PRIMARY KEY
    @Id
    @Column(name = "comment_id", insertable = false, updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    // ----- BODY COLUMN
    // > body TEXT NOT NULL
    @Column(name = "body", columnDefinition = "TEXT", nullable = false)
    private String body;

    // ----- REVIEW FOREIGN KEY
    // > review_fk INTEGER NOT NULL FOREIGN KEY REFERENCES review(review_id)
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, optional = false)
    @JoinColumn(name = "review_fk", nullable = false, foreignKey = @ForeignKey(name = "fk_comment__review"))
    private Review review;

    // ----- AUTHOR FOREIGN KEY
    // > author_fk INTEGER NOT NULL FOREIGN KEY REFERENCES app_user(user_id)
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, optional = false)
    @JoinColumn(name = "author_fk", nullable = false, foreignKey = @ForeignKey(name = "fk_comment__author"))
    private User author;
}
