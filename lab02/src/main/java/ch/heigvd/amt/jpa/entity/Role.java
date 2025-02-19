package ch.heigvd.amt.jpa.entity;

import io.quarkus.security.jpa.Roles;
import jakarta.persistence.*;

import java.util.List;

@Entity(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Roles
    @Column(name = "role")
    private String role;

    @ManyToMany(mappedBy = "roles")
    private List<Staff> staff;

    public Role() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Staff> getStaff() {
        return staff;
    }

    public void setStaff(List<Staff> staff) {
        this.staff = staff;
    }
}
