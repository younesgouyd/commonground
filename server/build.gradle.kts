group = "com.commonground"
version = "0.1.0"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.framework)
    alias(libs.plugins.spring.dependencyManagement)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())

    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.coroutines.core)
    implementation(libs.kotlin.reflect)

    implementation(libs.spring.web)
    implementation(libs.spring.validation)
    implementation(libs.spring.jpa)
    implementation(libs.spring.security)
    runtimeOnly(libs.postgresqlDriver)

    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
}
