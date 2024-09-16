plugins {
    kotlin("jvm") version "1.8.0"
    application
    kotlin("plugin.serialization") version "1.8.0"
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-client-core:2.0.0")
    implementation("io.ktor:ktor-client-cio:2.0.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

    implementation("org.slf4j:slf4j-api:2.0.0")
    implementation("ch.qos.logback:logback-classic:1.4.7")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0")
}

application {
    mainClass.set("ru.dziodzi.MainKt")
}
