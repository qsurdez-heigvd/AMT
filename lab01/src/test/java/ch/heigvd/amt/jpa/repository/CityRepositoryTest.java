package ch.heigvd.amt.jpa.repository;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class CityRepositoryTest {

    @Inject
    CityRepository cityRepository;

    @Inject
    EntityManager em;

    @Test
    public void testReadCity() {
        CityRepository.CityDTO cityDTO = cityRepository.read(1);
        assertEquals(cityDTO.id(), 1);
        assertEquals(cityDTO.city(), "A Corua (La Corua)");
        assertEquals(cityDTO.country(), "Spain");
    }

    @Test
    public void testCreateCity() {
        Integer cityId = cityRepository.create("Gotham", "Canada");
        CityRepository.CityDTO cityDTO = cityRepository.read(cityId);
        assertEquals(cityDTO.id(), cityId);
        assertEquals(cityDTO.city(), "Gotham");
        assertEquals(cityDTO.country(), "Canada");
    }

    @Test
    public void testCreateCityWithNonExistingCountry() {
        AtomicReference<Integer> atomicCityId = new AtomicReference<>();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            atomicCityId.set(cityRepository.create("Caras Galadhon", "Lothlorien"));
        });
        String expectedMessage = "Country with name Lothlorien does not exist";
        String actualMessage = exception.getMessage();
        System.out.println(actualMessage);
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void cityNameMustBeShorterThan50Characters() {
        AtomicReference<Integer> atomicCityId = new AtomicReference<>();

        Exception exception = assertThrows(DataException.class, () -> {
            atomicCityId.set(cityRepository.create("*".repeat(51), "Afghanistan"));
    });
        String expectedMessage = "value too long for type character varying(50)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertNull(atomicCityId.get(), "City ID should be null");
    }

    @Test
    public void testUpdateCity() {
        Integer cityId = cityRepository.create("Gotham", "Canada");

        cityRepository.update(cityId, "Metropolis", "Canada");
        em.flush();
        em.clear();

        CityRepository.CityDTO cityDTO = cityRepository.read(cityId);
        assertEquals(cityDTO.id(), cityId);
        assertEquals(cityDTO.city(), "Metropolis");
        assertEquals(cityDTO.country(), "Canada");
    }

    @Test
    public void testUpdateCityWithoutClear() {
        Integer cityId = cityRepository.create("Gotham", "Canada");

        cityRepository.update(cityId, "Metropolis", "Canada");
        em.flush();

        CityRepository.CityDTO cityDTO = cityRepository.read(cityId);
        assertEquals(cityDTO.id(), cityId);
        assertEquals(cityDTO.city(), "Metropolis");
        assertEquals(cityDTO.country(), "Canada");
    }

    @Test
    public void testUpdateCityWithoutClearAndFLush() {
        Integer cityId = cityRepository.create("Gotham", "Canada");

        cityRepository.update(cityId, "Metropolis", "Canada");

        CityRepository.CityDTO cityDTO = cityRepository.read(cityId);
        assertEquals(cityDTO.id(), cityId);
        assertEquals(cityDTO.city(), "Metropolis");
        assertEquals(cityDTO.country(), "Canada");
    }

    @Test
    public void testDeleteCity() {
        Integer cityId = cityRepository.create("Gotham", "Canada");
        em.flush();

        cityRepository.delete(cityId);
        em.flush();

        CityRepository.CityDTO cityDTO = cityRepository.read(cityId);
        assertNull(cityDTO);
    }

}
