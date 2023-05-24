
plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("application")
    id("com.balduvian.assetgen")
}

group = project.property("cngeGroup")!!
version = project.property("cngeVersion")!!

val lwjglVersion = "3.3.1"
val jomlVersion = "1.10.4"

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("org.lwjgl:lwjgl:${lwjglVersion}")
    implementation("org.lwjgl:lwjgl-opengl:${lwjglVersion}")
    implementation("org.lwjgl:lwjgl-glfw:${lwjglVersion}")
    implementation("org.lwjgl:lwjgl-openal:${lwjglVersion}")

    implementation("org.lwjgl:lwjgl:${lwjglVersion}:natives-windows")
    implementation("org.lwjgl:lwjgl-opengl:${lwjglVersion}:natives-windows")
    implementation("org.lwjgl:lwjgl-glfw:${lwjglVersion}:natives-windows")
    implementation("org.lwjgl:lwjgl-openal:${lwjglVersion}:natives-windows")

    implementation("org.joml:joml:${jomlVersion}")

    implementation("com.squareup:kotlinpoet:1.13.2")
}

sourceSets.create("gen") {
    java.srcDirs("src/gen/kotlin")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin", sourceSets["gen"].java)
}

assetgen {
   unused = "2344324"
}

tasks {
    genassets {
        mainSourceSet = sourceSets["main"]
        genSourceSet = sourceSets["gen"]
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.apiVersion = "1.6"
    }
    shadowJar {
        archiveFileName.set("${project.name}.jar")
        manifest {
            attributes(mapOf("Main-Class" to "game/GameKt"))
        }
    }
    jar {
        archiveFileName.set("${project.name}.jar")
        manifest {
            attributes(mapOf("Main-Class" to "game/GameKt"))
        }
    }
    build {
        dependsOn(shadowJar)
    }
}

application {
    mainClass.set("game/GameKt")
}
