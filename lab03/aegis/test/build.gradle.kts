plugins {
    id("java-library")
}

description = "Testing utilities for Aegis authorization"

dependencies {
    compileOnly(project(":aegis:api"))

    // Codegen
    implementation("com.palantir.javapoet:javapoet")

    // SPI
    compileOnly("com.google.auto.service:auto-service-annotations")
    annotationProcessor("com.google.auto.service:auto-service")

    compileOnly("org.assertj:assertj-core")
    testImplementation("org.assertj:assertj-core")
}
