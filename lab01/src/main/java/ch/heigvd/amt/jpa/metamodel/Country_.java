package ch.heigvd.amt.jpa.metamodel;

import ch.heigvd.amt.jpa.entity.Country;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.time.Instant;

@StaticMetamodel(Country.class)
public class Country_{
    public static volatile SingularAttribute<Country, Long> id;
    public static volatile SingularAttribute<Country, String> country;
    public static volatile SingularAttribute<Country, Instant> last_update;
}
