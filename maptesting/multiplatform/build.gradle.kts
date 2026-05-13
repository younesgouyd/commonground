plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.multiplatform)
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
    androidLibrary {
        namespace = "com.commonground"
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        minSdk = libs.versions.androidMinSdk.get().toInt()
        androidResources.enable = true
    }
    jvm()
    js {
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "commonground.js"
            }
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.maplibre.composeUi)

        }
        jvmMain.dependencies {
            implementation(libs.coroutines.desktop)
            implementation(compose.desktop.currentOs) {
                exclude("org.jetbrains.compose.material") // todo
            }
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
            implementation(libs.android.appcompat)
            implementation(libs.android.activityKtx)
            implementation(libs.android.activityCompose)
        }
    }
}
