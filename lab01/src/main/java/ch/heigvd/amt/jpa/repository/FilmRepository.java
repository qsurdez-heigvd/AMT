package ch.heigvd.amt.jpa.repository;

import ch.heigvd.amt.jpa.entity.Film;
import ch.heigvd.amt.jpa.entity.Language;
import ch.heigvd.amt.jpa.entity.enums.Rating;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FilmRepository {
    @Inject
    private EntityManager em;
    @Inject
    private LanguageRepository languageRepository;

    private FilmDTO fromEntityToDTO(Film film) {
        if (film == null) {
            return null;
        }

        return new FilmDTO(film.getId(), film.getTitle(), film.getLanguage().getName(), film.getRating().getCode());
    }

    public record FilmDTO (Integer id, String title, String language, String rating) {
    }

    public FilmDTO read(Integer id) {
        Film film = em.find(Film.class, id);
        return this.fromEntityToDTO(film);
    }

    @Transactional
    public Integer create(String title, String language, String rating) {
        Film film = new Film();
        film.setTitle(title);
        Language languageEntity;

        // I have questions about this part. Creating a new Repository for one query seems a bit overkill.
        // LanguageRepository.LanguageDTO languageDTO = new LanguageRepository().readByName(language);
        // Seems very strange to me but first solution found ...

        try {
            languageEntity = em.createQuery("SELECT l FROM language l WHERE trim(lower(l.name)) LIKE lower(:name)", Language.class)
                    .setParameter("name", language)
                    .getSingleResult();
        } catch (Exception e) {
            throw new IllegalArgumentException("Language with name " + language + " does not exist");
        }


        film.setLanguage(languageEntity);
        film.setRating(Rating.fromCode(rating));

        em.persist(film);
        return film.getId();
    }

    @Transactional
    public void update(Integer id, String title, String language, String rating) {
        Film film = em.find(Film.class, id);
        Language languageEntity;

        if (film == null) {
            throw new IllegalArgumentException("Film with id " + id + " does not exist");
        }

        film.setTitle(title);
        try {
            languageEntity = em.createQuery("SELECT l FROM language l WHERE trim(lower(l.name)) LIKE lower(:name)", Language.class)
                    .setParameter("name", language)
                    .getSingleResult();
        } catch (Exception e) {
            throw new IllegalArgumentException("Language with name " + language + " does not exist");
        }

        film.setLanguage(languageEntity);
        film.setRating(Rating.fromCode(rating));

        em.merge(film);
    }

    @Transactional
    public void delete(Integer id) {
        Film film = em.find(Film.class, id);

        if (film == null) {
            throw new IllegalArgumentException("Film with id " + id + " does not exist");
        }

        em.remove(film);
    }
}
