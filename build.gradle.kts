plugins {
    kotlin("jvm") version "1.4.20"
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}