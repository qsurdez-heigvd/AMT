package ch.heigvd.amt.jpa.entity;


import jakarta.persistence.*;

import java.util.List;

@Entity(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id")
    private Integer id;

    @Column(name = "country", length = 50, nullable = false)
    private String country;

    public Country() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", country='" + country + '\'' +
                '}';
    }
}
