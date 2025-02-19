package ch.heigvd.amt.jpa.repository;

import ch.heigvd.amt.jpa.entity.City;
import ch.heigvd.amt.jpa.entity.Country;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CityRepository {

    @Inject
    EntityManager em;

    private CityDTO fromEntityToDTO(City city) {
        if (city == null) {
            return null;
        }

        return new CityDTO(city.getId(), city.getCity(), city.getCountry().getCountry());
    }

    public CityDTO read(Integer id) {
        City city = em.find(City.class, id);
        return this.fromEntityToDTO(city);
    }

    @Transactional
    public Integer create(String city, String country) {
        City cityEntity = new City();
        cityEntity.setCity(city);
        Country countryEntity;

        // TODO : NamedQueries
        try {
            countryEntity = em.createQuery("SELECT c FROM country c WHERE trim(lower(c.country)) LIKE lower(:country)", Country.class)
                    .setParameter("country", country)
                    .getSingleResult();
        } catch (Exception e) {
            throw new IllegalArgumentException("Country with name " + country + " does not exist");
        }

        cityEntity.setCountry(countryEntity);

        em.persist(cityEntity);
        return cityEntity.getId();
    }

    @Transactional
    public void update(Integer id, String city, String country) {
        City cityEntity = em.find(City.class, id);
        Country countryEntity;

        if (cityEntity == null) {
            throw new IllegalArgumentException("City with id " + id + " does not exist");
        }

        cityEntity.setCity(city);

        try {
            countryEntity = em.createQuery("SELECT c FROM country c WHERE trim(lower(c.country)) LIKE lower(:country)", Country.class)
                    .setParameter("country", country)
                    .getSingleResult();
        } catch (Exception e) {
            throw new IllegalArgumentException("Country with name " + country + " does not exist");
        }

        cityEntity.setCountry(countryEntity);

        em.merge(cityEntity);
    }

    @Transactional
    public void delete(Integer id) {
        City cityEntity = em.find(City.class, id);

        if (cityEntity == null) {
            throw new IllegalArgumentException("City with id " + id + " does not exist");
        }

        em.remove(cityEntity);
    }

    public record CityDTO (Integer id, String city, String country) {
    }
}
