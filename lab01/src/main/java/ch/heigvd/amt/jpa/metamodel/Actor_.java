package ch.heigvd.amt.jpa.metamodel;

import ch.heigvd.amt.jpa.entity.Actor;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Actor.class)
public class Actor_ {
    public static volatile SingularAttribute<Actor, String> firstName;
    public static volatile SingularAttribute<Actor, String> lastName;
}
