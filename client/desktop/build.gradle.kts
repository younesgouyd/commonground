import org.jetbrains.compose.desktop.application.dsl.TargetFormat

group = "com.commonground"
version = "0.1.0"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    implementation(project(":client:multiplatform"))
    implementation(libs.compose.uiDesktop)
    implementation(libs.compose.material3)
}

compose.desktop {
    application {
        mainClass = "com.commonground.client.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.AppImage, TargetFormat.Deb)
            packageName = "com.commonground.client.desktop"
        }
    }
}