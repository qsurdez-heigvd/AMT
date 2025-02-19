package ch.heigvd.amt.jpa.resource;

import ch.heigvd.amt.jpa.service.ActorsInPGService;
import ch.heigvd.amt.jpa.service.CountryRentalsService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/films")
public class FilmResource {

    @Inject
    private ActorsInPGService actorsInPGService;

    @Inject
    private CountryRentalsService countryRentalsService;

    @GET
    @Path("/actorsInPGRating")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ActorsInPGService.ActorInPGRating> nbFilmsByActorInPGRating() {
        return actorsInPGService.actorInPGRatings_NativeSQL();
    }

    @GET
    @Path("/countryByRentals")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CountryRentalsService.CountryRentals> countryByRentals() {
        return countryRentalsService.countryRentals_NativeSQL();
    }
}
