group = "com.commonground"
version = "0.1.0"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.springframework)
    alias(libs.plugins.dependencyManagement)
    alias(libs.plugins.springPlugin)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.serialization.core)
    implementation(libs.serialization.json)
    implementation(libs.logback.jvm)
    implementation(libs.spring.starterWeb)
    implementation(libs.spring.starterTest)
    implementation(libs.orgKotlinReflect)
}