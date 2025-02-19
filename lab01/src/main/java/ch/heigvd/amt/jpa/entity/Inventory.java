package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;

@Entity(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer inventory_id;

    @ManyToOne
    @JoinColumn(
            name = "film_id",
            foreignKey = @ForeignKey(name = "inventory_film_id_fkey"),
            nullable = false
    )
    private Film film;

    @ManyToOne
    @JoinColumn(
            name = "store_id",
            foreignKey = @ForeignKey(name = "inventory_store_id_fkey"),
            nullable = false
    )
    private Store store;
}
