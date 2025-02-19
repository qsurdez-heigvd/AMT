package ch.heigvd.amt.jpa.metamodel;

import ch.heigvd.amt.jpa.entity.Customer;
import ch.heigvd.amt.jpa.entity.Payment;
import ch.heigvd.amt.jpa.entity.Rental;
import ch.heigvd.amt.jpa.entity.Store;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.time.Instant;
import java.util.List;

@StaticMetamodel(Customer.class)
public class Customer_ {
    public static volatile SingularAttribute<Customer, Long> id;
    public static volatile SingularAttribute<Customer, String> firstName;
    public static volatile SingularAttribute<Customer, String> lastName;
    public static volatile SingularAttribute<Customer, String> email;
    public static volatile SingularAttribute<Customer, String> address;
    public static volatile SingularAttribute<Customer, String> postalCode;
    public static volatile SingularAttribute<Customer, String> phone;
    public static volatile SingularAttribute<Customer, String> city;
    public static volatile SingularAttribute<Customer, String> country;
    public static volatile SingularAttribute<Customer, Integer> active;
    public static volatile SingularAttribute<Customer, Instant> createDate;
    public static volatile SingularAttribute<Customer, Instant> lastUpdate;
    public static volatile SingularAttribute<Customer, Store> store;
    public static volatile SingularAttribute<Customer, List<Rental>> rentals;
    public static volatile SingularAttribute<Customer, List<Payment>> payments;
}
