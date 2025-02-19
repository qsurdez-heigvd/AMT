package ch.heigvd.amt.jpa.metamodel;

import ch.heigvd.amt.jpa.entity.Actor;
import ch.heigvd.amt.jpa.entity.Film;
import ch.heigvd.amt.jpa.entity.enums.Rating;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Film.class)
public class Film_ {
    public static volatile SingularAttribute<Film, Long> id;
    public static volatile SingularAttribute<Film, Rating> rating;
    public static volatile ListAttribute<Film, Actor> actors;
}
