package ch.heigvd.amt.queries;

import ch.heigvd.amt.jpa.service.ActorsInPGService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.EntityManager;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@QuarkusTest
class ActorsInPGTest {

  @Inject
  EntityManager em;

  @Inject
  ActorsInPGService actorsInPGService;

  static List<ActorsInPGService.ActorInPGRating> results = new ArrayList<>();

  static Stream<String> flavors() {
    return Stream.of("NativeSQL", "JPQL", "CriteriaString", "CriteriaMetaModel");
  }

  @BeforeAll
  static void before() {
    results.addAll(Arrays.asList(
        new ActorsInPGService.ActorInPGRating("CAMERON", "ZELLWEGER", 15L),
        new ActorsInPGService.ActorInPGRating("JEFF", "SILVERSTONE", 12L),
        new ActorsInPGService.ActorInPGRating("KIRSTEN", "AKROYD", 12L),
        new ActorsInPGService.ActorInPGRating("NICK", "STALLONE", 12L),
        new ActorsInPGService.ActorInPGRating("VAL", "BOLGER", 12L),
        new ActorsInPGService.ActorInPGRating("RIP", "CRAWFORD", 11L),
        new ActorsInPGService.ActorInPGRating("WALTER", "TORN", 11L),
        new ActorsInPGService.ActorInPGRating("FAY", "WINSLET", 10L)
    ));
  }

  @ParameterizedTest
  @MethodSource("flavors")
  void queryTest(String flavor) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      var m = actorsInPGService.getClass().getMethod("actorInPGRatings_" + flavor);
      var actors = (List<ActorsInPGService.ActorInPGRating>) m.invoke(actorsInPGService);
      Assertions.assertEquals(199, actors.size(), m.getName());
      for (int i = 0; i < results.size(); i++) {
        Assertions.assertEquals(results.get(i), actors.get(i), m.getName());
      }
  }
}
