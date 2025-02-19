plugins {
    id("java-library")
}

description = "Annotation processor for converting Aegis annotations to interceptors integrating with Spring security"

dependencies {
    compileOnly(project(":aegis:api"))

    // Codegen
    implementation("com.palantir.javapoet:javapoet")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // GEM
    api("org.mapstruct.tools.gem:gem-api")
    annotationProcessor("org.mapstruct.tools.gem:gem-processor")

    // SPI
    compileOnly("com.google.auto.service:auto-service-annotations")
    annotationProcessor("com.google.auto.service:auto-service")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
