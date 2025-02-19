package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;

@Entity(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer store_id;

    @ManyToOne
    @JoinColumn(
            name = "manager_staff_id",
            foreignKey = @ForeignKey(name = "store_manager_staff_id_fkey"),
            nullable = false
    )
    private Staff manager;

    @ManyToOne
    @JoinColumn(
            name = "address_id",
            foreignKey = @ForeignKey(name = "store_address_id_fkey"),
            nullable = false
    )
    private Address address;
}
