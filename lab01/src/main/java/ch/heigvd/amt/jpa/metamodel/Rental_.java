package ch.heigvd.amt.jpa.metamodel;

import ch.heigvd.amt.jpa.entity.*;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.time.Instant;

@StaticMetamodel(Rental.class)
public class Rental_ {
    public static volatile SingularAttribute<Rental, Long> id;
    public static volatile SingularAttribute<Rental, Instant> rental_date;
    public static volatile SingularAttribute<Rental, Instant> return_date;
    public static volatile SingularAttribute<Rental, Instant> last_update;
    public static volatile SingularAttribute<Rental, Staff> staff;
    public static volatile SingularAttribute<Rental, Customer> customer;
    public static volatile SingularAttribute<Rental, Inventory> inventory;
    public static volatile SingularAttribute<Rental, Payment> payment;
}
