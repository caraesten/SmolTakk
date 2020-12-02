plugins {
    kotlin("jvm")
}

group = "com.smoltakk"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-core:1.3.2")
    implementation("io.ktor:ktor-server-jetty:1.3.2")
    implementation("io.ktor:ktor-server-sessions:1.3.2")
    implementation("io.ktor:ktor-mustache:1.3.2")
    implementation("com.zaxxer:HikariCP:2.7.8")
    implementation("org.postgresql:postgresql:42.2.2")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.jetbrains.exposed:exposed-core:0.26.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.26.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.26.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.26.1")
    implementation(project(":repositories"))
    implementation(project(":database"))
    implementation(project(":models"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}