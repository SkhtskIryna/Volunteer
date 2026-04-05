import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    kotlin("jvm") version "1.9.10"
    id("application")
    alias(libs.plugins.kotlin.serialization)
    id("com.github.johnrengelman.shadow") version "8.1.1"
}
application {
    mainClass.set("eu.tutorials.server.ServerKt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

sourceSets {
    main {
        resources.srcDir("src/main/resources")
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.processResources {
    from("src/main/resources") {
        include("application.conf")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("server")
    archiveClassifier.set("")
    archiveVersion.set("")
    manifest {
        attributes["Main-Class"] = "eu.tutorials.server.ServerKt"
    }
}

val ktor_version = "2.3.5"

dependencies {
//    implementation(project(":domain"))
//    implementation(project(":data"))
    implementation(files(
        "libs/domain.jar",
        "libs/data.jar"
    ))

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("io.ktor:ktor-server-cio-jvm:$ktor_version")
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("org.slf4j:slf4j-api:2.0.12")

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)

    // Для Ktor + JSON
    implementation("io.ktor:ktor-server-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // HTTP клієнт Ktor
    implementation("io.ktor:ktor-client-core:2.3.4")
    implementation("io.ktor:ktor-client-cio:2.3.4")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-client-serialization:2.3.4") // для старої серіалізації, можна видалити якщо не потрібно

    implementation("com.typesafe:config:1.4.2")
    implementation(libs.mysql.connector.java)
    implementation(libs.jbcrypt)

    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
}