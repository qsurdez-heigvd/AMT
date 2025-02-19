plugins {
    id("io.spring.dependency-management")
}

allprojects {
    group = "ch.heig.amt"
}

subprojects {
    version = "0.0.1-SNAPSHOT"

    apply {
        plugin("io.spring.dependency-management")
    }

    repositories {
        mavenCentral()
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.0")
            mavenBom("org.springdoc:springdoc-openapi:2.8.3")
            mavenBom("io.jsonwebtoken:jjwt:0.12.6")
        }

        dependencies {
            dependency("org.bouncycastle:bcprov-jdk18on:1.80")

            // For annotation processing
            dependency("org.jetbrains:annotations:26.0.1")
            dependency("com.palantir.javapoet:javapoet:0.6.0")
            dependency("org.mapstruct.tools.gem:gem-api:1.0.0.Alpha3")
            dependency("org.mapstruct.tools.gem:gem-processor:1.0.0.Alpha3")
            dependency("com.google.auto.service:auto-service-annotations:1.1.1")
            dependency("com.google.auto.service:auto-service:1.1.1")

            // Utilities
            dependency("org.projectlombok:lombok:1.18.36")
            dependency("org.mapstruct:mapstruct:1.6.3")
            dependency("org.mapstruct:mapstruct-processor:1.6.3")
            dependency("org.mapstruct.extensions.spring:mapstruct-spring-annotations:1.1.2")
            dependency("org.mapstruct.extensions.spring:mapstruct-spring-extensions:1.1.2")
        }
    }
}
