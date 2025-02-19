package ch.heigvd.amt.jpa.repository;

import ch.heigvd.amt.jpa.entity.Address;
import ch.heigvd.amt.jpa.entity.City;
import ch.heigvd.amt.jpa.entity.Country;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AddressRepository {

    @Inject
    EntityManager em;

    private AddressDTO fromEntityToDTO(Address address) {
        if (address == null) {
            return null;
        }

        return new AddressDTO(address.getId(), address.getAddress(), address.getDistrict(), address.getCity().getCity(), address.getPhone());
    }

    public AddressDTO read(Integer id) {
        Address address = em.find(Address.class, id);
        return this.fromEntityToDTO(address);
    }

    @Transactional
    public Integer create(String address, String district, String city, String phone) {
        Address addressEntity = new Address();
        addressEntity.setAddress(address);
        addressEntity.setDistrict(district);
        addressEntity.setPhone(phone);
        City cityEntity;

        try {
            cityEntity = em.createQuery("SELECT c FROM city c WHERE trim(lower(c.city)) LIKE lower(:city)", City.class)
                    .setParameter("city", city)
                    .getSingleResult();
        } catch (Exception e) {
            throw new IllegalArgumentException("City with name " + city + " does not exist");
        }

        addressEntity.setCity(cityEntity);

        em.persist(addressEntity);
        return addressEntity.getId();
    }

    @Transactional
    public void update(Integer id, String address, String district, String city, String phone) {
        Address addressEntity = em.find(Address.class, id);
        addressEntity.setAddress(address);
        addressEntity.setDistrict(district);
        addressEntity.setPhone(phone);
        City cityEntity;

        if (addressEntity == null) {
            throw new IllegalArgumentException("Address with id " + id + " does not exist");
        }

        try {
            cityEntity = em.createQuery("SELECT c FROM city c WHERE trim(lower(c.city)) LIKE lower(:city)", City.class)
                    .setParameter("city", city)
                    .getSingleResult();
        } catch (Exception e) {
            throw new IllegalArgumentException("City with name " + city + " does not exist");
        }

        addressEntity.setCity(cityEntity);

        em.merge(cityEntity);
    }

    @Transactional
    public void delete(Integer id) {
        Address addressEntity = em.find(Address.class, id);

        if (addressEntity == null) {
            throw new IllegalArgumentException("Address with id " + id + " does not exist");
        }

        em.remove(addressEntity);
    }


    public record AddressDTO(Integer id, String address, String district, String city, String phone) {}

}
