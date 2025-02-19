package ch.heigvd.amt.jpa.entity;


import ch.heigvd.amt.jpa.entity.enums.Rating;
import ch.heigvd.amt.jpa.types.EnumTypePostgreSql;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import java.util.List;


@Entity(name = "film")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    private Integer id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description",  nullable = true)
    private String description;

    @Column(name = "release_year", columnDefinition = "int4", nullable = true)
    private Integer releaseYear;

    @ManyToOne
    @JoinColumn(
            name = "language_id",
            foreignKey = @ForeignKey(name = "fk_language__film"),
            nullable = false
    )
    private Language language;

    @ManyToOne
    @JoinColumn(
            name = "original_language_id",
            foreignKey = @ForeignKey(name = "fk_original_language__film"),
            nullable = true
    )
    private Language originalLanguage;

    @Column(name = "rental_duration", nullable = false, columnDefinition = "int2")
    // This annotation is used by hibernate and is not part of the JPA specification
    @ColumnDefault("3")
    private Integer rentalDuration;

    @Column(name = "rental_rate", nullable = false, columnDefinition = "numeric(4,2)")
    // This annotation is used by hibernate and is not part of the JPA specification
    @ColumnDefault("4.99")
    private Double rentalRate;

    @Column(name = "length", columnDefinition = "int2", nullable = true)
    private Integer length;

    @Column(name = "replacement_cost", nullable = false, columnDefinition = "numeric(5,2)")
    // This annotation is used by hibernate and is not part of the JPA specification
    @ColumnDefault("19.99")
    private Double replacementCost;

    @Column(name = "rating", nullable = true)
    @Type(
            value = EnumTypePostgreSql.class
    )
    private Rating rating;

    @Column(name = "special_features", nullable = true, columnDefinition = "text[]")
    private String[] specialFeatures;

    @ManyToMany
    @JoinTable(
            name = "film_actor",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors;

    @PrePersist
    public void prePersist() {
        if (rentalDuration == null) {
            rentalDuration = 3;
        }

        if (rentalRate == null) {
            rentalRate = 4.99;
        }

        if (replacementCost == null) {
            replacementCost = 19.99;
        }

    }

    public Film() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Language getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(Language originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public Integer getRentalDuration() {
        return rentalDuration;
    }

    public void setRentalDuration(Integer rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public Double getRentalRate() {
        return rentalRate;
    }

    public void setRentalRate(Double rentalRate) {
        this.rentalRate = rentalRate;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Double getReplacementCost() {
        return replacementCost;
    }

    public void setReplacementCost(Double replacementCost) {
        this.replacementCost = replacementCost;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String[] getSpecialFeatures() {
        return specialFeatures;
    }

    public void setSpecialFeatures(String[] specialFeatures) {
        this.specialFeatures = specialFeatures;
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", releaseYear='" + releaseYear + '\'' +
                ", language=" + language +
                ", originalLanguage=" + originalLanguage +
                ", rentalDuration=" + rentalDuration +
                ", rentalRate=" + rentalRate +
                ", length=" + length +
                ", replacementCost=" + replacementCost +
                ", rating=" + rating +
                ", specialFeatures=" + specialFeatures +
                '}';
    }

}

