package ch.heig.amt.vineward.business.model.review;

import ch.heig.amt.vineward.business.model.ColumnSize;
import ch.heig.amt.vineward.business.model.Wine;
import ch.heig.amt.vineward.business.model.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Review entity.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Entity
@Table(name = "review")
@Getter
@Setter
@Accessors(chain = true)
public class Review {

    // ----- ID COLUMN
    // > review_id INTEGER NOT NULL UNIQUE GENERATED ALWAYS AS IDENTITY PRIMARY KEY
    @Id
    @Column(name = "review_id", insertable = false, updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    // ----- WINE FOREIGN KEY
    // > wine_fk INTEGER NOT NULL FOREIGN KEY REFERENCES wine(wine_id)
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, optional = false)
    @JoinColumn(name = "wine_fk", nullable = false, foreignKey = @ForeignKey(name = "fk_review__wine"))
    private Wine wine;

    // ----- TITLE COLUMN
    // > title CHARACTER VARYING(250) NOT NULL
    @Column(name = "title", length = ColumnSize.POST_TITLE_LENGTH, nullable = false)
    private String title;

    // ----- BODY COLUMN
    // > body TEXT NOT NULL
    @Column(name = "body", columnDefinition = "TEXT", nullable = false)
    private String body;

    // ----- STATUS COLUMN
    // > status CHARACTERS VARYING(255) NOT NULL
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = ColumnSize.POST_STATUS_LENGTH, nullable = false)
    private ReviewStatus status;

    // ----- AUTHOR FOREIGN KEY
    // > author_fk INTEGER NOT NULL FOREIGN KEY REFERENCES app_user(user_id)
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, optional = false)
    @JoinColumn(name = "author_fk", nullable = false, foreignKey = @ForeignKey(name = "fk_review__author"))
    private User author;
}
