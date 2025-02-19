package ch.heigvd.amt.jpa.repository;

import ch.heigvd.amt.jpa.entity.Category;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CategoryRepository {

    @Inject
    private EntityManager em;

    private CategoryDTO fromEntityToDTO(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryDTO(category.getId(), category.getName());
    }

    public CategoryDTO read(Integer id) {
        Category category = em.find(Category.class, id);
        return this.fromEntityToDTO(category);
    }

    @Transactional
    public Integer create(String name) {
        Category category = new Category();
        category.setName(name);

        em.persist(category);
        return category.getId();
    }

    @Transactional
    public void update(Integer id, String name) {
        Category category = em.find(Category.class, id);

        if (category == null) {
            throw new IllegalArgumentException("Category with id " + id + " does not exist");
        }

        category.setName(name);

        em.merge(category);
    }

    @Transactional
    public void delete(Integer id) {
        Category category = em.find(Category.class, id);

        if (category == null) {
            throw new IllegalArgumentException("Category with id " + id + " does not exist");
        }

        em.remove(category);
    }

    public record CategoryDTO(Integer id, String name) {
    }
}
