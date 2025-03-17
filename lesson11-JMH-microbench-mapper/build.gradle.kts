plugins {
    kotlin("jvm") version "2.0.21"
    id("me.champeau.jmh") version "0.7.3"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lesson03-logger"))
    implementation(project(":lesson03-domain-model"))
    implementation(project(":lesson04-mapTo"))
    implementation(project(":lesson11-naive-mapper-enhanced"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}