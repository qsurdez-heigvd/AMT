package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity(name = "rental")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rental_id;

    @Column(name = "rental_date", nullable = false)
    private Instant rental_date;

    @ManyToOne
    @JoinColumn(
            name = "inventory_id",
            foreignKey = @ForeignKey(name = "rental_inventory_id_fkey"),
            nullable = false
    )
    private Inventory inventory;

    @ManyToOne
    @JoinColumn(
            name = "customer_id",
            foreignKey = @ForeignKey(name = "rental_customer_id_fkey"),
            nullable = false
    )
    private Customer customer;

    @Column(name = "return_date")
    private Instant return_date;

    @ManyToOne
    @JoinColumn(
            name = "staff_id",
            foreignKey = @ForeignKey(name = "rental_staff_id_fkey"),
            nullable = false
    )
    private Staff staff;
}
