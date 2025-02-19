package ch.heig.amt.vineward.business.model;

import ch.heig.amt.vineward.business.model.review.Review;
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
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Wine entity.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Entity
@Table(name = "wine")
@Getter
@Setter
@Accessors(chain = true)
public class Wine {

    // ----- ID COLUMN
    // > wine_id INTEGER NOT NULL UNIQUE GENERATED ALWAYS AS IDENTITY PRIMARY KEY
    @Id
    @Column(name = "wine_id", insertable = false, updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    // ----- NAME COLUMN
    // > name CHARACTERS VARYING(100) NOT NULL
    @Column(name = "name", length = ColumnSize.WINE_NAME_LENGTH, nullable = false)
    private String name;

    // ----- VINTNER COLUMN
    // > vintner CHARACTERS VARYING(50) NOT NULL
    @Column(name = "vintner", length = ColumnSize.WINE_VINTNER_LENGTH, nullable = false)
    private String vintner;

    // ----- ORIGIN COLUMN
    // > origin CHARACTERS VARYING(32) NOT NULL
    @Enumerated(EnumType.STRING)
    @Column(name = "origin", length = ColumnSize.CANTON_LENGTH, nullable = false)
    private Canton origin;

    // ----- REGION COLUMN
    // > region CHARACTERS VARYING(24) NOT NULL
    @Enumerated(EnumType.STRING)
    @Column(name = "region", length = ColumnSize.REGION_LENGTH, nullable = false)
    private Region region;

    // ----- VARIETY COLUMN
    // > wine_variety(wine_fk INTEGER NOT NULL, variety CHARACTERS VARYING(32) NOT NULL)
    // > FOREIGN KEY(wine_fk) REFERENCES wine(wine_id)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "variety", length = ColumnSize.WINE_VARIETY_LENGTH, nullable = false)
    @JoinTable(name = "wine_variety", joinColumns = @JoinColumn(
        name = "wine_fk",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_wine_variety__wine")
    ))
    private Set<WineVariety> varieties;

    // ----- REVIEWS COLLECTION
    @OneToMany(mappedBy = "wine", fetch = FetchType.LAZY)
    private Set<Review> reviews;
}
