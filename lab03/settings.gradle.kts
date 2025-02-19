rootProject.name = "vineward-build"

pluginManagement {
    plugins {
        id("io.spring.dependency-management") version "1.1.6"
        id("org.springframework.boot") version "3.4.0"
        id("org.hibernate.orm") version "6.6.4.Final"
    }
}

// Aegis: Authorization Library
include("aegis:api", "aegis:processor", "aegis:test")

// VineWard Application
include("vineward:backend")
project(":vineward:backend").name = "vineward"
