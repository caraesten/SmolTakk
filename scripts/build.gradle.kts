plugins {
    kotlin("jvm")
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

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    register("createDb", JavaExec::class.java) {
        main = "com.smolltakk.scripts.db.CreateDb"
        classpath = sourceSets.main.get().runtimeClasspath
    }
    register("bootstrapAdmin", JavaExec::class.java) {
        main = "com.smolltakk.scripts.users.InitializeAdminUser"
        classpath = sourceSets.main.get().runtimeClasspath
    }
    register("updateUser", JavaExec::class.java) {
        main = "com.smolltakk.scripts.users.UpdateUser"
        classpath = sourceSets.main.get().runtimeClasspath
    }
    register("sendEmail", JavaExec::class.java) {
        main = "com.smolltakk.scripts.messages.SendDigestEmails"
        classpath = sourceSets.main.get().runtimeClasspath
    }
}
