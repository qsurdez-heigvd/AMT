package ch.heigvd.amt.jpa.entity;


import jakarta.persistence.*;

@Entity(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer id;

    @Column(name = "address", length = 50, nullable = false)
    private String address;

    @Column(name = "address2", length = 50, nullable = true)
    private String address2;

    @Column(name = "district", length = 20, nullable = false)
    private String district;

    @ManyToOne
    @JoinColumn(
            name = "city_id",
            foreignKey = @ForeignKey(name = "fk_city__address"),
            nullable = false
    )
    private City city;

    @Column(name = "postal_code", length = 10, nullable = true)
    private String postalCode;

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    public Address() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", address2='" + address2 + '\'' +
                ", district='" + district + '\'' +
                ", city=" + city +
                ", postalCode='" + postalCode + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

}
