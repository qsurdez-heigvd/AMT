package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customer_id;

    @ManyToOne
    @JoinColumn(
            name = "store_id",
            foreignKey = @ForeignKey(name = "customer_store_id_fkey"),
            nullable = false
    )
    private Store store;

    @Column(name = "first_name", length = 45, nullable = false)
    private String first_name;

    @Column(name = "last_name", length = 45, nullable = false)
    private String last_name;

    @Column(name = "email", length = 50)
    private String email;

    @ManyToOne
    @JoinColumn(
            name = "address_id",
            foreignKey = @ForeignKey(name = "customer_address_id_fkey"),
            nullable = false
    )
    private Address address;

    @Column(name = "activebool", nullable = false, columnDefinition = "DEFAULT true")
    private Boolean activebool;

    @Column(name = "create_date", nullable = false, columnDefinition = "DATE DEFAULT ('now'::text)::date")
    private LocalDate create_date;

    @Column(name = "active", nullable = false)
    private Integer active;
}
