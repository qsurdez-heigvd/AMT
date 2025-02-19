package ch.heigvd.amt.jpa.service;

import ch.heigvd.amt.jpa.entity.Film;
import ch.heigvd.amt.jpa.entity.enums.Rating;
import ch.heigvd.amt.jpa.metamodel.Actor_;
import ch.heigvd.amt.jpa.metamodel.Film_;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * Exercise Actors with films of PG rating.
 * Signature of methods (actorInPGRatings_*) must not be changed.
 */
@ApplicationScoped
public class ActorsInPGService {

  @Inject
  private EntityManager em;

  public record ActorInPGRating(String firstName, String lastName, Long nbFilms) {
  }

  public List<ActorInPGRating> actorInPGRatings_NativeSQL() {
    var query = """
            SELECT a.actor_id, a.first_name, a.last_name, COUNT(f.film_id) AS nb_films
            FROM actor a
                     JOIN film_actor fa ON a.actor_id = fa.actor_id
                     JOIN film f ON fa.film_id = f.film_id
            WHERE f.rating = 'PG'
            GROUP BY a.actor_id, a.first_name, a.last_name
            ORDER BY nb_films DESC, a.first_name, a.last_name, a.actor_id;
            """;
    List<Object[]> rawResults = em.createNativeQuery(query).getResultList();

    return rawResults.stream()
            .map(r -> new ActorInPGRating(
                    (String) r[1],
                    (String) r[2],
                    ((Number) r[3]).longValue()
            )).toList();
  }

  public List<ActorInPGRating> actorInPGRatings_JPQL() {
    TypedQuery<Object[]> query = em.createQuery("""
        SELECT a.firstName, a.lastName, COUNT(f.id)
        FROM film f
            JOIN f.actors a
        WHERE f.rating = 'PG'
        GROUP BY a.id, a.firstName, a.lastName
        ORDER BY COUNT(f.id) DESC, a.firstName, a.lastName, a.id
        """, Object[].class);

    return query.getResultList().stream()
            .map(r -> new ActorInPGRating(
                    (String) r[0],
                    (String) r[1],
                    ((Number) r[2]).longValue()
            )).toList();
  }


  public List<ActorInPGRating> actorInPGRatings_CriteriaString() {
    var cb = em.getCriteriaBuilder();
    var query = cb.createQuery(ActorInPGRating.class);
    var films = query.from(Film.class);
    var actors = films.join("actors");
    query.multiselect(
            actors.get("firstName"),
            actors.get("lastName"),
            cb.count(films.get("id"))
    );
    query.where(cb.equal(films.get("rating"), Rating.PARENTAL_GUIDANCE_SUGGESTED));
    query.groupBy(actors.get("id"), actors.get("firstName"), actors.get("lastName"));
    query.orderBy(cb.desc(cb.count(films.get("id"))), cb.asc(actors.get("firstName")), cb.asc(actors.get("lastName")), cb.asc(actors.get("id")));

    return em.createQuery(query).getResultList();
  }

  public List<ActorInPGRating> actorInPGRatings_CriteriaMetaModel() {
    var cb = em.getCriteriaBuilder();
    var query = cb.createQuery(Object[].class);
    var films = query.from(Film.class);
    var actors = films.join(Film_.actors);
    query.multiselect(
            actors.get(Actor_.firstName),
            actors.get(Actor_.lastName),
            cb.count(films.get(Film_.id))
    );
    query.where(cb.equal(films.get(Film_.rating), Rating.PARENTAL_GUIDANCE_SUGGESTED));
    query.groupBy(actors.get(Actor_.firstName), actors.get(Actor_.lastName));
    query.orderBy(cb.desc(cb.count(films.get(Film_.id))));

    return em.createQuery(query).getResultList().stream()
            .map(r -> new ActorInPGRating(
                    (String) r[0],
                    (String) r[1],
                    ((Number) r[2]).longValue()
            )).toList();
  }
}
