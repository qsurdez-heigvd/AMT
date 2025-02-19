package ch.heigvd.amt.jpa.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class InventoryRepository {
    @Inject
    private EntityManager em;

    public record InventoryDTO (Integer id, Integer filmdId, Integer storeId) {
        public static InventoryDTO create(Integer filmdId, Integer storeId) {
            return new InventoryDTO(null, filmdId, storeId);
        }
    }

    public InventoryDTO read(Integer id) {
        // TODO: implement solution
        return null;
    }

    @Transactional
    public InventoryDTO create(InventoryDTO inventory) {
        // TODO: implement solution
        return null;
    }

    @Transactional
    public void update(InventoryDTO inventory) {
        // TODO: implement solution

    }

    @Transactional
    public void delete(Integer id) {
        // TODO: implement solution
    }

}
