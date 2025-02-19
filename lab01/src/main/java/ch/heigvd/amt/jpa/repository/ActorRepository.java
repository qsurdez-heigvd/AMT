package ch.heigvd.amt.jpa.repository;

import ch.heigvd.amt.jpa.entity.Actor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ActorRepository {
    @Inject
    private EntityManager em;

    private ActorDTO fromEntityToDTO(Actor actor) {
        if (actor == null) {
            return null;
        }

        return new ActorDTO(actor.getId(), actor.getFirstName(), actor.getLastName());
    }

    public record ActorDTO (Integer id, String firstName, String lastName) {
    }

    @Transactional
    public ActorDTO read(Integer id) {
        Actor actor = em.find(Actor.class, id);
        return this.fromEntityToDTO(actor);
    }

    @Transactional
    public Integer create(String firstname, String lastname) {
        Actor actor = new Actor();
        actor.setFirstName(firstname);
        actor.setLastName(lastname);

        em.persist(actor);
        return actor.getId();
    }

    @Transactional
    public void update(Integer id, String firstName, String lastName) {
        Actor actor = em.find(Actor.class, id);

        if (actor == null) {
            throw new IllegalArgumentException("Actor with id " + id + " does not exist");
        }

        actor.setFirstName(firstName);
        actor.setLastName(lastName);

        em.merge(actor);
    }

    @Transactional
    public void delete(Integer id) {
        Actor actor = em.find(Actor.class, id);

        if (actor == null) {
            throw new IllegalArgumentException("Actor with id " + id + " does not exist");
        }
        em.remove(actor);
    }
}
