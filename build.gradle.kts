plugins {
    kotlin("jvm") version "1.9.21"
}

group = "de.agrothe.crosswords"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("io.github.config4k:config4k:0.6.0")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.3")
    implementation("org.slf4j:slf4j-simple:2.0.12")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}