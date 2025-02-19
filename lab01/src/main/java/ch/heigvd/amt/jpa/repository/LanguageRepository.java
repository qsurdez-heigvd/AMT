package ch.heigvd.amt.jpa.repository;

import ch.heigvd.amt.jpa.entity.Language;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class LanguageRepository {

    @Inject
    private EntityManager em;

    private LanguageDTO fromEntityToDTO(Language language) {
        if (language == null) {
            return null;
        }

        return new LanguageDTO(language.getId(), language.getName());
    }

    public LanguageDTO read(Integer id) {
        Language language = em.find(Language.class, id);
        return this.fromEntityToDTO(language);
    }

    /*
    public LanguageDTO readByName(String name) {
        Language language = em.createQuery("SELECT l FROM language l WHERE l.name LIKE :name", Language.class)
                .setParameter("name", name + "%")
                .getSingleResult();

        return this.fromEntityToDTO(language);
    }
    */

    @Transactional
    public Integer create(String name) {
        Language language = new Language();
        language.setName(name);

        em.persist(language);
        return language.getId();
    }

    @Transactional
    public void update(Integer id, String name) {
        Language language = em.find(Language.class, id);

        if (language == null) {
            throw new IllegalArgumentException("Language with id " + id + " does not exist");
        }

        language.setName(name);

        em.merge(language);
    }

    @Transactional
    public void delete(Integer id) {
        Language language = em.find(Language.class, id);

        if (language == null) {
            throw new IllegalArgumentException("Language with id " + id + " does not exist");
        }

        em.remove(language);
    }

    public record LanguageDTO(Integer id, String name) {
    }

}
