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
public class CountryRepositoryTest {

    @Inject
    CountryRepository countryRepository;

    @Inject
    EntityManager em;

    @Test
    public void testReadCountry() {
        CountryRepository.CountryDTO countryDTO = countryRepository.read(1);
        assertEquals(countryDTO.id(), 1);
        assertEquals(countryDTO.country(), "Afghanistan");
    }

    @Test
    public void testCreateCountry() {
        String countryName = "Prussia";
        Integer countryId = countryRepository.create(countryName);
        CountryRepository.CountryDTO countryDTO = countryRepository.read(countryId);
        assertEquals(countryDTO.id(), countryId);
        assertEquals(countryDTO.country(), countryName);
    }

    @Test
    public void countryNameMustBeShorterThan50Characters() {
        AtomicReference<Integer> atomicCountryId = new AtomicReference<>();

        Exception exception = assertThrows(DataException.class, () -> {
            atomicCountryId.set(countryRepository.create("*".repeat(51)));
        });

        String expectedMessage = "value too long for type character varying(50)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertNull(atomicCountryId.get(), "Country ID should be null");
    }

    @Test
    public void testUpdateCountry() {
        String countryName = "Prussia";
        String updatedCountryName = "USSR";
        Integer countryId = countryRepository.create(countryName);

        countryRepository.update(countryId, updatedCountryName);
        em.flush();
        em.clear();

        CountryRepository.CountryDTO countryDTO = countryRepository.read(countryId);
        assertEquals(countryDTO.id(), countryId);
        assertEquals(countryDTO.country(), updatedCountryName);
    }

    @Test
    public void testUpdateCountryWithoutClear() {
        String countryName = "Prussia";
        String updatedCountryName = "USSR";
        Integer countryId = countryRepository.create(countryName);

        countryRepository.update(countryId, updatedCountryName);
        em.flush();

        CountryRepository.CountryDTO countryDTO = countryRepository.read(countryId);
        assertEquals(countryDTO.id(), countryId);
        assertEquals(countryDTO.country(), updatedCountryName);
    }

    @Test
    public void testUpdateCountryWithoutClearAndFlush() {
        String countryName = "Prussia";
        String updatedCountryName = "USSR";
        Integer countryId = countryRepository.create(countryName);

        countryRepository.update(countryId, updatedCountryName);

        CountryRepository.CountryDTO countryDTO = countryRepository.read(countryId);
        assertEquals(countryDTO.id(), countryId);
        assertEquals(countryDTO.country(), updatedCountryName);
    }

    @Test
    public void testDeleteCountry() {
        String countryName = "Prussia";
        Integer countryId = countryRepository.create(countryName);
        em.flush();

        countryRepository.delete(countryId);
        em.flush();

        CountryRepository.CountryDTO countryDTO = countryRepository.read(countryId);
        assertNull(countryDTO);
    }

}
