package ch.heigvd.amt;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    /**
     * Smoke test that the Quarkus app can start and serves request
     */
    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/hello?name={name}", "student")
          .then()
             .statusCode(200)
             .body(is("Hello student!"));
    }

}
