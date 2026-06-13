plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

fun detectTarget(): String {
    val hostOs = when (val os = System.getProperty("os.name").lowercase()) {
        "mac os x" -> "macos"
        else -> os.split(" ").first()
    }
    val hostArch = when (val arch = System.getProperty("os.arch").lowercase()) {
        "x86_64" -> "amd64"
        "arm64" -> "aarch64"
        else -> arch
    }
    val renderer = when (hostOs) {
        "macos" -> "metal"
        else -> "opengl"
    }
    return "${hostOs}-${hostArch}-${renderer}"
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
    android {
        namespace = "com.commonground"
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        minSdk = libs.versions.androidMinSdk.get().toInt()
        androidResources.enable = true
    }
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(libs.datetime)
            implementation(libs.serialization.core)
            implementation(libs.serialization.json)
            implementation(libs.coroutines.core)
            implementation(libs.logging)

            // compose
            implementation(libs.compose.material3)
            implementation(libs.compose.windowSizeClass)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.compose.viewmodel)
            implementation(libs.compose.navigation)
            implementation(libs.maplibre.composeUi)

            // ktor
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.engine)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.client.auth)
        }
        jvmMain.dependencies {
            implementation(libs.coroutines.desktop)
            implementation(compose.desktop.currentOs) {
                exclude("org.jetbrains.compose.material") // todo
            }
            implementation(libs.logback.jvm)
            val maplibrebindings = libs.maplibre.nativeBindingsJni.get()
            runtimeOnly(maplibrebindings.toString()) {
                capabilities {
                    requireCapability("${maplibrebindings.group}:${maplibrebindings.name}-${detectTarget()}")
                }
            }
        }
        androidMain.dependencies {
            implementation(libs.coroutines.android)
            implementation(libs.android.coreKtx)
            implementation(libs.android.activityCompose)
            implementation(libs.logback.android)
        }
    }
}
