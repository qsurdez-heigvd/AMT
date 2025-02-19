package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payment_id;

    @ManyToOne
    @JoinColumn(
            name = "customer_id",
            foreignKey = @ForeignKey(name = "payment_customer_id_fkey"),
            nullable = false
    )
    private Customer customer;

    @ManyToOne
    @JoinColumn(
            name = "staff_id",
            foreignKey = @ForeignKey(name = "payment_staff_id_fkey"),
            nullable = false
    )
    private Staff staff;

    @ManyToOne
    @JoinColumn(
            name = "rental_id",
            foreignKey = @ForeignKey(name = "payment_rental_id_fkey"),
            nullable = false
    )
    private Rental rental;

    @Column(name = "amount", precision = 5, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private Instant payment_date;
}
