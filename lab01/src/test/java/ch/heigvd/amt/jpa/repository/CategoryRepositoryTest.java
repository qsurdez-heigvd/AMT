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
public class CategoryRepositoryTest {

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    EntityManager em;

    @Test
    public void testReadCategory() {
        CategoryRepository.CategoryDTO categoryDTO = categoryRepository.read(1);
        assertEquals(categoryDTO.id(), 1);
        assertEquals(categoryDTO.name(), "Action");
    }

    @Test
    public void testCreateCategory() {
        String categoryName = "Author";
        Integer categoryId = categoryRepository.create(categoryName);

        CategoryRepository.CategoryDTO categoryDTO = categoryRepository.read(categoryId);
        assertEquals(categoryDTO.id(), categoryId);
        assertEquals(categoryDTO.name(), categoryName);
    }

    @Test
    public void categoryNameMustBeShorterThan25Characters() {
        AtomicReference<Integer> atomicCategoryId = new AtomicReference<>();

        Exception exception = assertThrows(DataException.class, () -> {
            atomicCategoryId.set(categoryRepository.create("*".repeat(26)));
        });
        String expectedMessage = "value too long for type character varying(25)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertNull(atomicCategoryId.get(), "Category ID should be null");
    }

    @Test
    public void testUpdateCategory() {
        String categoryName = "Author";
        String categoryNameUpdated = "Nanar";
        Integer categoryId = categoryRepository.create(categoryName);

        categoryRepository.update(categoryId, categoryNameUpdated);
        em.flush();
        em.clear();

        CategoryRepository.CategoryDTO categoryDTO = categoryRepository.read(categoryId);
        assertEquals(categoryDTO.id(), categoryId);
        assertEquals(categoryDTO.name(), categoryNameUpdated);
    }

    @Test
    public void testUpdateCategoryWithoutClear() {
        String categoryName = "Author";
        String categoryNameUpdated = "Nanar";
        Integer categoryId = categoryRepository.create(categoryName);

        categoryRepository.update(categoryId, categoryNameUpdated);
        em.flush();

        CategoryRepository.CategoryDTO categoryDTO = categoryRepository.read(categoryId);
        assertEquals(categoryDTO.id(), categoryId);
        assertEquals(categoryDTO.name(), categoryNameUpdated);
    }

    @Test
    public void testUpdateCategoryWithoutFlushAndClear() {
        String categoryName = "Author";
        String categoryNameUpdated = "Nanar";
        Integer categoryId = categoryRepository.create(categoryName);

        categoryRepository.update(categoryId, categoryNameUpdated);

        CategoryRepository.CategoryDTO categoryDTO = categoryRepository.read(categoryId);
        assertEquals(categoryDTO.id(), categoryId);
        assertEquals(categoryDTO.name(), categoryNameUpdated);
    }

    @Test
    public void testDeleteCategory() {
        String categoryName = "Author";
        Integer categoryId = categoryRepository.create(categoryName);

        categoryRepository.delete(categoryId);

        CategoryRepository.CategoryDTO categoryDTO = categoryRepository.read(categoryId);
        assertNull(categoryDTO);
    }
}
