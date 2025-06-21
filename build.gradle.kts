import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    java
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.reflections:reflections:0.10.2")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.7.0")
    implementation("com.charleskorn.kaml:kaml:0.80.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.8.1")

    implementation("com.google.guava:guava:32.1.3-jre")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.10.1")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("commons-codec:commons-codec:1.16.0")
    implementation("commons-io:commons-io:2.15.1")
    implementation("org.apache.commons:commons-text:1.11.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.jsoup:jsoup:1.17.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.luben:zstd-jni:1.5.5-6")
    implementation("net.sf.trove4j:core:3.1.0")


    implementation("club.minnced:discord-webhooks:0.8.4")
    implementation("club.minnced:jda-ktx:0.12.0")
    implementation("net.dv8tion:JDA:5.6.1")


    implementation("org.postgresql:postgresql:42.7.2")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.jetbrains.exposed:exposed-core:0.49.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.49.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.49.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.49.0")
    implementation("org.xerial:sqlite-jdbc:3.46.1.0")

    val ktorVersion = "2.3.11"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-caching-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("io.ktor:ktor-server-compression:$ktorVersion")
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:1.13.6")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.1")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.17.1")
    implementation("org.honton.chas.hocon:jackson-dataformat-hocon:1.1.1")

    implementation("org.json:json:20190722")
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")

    implementation("com.vladsch.flexmark:flexmark-all:0.62.2")

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.assertj:assertj-core:3.25.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
