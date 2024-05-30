plugins{
    kotlin("jvm") version "1.9.21"
    id("io.ktor.plugin") version "2.3.10"
    kotlin("plugin.serialization") version "2.0.0"
}

application{
    mainClass.set("io.ktor.server.netty.EngineMain")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

group = "de.agrothe.crosswords"
version = "1.0"

repositories{
    mavenCentral()
    maven{url=uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")}
}

dependencies{
    implementation("io.github.config4k:config4k:0.6.0")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.3")
    implementation("org.slf4j:slf4j-simple:2.0.12")
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    implementation("io.ktor:ktor-server-html-builder-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.10.1")
    implementation("org.jetbrains:kotlin-css-jvm:1.0.0-pre.129-kotlin-1.4.20")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.10")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-websockets:2.3.10")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.10")
    implementation("net.jodah:expiringmap:0.5.11")
    //implementation("ch.qos.logback:logback-classic:$logback_version")
    //testImplementation("io.ktor:ktor-server-tests-jvm")
    //testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
tasks.test{
    useJUnitPlatform()
}
kotlin{
    jvmToolchain(19)
}