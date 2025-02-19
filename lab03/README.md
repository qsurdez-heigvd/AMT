<p align="center">HEIG-VD &mdash; AMT</p>

---

<h3 align="center">
    Project Aegis
</h3>
<p align="center">
    Type-safe authorization library for Java applications.
</p>

---

<h3 align="center">
    VineWard
</h3>
<p align="center">
    Multi-tier application for wine enthusiasts.
</p>

---

Aegis is a type-safe authorization library for Java applications. It provides a way to define
authorization policies using annotations in classes and methods, which are then processed at compile
time to generate the necessary code to enforce these policies. This allows for a more secure and
maintainable way to handle authorization in Java applications.

Vineward is a multi-tier application for wine enthusiasts. It allows users to share insights, 
reviews, and comments about wines. The application implements a sophisticated attribute-based
authorization system using Aegis to manage user permissions and content validation.

## Building and Running

On first load of the project, you might need to run a full gradle build to ensure you have the
generated files needed for the project to run.

```bash
./gradlew build
```

As for the frontend, we used `pnpm` to manage dependencies, but any package manager should work.

```bash
pnpm install
```

> [!TIP]
> The frontend requires to have references to where the backend is running. Simply copy and paste
> the `.env.example` file to `.env` and all necessary variables should be set.
>
> We use Nuxt UI Pro for the frontend, which is a paid library. For local development as described
> herein-after it will only warn you. Although, you will not be able to build a docker image without
> a valid license key.

TL.DR. for running the application locally:

1. Start the backend server: `./gradlew bootRun`
2. Start the frontend server: `pnpm run dev`

## Backend Development

Start the development server on `http://localhost:13000`:

```bash
./gradlew bootRun
```

Or by using the `backend [start]` profile in IntelliJ IDEA, stored in the `.run` folder.

### Project structure

The repository is split in two main modules:
1. Aegis in the `aegis` directory, which is the authorization library. It has 3 sub-modules:
    - `aegis-api`: The API of the library, contains all the annotations and interfaces.
    - `aegis-processor`: Contains the annotation processor that generates the authorization systems.
    - `aegis-test`: Contains testing utilities for creating tests when using Aegis in a project.
2. Vineward in the `vineward` directory, which is the main application. It has 2 sub-modules:
    - `backend`: The multi-tier backend of the application.
    - `frontend`: The Nuxt 3 frontend of the application.

### Development guidelines

#### Local environment

We leverage
[Spring Boot's Docker Compose module](https://docs.spring.io/spring-boot/reference/features/dev-services.html#features.dev-services.docker-compose)
to provision the local development environment. This includes the PostgreSQL database.

Running the API server in a development environment will automatically call `docker compose up` and
add the necessary connection details to the application context.

> [!TIP]
> When testing, either manually or in integration / end-to-end tests, you can use the 
> `TestcontainersConfiguration` which provides the same environment as the local development setup,
> but it is volatile and is reset between each runs.
>
> A Spring runner is also configured to use the Testcontainers configuration in 
> `TestPresentiumApiApplication`. Which can be started using the `./gradlew bootTestRun` command.

#### API / Business separation

This application is split into standard layers. The business layer shall be responsible of handling
the business model and its logic (using entities, repositories, services, etc.), while the API layer
shall be responsible for handling API calls and responses.

Therefore, the application should mainly work using business entities at all times, and the API
layer will use mappers to convert said business entities to view models that represent exposed
information. The API also defines request bodies for request requiring it, using mappers to convert them to
business entities straight away.

Controllers are responsible for opening transactions, calling services, and returning the response.
They are responsible for ensuring that the user making the call is authorized to do so, validating
that the sent data is correct, then calling the business service to perform the operation. If the
business service sends a response, the controller should convert it to a view model and return it,
handling any exceptions should one arise.

##### File structure

- `api` package contains the API layer, with controllers, mappers, and view models.
- `business` package contains the business layer, with entities, repositories, services, and
  mappers.
- `security` package contains the security layer, with security configurations, filters, and
  the policies for the Aegis framework.

Inside packages, we try to separate concerns by their business domain. For example, the `user`
domain is held in `api.user` for the API layer, and `business.model.user` for the data domain.

## Frontend Development

Start the development server on `http://localhost:3000`:

```bash
pnpm run dev
```

If you have not configured a Nuxt UI Pro license key, you will see a warning in the console. You can
ignore this warning for local development and testing.

## Docker build

We provide a docker compose file for deploying the application in a containerized environment. The
application must be built by the docker compose since it requires the frontend to be built with a
valid license key and the proper URLs for accessing the backend.

Copy the `docker/.env.example` file to `docker/.env` and fill in the necessary variables, then run:

```bash
docker compose up --build
```

Using the default configuration, the application will be available at `http://localhost:3000`, the
backend being at `http://localhost:8080/api`.
