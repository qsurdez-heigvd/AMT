package ch.heigvd.amt.jpa.entity;


import jakarta.persistence.*;

@Entity(name = "city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    private Integer id;

    @Column(name = "city", length = 50, nullable = false)
    private String city;

    @ManyToOne
    @JoinColumn(
            name = "country_id",
            foreignKey = @ForeignKey(name = "fk_country__city"),
            nullable = false)
    private Country country;

    public City() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", country=" + country +
                '}';
    }

}
