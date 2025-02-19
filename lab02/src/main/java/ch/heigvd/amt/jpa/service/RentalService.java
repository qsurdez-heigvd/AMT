package ch.heigvd.amt.jpa.service;

import ch.heigvd.amt.jpa.entity.Customer;
import ch.heigvd.amt.jpa.entity.Inventory;
import ch.heigvd.amt.jpa.entity.Staff;
import ch.heigvd.amt.jpa.entity.Store;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Signature of existing methods must not be changed.
 */
@ApplicationScoped
public class RentalService {

  @Inject
  EntityManager em;

  // The following records must not be changed
  public record RentalDTO (Integer inventory, Integer customer) {}
  public record FilmInventoryDTO(String title, String description, Integer inventoryId) {}
  public record CustomerDTO(Integer id, String firstName, String lastName) {}

  /**
   * Rent a film out of store's inventory for a given customer.
   *
   * @param inventory the inventory to rent
   * @param customer  the customer to which the inventory is rented
   * @param staff     the staff that process the customer's request in the store
   * @return an Optional that is present if rental is successful, if Optional is empty rental failed
   */
  public Optional<RentalDTO> rentFilm(Inventory inventory, Customer customer, Staff staff) {
    // TODO: implement solution (exercise 1)
    return Optional.empty();
  }

  /**
   *
   * @param query the searched string
   * @return films matching the query
   */
  public List<FilmInventoryDTO> searchFilmInventory(String query, Store store) {
    // TODO: implement solution (exercise 2)
    return Collections.emptyList();
  }

  public FilmInventoryDTO searchFilmInventory(Integer inventoryId) {
    // TODO: implement solution (exercise 2)
    return new FilmInventoryDTO("title", "description", 1);
  }

  public CustomerDTO searchCustomer(Integer customerId) {
    // TODO: implement solution (exercise 2)
    return new CustomerDTO(1,"firstname", "lastname");
  }

  public List<CustomerDTO> searchCustomer(String query, Store store) {
    // TODO: implement solution (exercise 2)
    return Collections.emptyList();
  }
}
