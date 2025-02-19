package ch.heigvd.amt.jpa.resource;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

// The existing annotations on this class must not be changed
@Path("hello")
public class HelloResource {

    @Inject
    Template hello;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public TemplateInstance get(@QueryParam("name") String name) {
        return hello.data("name", name);
    }

    // TODO: implement solution (exercise 3)
    @GET
    @Path("me")
    @Produces(MediaType.TEXT_PLAIN)
    public TemplateInstance getMe() {
        return hello.data("name", "username");
    }
}
