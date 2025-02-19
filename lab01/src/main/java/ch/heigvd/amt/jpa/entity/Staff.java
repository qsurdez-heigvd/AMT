package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;

@Entity(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer staff_id;

    @Column(name = "first_name", length = 45, nullable = false)
    private String first_name;

    @Column(name = "last_name", length = 45, nullable = false)
    private String last_name;

    @ManyToOne
    @JoinColumn(
            name = "address_id",
            foreignKey = @ForeignKey(name = "staff_address_id_fkey"),
            nullable = false
    )
    private Address address;

    @Column(name = "email", length = 50)
    String email;

    @ManyToOne
    @JoinColumn(
            name = "store_id",
            foreignKey = @ForeignKey(name = "staff_store_id_fkey"),
            nullable = false
    )
    private Store store;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "username", length = 16, nullable = false)
    private String username;

    @Column(name = "password", length = 40)
    private String password;

    @Column(name = "picture")
    private byte[] picture;

}
