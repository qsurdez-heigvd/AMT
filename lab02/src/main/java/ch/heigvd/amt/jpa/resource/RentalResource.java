package ch.heigvd.amt.jpa.resource;

import ch.heigvd.amt.jpa.service.RentalService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.reactive.RestForm;

import java.util.List;

// The existing annotations on this class must not be changed (i.e. new ones are allowed)
@Path("rental")
public class RentalResource {

  @CheckedTemplate
  public static class Templates {
    public static native TemplateInstance rental(String username);
    public static native TemplateInstance rental$success(RentalService.RentalDTO rental);
    public static native TemplateInstance rental$failure(String message);
    public static native TemplateInstance searchFilmsResults(
            List<RentalService.FilmInventoryDTO> films);
    public static native TemplateInstance searchFilmsSelect(
            RentalService.FilmInventoryDTO film);
    public static native TemplateInstance searchCustomersResults(
            List<RentalService.CustomerDTO> customers);
    public static native TemplateInstance searchCustomersSelect(
            RentalService.CustomerDTO customer);
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public TemplateInstance rental(@Context SecurityContext securityContext) {
    return Templates.rental(securityContext.getUserPrincipal().getName());
  }

  @POST
  @Produces(MediaType.TEXT_HTML)
  @Blocking
  public TemplateInstance registerRental(@Context SecurityContext securityContext,
                                         @RestForm Integer inventory, @RestForm Integer customer) {
    if (inventory == null || customer == null) {
      return Templates.rental$failure("The submission is not valid, missing inventory or customer");
    }

    // TODO: implement solution (exercise 5)

    if (true /* TODO */) {
      return Templates.rental$success(null /* TODO */);
    } else {
      return Templates.rental$failure("The selected item is not available.");
    }
  }

  @GET
  @Path("/film/{inventory}")
  @Produces(MediaType.TEXT_HTML)
  @Blocking
  public TemplateInstance selectFilmsGet(Integer inventory) {
    // TODO: implement solution (exercise 5)
    return Templates.searchFilmsSelect(null /* TODO */);
  }

  @POST
  @Path("/film/search")
  @Produces(MediaType.TEXT_HTML)
  @Blocking
  public TemplateInstance searchFilmsPost(@Context SecurityContext securityContext, @RestForm String query) {
    // TODO: implement solution (exercise 5)
    return Templates.searchFilmsResults(null /* TODO */);
  }

  @POST
  @Path("/customer/search")
  @Produces(MediaType.TEXT_HTML)
  @Blocking
  public TemplateInstance searchCustomersPost(@Context SecurityContext securityContext, @RestForm String query) {
    // TODO: implement solution (exercise 5)
    return Templates.searchCustomersResults(null /* TODO */);
  }

  @GET
  @Path("/customer/{customer}")
  @Produces(MediaType.TEXT_HTML)
  @Blocking
  public TemplateInstance selectCustomerGet(Integer customer) {
    // TODO: implement solution (exercise 5)
    return Templates.searchCustomersSelect(null /* TODO */);
  }
}
