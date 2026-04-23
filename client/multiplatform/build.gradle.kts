plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.androidMultiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
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
            implementation(libs.compose.material3)
            implementation(libs.compose.windowSizeClass)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.room.runtime)
            implementation(libs.logging)
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.engine)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.contentNegotiation)
        }
        jvmMain.dependencies {
            implementation(libs.sqlite.jvm)
            implementation(libs.coroutines.desktop)
            implementation(compose.desktop.currentOs) {
                exclude("org.jetbrains.compose.material") // todo
            }
            implementation(libs.logback.jvm)
        }
        androidMain.dependencies {
            implementation(libs.sqlite.android)
            implementation(libs.coroutines.android)
            implementation(libs.android.coreKtx)
            implementation(libs.android.appcompat)
            implementation(libs.android.activityKtx)
            implementation(libs.android.activityCompose)
            implementation(libs.logback.android)
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspJvm", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
