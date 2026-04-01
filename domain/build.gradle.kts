plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

dependencies{
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.4.7")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs ("-Xshare:off", "-XX:+EnableDynamicAgentLoading")
}
