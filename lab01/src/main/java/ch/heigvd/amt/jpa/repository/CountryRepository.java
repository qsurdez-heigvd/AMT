package ch.heigvd.amt.jpa.repository;

import ch.heigvd.amt.jpa.entity.Country;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CountryRepository {

    @Inject
    EntityManager em;

    private CountryDTO fromEntityToDTO(Country country) {
        if (country == null) {
            return null;
        }

        return new CountryDTO(country.getId(), country.getCountry());
    }

    public CountryDTO read(Integer id) {
        Country country = em.find(Country.class, id);
        return this.fromEntityToDTO(country);
    }

    @Transactional
    public Integer create(String country) {
        Country countryEntity = new Country();
        countryEntity.setCountry(country);

        em.persist(countryEntity);
        return countryEntity.getId();
    }

    @Transactional
    public void update(Integer id, String country) {
        Country countryEntity = em.find(Country.class, id);

        if (countryEntity == null) {
            throw new IllegalArgumentException("Country with id " + id + " does not exist");
        }

        countryEntity.setCountry(country);

        em.merge(countryEntity);
    }

    @Transactional
    public void delete(Integer id) {
        Country countryEntity = em.find(Country.class, id);

        if (countryEntity == null) {
            throw new IllegalArgumentException("Country with id " + id + " does not exist");
        }

        em.remove(countryEntity);
    }


    public record CountryDTO(Integer id, String country) {
    }
}
