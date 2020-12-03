plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "com.smolltakk"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":database"))
    implementation(project(":repositories"))
    implementation(project(":emails"))
}

sourceSets.getByName("main").java.srcDirs("src/main/kotlin")

application {
    mainClassName = "com.smolltakk.scripts.ScriptsMain"
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    jar {
        manifest {
            attributes(mapOf(
                "Main-Class" to application.mainClassName
            ))
        }
    }
}
