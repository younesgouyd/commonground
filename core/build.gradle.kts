plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.androidMultiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
    androidLibrary {
        namespace = "com.commonground"
        compileSdk = 36
        minSdk = 29
        androidResources.enable = true
    }
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(libs.serialization.core)
            implementation(libs.serialization.json)
            implementation(libs.coroutines.core)
            implementation(libs.logging)
        }
        jvmMain.dependencies {
            implementation(libs.coroutines.desktop)
            implementation(libs.logback.jvm)
        }
        androidMain.dependencies {
            implementation(libs.coroutines.android)
            implementation(libs.android.coreKtx)
            implementation(libs.logback.android)
        }
    }
}

