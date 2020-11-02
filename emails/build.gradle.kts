plugins {
    kotlin("jvm")
}

group = "com.smoltakk"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.sun.mail:jakarta.mail:1.6.5")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.1")
    implementation(project(":repositories"))
    implementation(project(":database"))
    implementation(project(":models"))
}
