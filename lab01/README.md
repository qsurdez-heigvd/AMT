[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/j1mG3FnO)
# Quarkus with Sakila

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

If you want to learn more about Sakila, please visit: https://www.jooq.org/sakila

## Dev environment

Make sure to have a Quarkus tooling in the IDE: https://quarkus.io/guides/ide-tooling

> **_NOTE:_**  If you are using IntelliJ IDEA Ultimate, do not install the IntelliJ Quarkus Tools plugin, but use the built-in Quarkus features available in IntelliJ IDEA Ultimate instead, as the former can cause troubles according to our tests.

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

Alternatively use the IDE integration to execute the dev mode from within the IDE by following the documentation of the tooling installed.

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

The Postgresql database managed by Quarkus Dev Services is available at `localhost:55432, username=postgres, password=postgres, db=postgres`.
The database is only up and running while running Quarkus dev mode.



## Directives
Not respecting the following directives will lead to penalties during grading:
- All instructions given in Javadoc must be followed.
- Files located in `src/test` must not be modified.
- Database schema must not be modified.
- The file `pom.xml` must not be modified.

The above rules apply for changes commited and pushed to the repository. Grading will only be made on content  published to the repository on `main` branch.

The `REPORT.md` file must be completed.

## Exercise 1 - Entity mappings

In this exercise, the goal is to write the entity mappings and some repositories for the Sakila database.

- All the main tables, the 13 tables that are not [associative/join](https://en.wikipedia.org/wiki/Associative_entity) table, have to be mapped to a JPA Entity
- All the tests contained in `src/test/java/ch/heigvd/amt/jpa/repository` have to pass at the end of this exercise. It requires to implement a few repository classes.
- The mappings have to be as precise as possible (e.g. table names, column names, column types, sequences, relations, etc.).
- The code must be documented with suitable JavaDoc.
- The `LAST_UPDATE` fields, can be omitted as they are automatically managed by the database with triggers when one updates an entity.
- For the film `rating` field, the PostgreSQL `enum` type has to be mapped to a Java enum.
- The `SPECIAL_FEATURES` field of the Film table is an array of String, it has to be mapped in Java to an array of String (see [this article](https://www.baeldung.com/java-hibernate-map-postgresql-array)).
- Read operations (`SELECT * FROM table`) must be possible on all entities.


As a starter point, the `Actor` entity is already implemented in the project, and an `ActorRepository` is provided as an example for a repository.

To map the PostgreSQL Enum `mpaa_rating` to a Java enum one of the following will be required:
- JPA Attribute converter: https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a2999
- Hibernate UserType: https://docs.jboss.org/hibernate/orm/6.6/userguide/html_single/Hibernate_User_Guide.html#basic-custom-type-UserType

**Report** The tests provided that validate updating entities are using `EntityManager.flush()` and `EntityManager.clear()`) (e.g. `ActorRepositoryTest.testUpdateActor`).

* Describe precisely from the perspective of SQL statements sent to the database, the difference between:
    * `ActorRepositoryTest.testUpdateActor`
    * `ActorRepositoryTest.testUpdateActorWithoutFlushAndClear`
    * `ActorRepositoryTest.testUpdateActorWithoutClear`

* Explain the behavior differences and why it occurs.

Hints: run the tests using the debugger, look at the SQL statements in the log.

## Exercise 2 - Querying

In this exercise, the goal is to write queries in SQL, Jakarta JPQL and Jakarta Criteria API (Metamodel and Strings versions).

The SQL queries can be implemented and tested directly against the database using Quarkus Dev Services database and student's preferred database tool (e.g. Jetbrains DataGrip, DBeaver, pgAdmin, Beekeeper Studio).

Documentation:
* SQL: https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a4427
* JPQL: https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a4665
* Criteria API: https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a6925

**Report** on the query language that you prefer and why.

### Actors with films of PG rating

Implementation service: `ActorsInPGService`

The SQL, Jakarta JPQL and Jakarta Criteria API (Metamodel and Strings) have to be implemented in the service.

Write queries that correspond the following description:

> List the actors (firstname, lastname) and their number of films, for films that have PG rating, ordered by: their number of films in descending order, the actor's firstname, the actor's lastname and the actor unique ID.

The expected first results of that query:
```
first_name |last_name   |nbfilms|
-----------+------------+-------+
CAMERON    |ZELLWEGER   |     15|
KIRSTEN    |AKROYD      |     12|
NICK       |STALLONE    |     12|
JEFF       |SILVERSTONE |     12|
[...]
```

Validate the implementation of the service by running tests contained in `ActorsInPGTest` class.

### Country by rentals

Implementation service: `CountryRentalsService`

The SQL, Jakarta JPQL and Jakarta Criteria API (Metamodel and Strings) have to be implemented in the service.

Write queries that correspond the following description:
> The countries with their number of rentals ordered by: their number of rentals in descending order, the country's name and the country unique ID.

The expected first results of that query:
```
country                              |rentals|
-------------------------------------+-------+
India                                |   1572|
China                                |   1426|
United States                        |    968|
Japan                                |    825|
Mexico                               |    796|
[...]
```

Validate the implementation of the service by running tests contained in `CountryRentalsTest` class.

## References

### Jakarta persistence
https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1

Relevant sections:
- `2. Entities`
- `3. Entity Operations`
- `4. Query Language`
- `6.4. Criteria Query API Usage`
- `6.5. Constructing Criteria Queries`

Lighter version in tutorial mode: https://eclipse-ee4j.github.io/jakartaee-tutorial/

Relevant sections:
- `Part VIII: Persistence`
- `Chapter 42. The Jakarta Persistence Query Language`
- `Chapter 43. Using the Criteria API to Create Queries`
- `Chapter 44. Creating and Using String-Based Criteria Queries`

