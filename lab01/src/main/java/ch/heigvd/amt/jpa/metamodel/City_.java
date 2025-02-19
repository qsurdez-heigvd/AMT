package ch.heigvd.amt.jpa.metamodel;

import ch.heigvd.amt.jpa.entity.City;
import ch.heigvd.amt.jpa.entity.Country;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.time.Instant;

@StaticMetamodel(City.class)
public class City_ {
    public static volatile SingularAttribute<City, Long> id;
    public static volatile SingularAttribute<City, String> city;
    public static volatile SingularAttribute<City, Country> country;
    public static volatile SingularAttribute<City, Instant> last_update;
}
