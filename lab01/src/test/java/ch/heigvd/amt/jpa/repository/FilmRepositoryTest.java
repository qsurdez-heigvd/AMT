package ch.heigvd.amt.jpa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
class FilmRepositoryTest {
    @Inject
    FilmRepository filmRepository;
    @Inject
    EntityManager em;

    @Test
    public void testRead() {
        var f = filmRepository.read(854);
        assertEquals("STRANGERS GRAFFITI", f.title());
        assertTrue("english".equalsIgnoreCase(f.language().trim()));
        assertEquals("R", f.rating());
    }

    @Test
    public void testCreate() {
        var title = "A trip from Jakarta to Java";
        var language = "english";
        var rating = "PG-13";

        var fId = filmRepository.create(title, language, rating);
        assertNotNull(fId);

        var f = filmRepository.read(fId);
        assertEquals(title, f.title());
        assertTrue(language.equalsIgnoreCase(f.language().trim()));
        assertEquals(rating, f.rating());
    }

    @Test
    public void testUpdate() {
        var filmId = 988;
        var title = "Un voyage de Java à Jakarta";
        var language = "french";
        var rating = "NC-17";

        filmRepository.update(filmId, title, language, rating);
        em.flush();

        var f = filmRepository.read(filmId);
        assertEquals(title, f.title());
        assertTrue(language.equalsIgnoreCase(f.language().trim()));
        assertEquals(rating, f.rating());
    }

    @Test
    public void testDelete() {
        var fId = filmRepository.create("Café", "french", "R");
        assertNotNull(fId);
        em.flush();

        filmRepository.delete(fId);
        em.flush();

        var f = filmRepository.read(fId);
        assertNull(f);
    }
}