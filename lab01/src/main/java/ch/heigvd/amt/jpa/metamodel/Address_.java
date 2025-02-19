package ch.heigvd.amt.jpa.metamodel;

import ch.heigvd.amt.jpa.entity.Address;
import ch.heigvd.amt.jpa.entity.Customer;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Address_.class)
public class Address_ {
    public static volatile SingularAttribute<Address, Long> id;
    public static volatile SingularAttribute<Address, String> address;
    public static volatile SingularAttribute<Address, String> postalCode;
    public static volatile SingularAttribute<Address, String> phone;
    public static volatile SingularAttribute<Address, String> city;
    public static volatile SingularAttribute<Address, String> country;
    public static volatile SingularAttribute<Address, Customer> customer;
}
