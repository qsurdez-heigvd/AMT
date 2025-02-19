package ch.heigvd.amt.jpa.service;

import ch.heigvd.amt.jpa.entity.Rental;
import ch.heigvd.amt.jpa.metamodel.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

/**
 * Exercise Country by rentals.
 * Signature of methods (countryRentals_*) must not be changed.
 */
@ApplicationScoped
public class CountryRentalsService {

  @Inject
  private EntityManager em;

  public record CountryRentals(String country, Long rentals) {
  }

  public List<CountryRentals> countryRentals_NativeSQL() {
    var query = """
            SELECT c.country, COUNT(r.rental_id) AS rentals
            FROM country c
                JOIN city ci ON c.country_id = ci.country_id
                JOIN address a ON ci.city_id = a.city_id
                JOIN customer cu ON a.address_id = cu.address_id
                JOIN rental r ON cu.customer_id = r.customer_id
            GROUP BY c.country
            ORDER BY rentals DESC;
            """;
    List<Object[]> rawResults = em.createNativeQuery(query).getResultList();
    return rawResults.stream()
            .map(r -> new CountryRentals(
                    (String) r[0],
                    ((Number) r[1]).longValue()
            )).toList();
  }

  public List<CountryRentals> countryRentals_JPQL() {
    var query = """
            SELECT c.country, COUNT(r.rental_id) AS rentals
            FROM rental r
                JOIN r.customer cu
                JOIN cu.address a
                JOIN a.city ci
                JOIN ci.country c
            GROUP BY c.country
            ORDER BY rentals DESC
            """;
    return em.createQuery(query, CountryRentals.class).getResultList();
  }

  public List<CountryRentals> countryRentals_CriteriaString() {
    var cb = em.getCriteriaBuilder();
    var query = cb.createQuery(CountryRentals.class);
    var rentals = query.from(ch.heigvd.amt.jpa.entity.Rental.class);
    var customer = rentals.join("customer");
    var address = customer.join("address");
    var city = address.join("city");
    var country = city.join("country");
    query.multiselect(
            country.get("country"),
            cb.count(rentals.get("rental_id"))
    );
    query.groupBy(country.get("country"));
    query.orderBy(cb.desc(cb.count(rentals.get("rental_id"))));
    return em.createQuery(query).getResultList();
  }

  public List<CountryRentals> countryRentals_CriteriaMetaModel() {
    var cb = em.getCriteriaBuilder();
    var query = cb.createQuery(Object[].class);
    var rentals = query.from(Rental.class);
    var customer = rentals.join(Rental_.customer);
    var address = customer.join(Customer_.address);
    var city = address.join(Address_.city.toString());
    var country = city.join(City_.country.toString());
    query.multiselect(
            country.get(Country_.country.toString()),
            cb.count(rentals.get(Rental_.id))
    );
    query.groupBy(country.get(Country_.country.toString()));
    query.orderBy(cb.desc(cb.count(rentals.get(Rental_.id))));

    return em.createQuery(query).getResultList().stream()
            .map(r -> new CountryRentals(
                    (String) r[0],
                    ((Number) r[1]).longValue()
            )).toList();
  }
}
