import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
            groupId = "com.shiviraj" // Use your package group ID
            artifactId = "logging-starter"
            version = "1.0.0"

            pom {
                name.set("Reactive Logging Starter")
                description.set("A reactive logging starter package")
                url.set("https://maven.pkg.github.com/IOT-echo-system/logging-starter") // Your GitHub repo URL
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/IOT-echo-system/logging-starter") // Replace with your repo path
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.token") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}


group = "com.shiviraj.iot"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/IOT-echo-system/logging-starter")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.google.code.gson:gson:2.8.9")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named("bootJar") {
    enabled = false
}
