plugins {
    id("java-library")
}

description = "Annotation and data models for Aegis"

dependencies {
    compileOnly("org.springframework.security:spring-security-core")
    compileOnly("org.springframework.security:spring-security-config")
    compileOnlyApi("org.jetbrains:annotations")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}
