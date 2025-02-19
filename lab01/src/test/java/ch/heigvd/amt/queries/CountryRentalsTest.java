package ch.heigvd.amt.queries;

import ch.heigvd.amt.jpa.service.CountryRentalsService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@QuarkusTest
class CountryRentalsTest {
  @Inject
  CountryRentalsService countryRentalsService;

  static List<CountryRentalsService.CountryRentals> results = new ArrayList<>();

  static Stream<String> flavors() {
    return Stream.of("NativeSQL", "JPQL", "CriteriaString", "CriteriaMetaModel");
  }

  @BeforeAll
  static void before() {
    results.addAll(Arrays.asList(
        new CountryRentalsService.CountryRentals("India", 1572L),
        new CountryRentalsService.CountryRentals("China", 1426L),
        new CountryRentalsService.CountryRentals("United States", 968L),
        new CountryRentalsService.CountryRentals("Japan", 825L),
        new CountryRentalsService.CountryRentals("Mexico", 796L),
        new CountryRentalsService.CountryRentals("Brazil", 748L),
        new CountryRentalsService.CountryRentals("Russian Federation", 713L),
        new CountryRentalsService.CountryRentals("Philippines", 568L),
        new CountryRentalsService.CountryRentals("Turkey", 388L)
    ));
  }

  @ParameterizedTest
  @MethodSource("flavors")
  void test(String flavor) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    var m = countryRentalsService.getClass().getMethod("countryRentals_" + flavor);
    var countries = (List<CountryRentalsService.CountryRentals>) m.invoke(countryRentalsService);
    Assertions.assertEquals(108, countries.size(), m.getName());
    for (int i = 0; i < results.size(); i++) {
      Assertions.assertEquals(results.get(i), countries.get(i), m.getName());
    }
  }
}
