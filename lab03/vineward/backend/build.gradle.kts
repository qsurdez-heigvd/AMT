import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("java")
    id("org.springframework.boot")
    id("org.hibernate.orm")
}

description = "Vineward API"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    // Spring Boot for API
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // --- Database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")

    // --- Authentication
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.bouncycastle:bcprov-jdk18on")
    implementation("io.jsonwebtoken:jjwt-api")
    runtimeOnly("io.jsonwebtoken:jjwt-impl")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson")

    // --- Aegis Authorization
    implementation(project(":aegis:api"))
    annotationProcessor(project(":aegis:processor"))
    testImplementation(project(":aegis:test"))
    testAnnotationProcessor(project(":aegis:test"))

    // --- Messaging
    implementation("org.springframework.boot:spring-boot-starter-artemis")
    runtimeOnly("org.apache.activemq:artemis-jakarta-server")

    // --- OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

    // --- Codegen utils
    // Lombok
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // Mapstruct
    compileOnly("org.mapstruct.extensions.spring:mapstruct-spring-annotations")
    implementation("org.mapstruct:mapstruct")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("org.mapstruct.extensions.spring:mapstruct-spring-extensions")

    // --- Devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // --- Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.hibernate.orm:hibernate-ant")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<BootRun>("bootRun") {
    systemProperty("spring.profiles.active", "dev")
    classpath(configurations["developmentOnly"])
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf(
        "-Amapstruct.defaultComponentModel=spring",
        "-Amapstruct.defaultInjectionStrategy=constructor",
    ))
}
