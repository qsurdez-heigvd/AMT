package ch.heigvd.amt.jpa.repository;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class LanguageRepositoryTest {

    @Inject
    LanguageRepository languageRepository;

    @Inject
    EntityManager em;

    @Test
    public void testReadLanguage() {
        LanguageRepository.LanguageDTO languageDTO = languageRepository.read(1);
        assertEquals(languageDTO.id(), 1);
        assertEquals(languageDTO.name().trim(), "English");
    }

    @Test
    public void testCreateLanguage() {
        String languageName = "Russian";
        Integer languageId = languageRepository.create(languageName);
        assertNotNull(languageId);

        LanguageRepository.LanguageDTO languageDTO = languageRepository.read(languageId);
        assertEquals(languageDTO.id(), languageId);
        assertEquals(languageDTO.name(), languageName);
    }

    @Test
    public void languageNameMustBeShorterThat20Characters() {
        AtomicReference<Integer> atomicLanguageId = new AtomicReference<>();

        Exception exception = assertThrows(DataException.class, () -> {
            atomicLanguageId.set(languageRepository.create("*".repeat(21)));
        });
        String expectedMessage = "value too long for type character varying(20)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertNull(atomicLanguageId.get(), "Language ID should be null");
    }

    @Test
    public void testUpdateLanguage() {
        String languageName = "Russian";
        String updatedLanguageName = "Finnish";
        Integer languageId = languageRepository.create(languageName);

        languageRepository.update(languageId, updatedLanguageName);
        em.flush();
        em.clear();

        LanguageRepository.LanguageDTO languageDTO = languageRepository.read(languageId);
        assertEquals(languageDTO.id(), languageId);
        assertEquals(languageDTO.name(), updatedLanguageName);
    }

    @Test
    public void testUpdateLanguageWithoutClear() {
        String languageName = "Russian";
        String updatedLanguageName = "Finnish";
        Integer languageId = languageRepository.create(languageName);

        languageRepository.update(languageId, updatedLanguageName);
        em.flush();

        LanguageRepository.LanguageDTO languageDTO = languageRepository.read(languageId);
        assertEquals(languageDTO.id(), languageId);
        assertEquals(languageDTO.name(), updatedLanguageName);
    }

    @Test
    public void testUpdateLanguageWithoutFlushAndClear() {
        String languageName = "Russian";
        String updatedLanguageName = "Finnish";
        Integer languageId = languageRepository.create(languageName);

        languageRepository.update(languageId, updatedLanguageName);

        LanguageRepository.LanguageDTO languageDTO = languageRepository.read(languageId);
        assertEquals(languageDTO.id(), languageId);
        assertEquals(languageDTO.name(), updatedLanguageName);
    }

    @Test
    public void testDeleteLanguage() {
        String languageName = "Russian";
        Integer languageId = languageRepository.create(languageName);
        em.flush();

        languageRepository.delete(languageId);
        em.flush();

        LanguageRepository.LanguageDTO languageDTO = languageRepository.read(languageId);
        assertNull(languageDTO);
    }

}
