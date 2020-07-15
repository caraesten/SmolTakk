plugins {
    kotlin("jvm") version "1.3.72"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    // TODO: Replace versions with imported properties
    implementation("io.ktor:ktor-server-core:1.3.2")
    implementation("io.ktor:ktor-server-netty:1.3.2")
    implementation("io.ktor:ktor-server-sessions:1.3.2")
    implementation("io.ktor:ktor-mustache:1.3.2")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.jetbrains.exposed:exposed-core:0.26.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.26.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.26.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.26.1")
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}