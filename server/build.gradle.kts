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
    implementation(project(":core"))
    implementation(libs.coroutines.core)
    implementation(libs.serialization.core)
    implementation(libs.serialization.json)
    implementation(libs.logback.jvm)

    implementation(libs.spring.starterWeb)
    implementation(libs.spring.starterValidation)
    implementation(libs.orgKotlinReflect)

    implementation(libs.bcrypt)

    implementation(libs.logging)
    runtimeOnly(libs.logback.jvm)

    testImplementation(libs.spring.starterTest)
}
kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> { useJUnitPlatform() }

