rootProject.name = "commonground"

include(":core")
include(":server")
include(":client:multiplatform")
include(":client:android")
include(":client:desktop")

include(":maptesting:multiplatform")
include(":maptesting:android")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            val versions = object {
                val java = version("java", "17")
                val kotlin = "2.3.10"
                val serialization = "1.10.0"
                val coroutines = "1.10.2"
                val room = "2.8.4"
                val sqlite = "2.6.2"
                val ksp = "2.3.5"
                val logging = "8.0.01"
                val logback = object {
                    val jvm = "1.5.31"
                    val android = "3.0.0"
                }
                val ktor = "3.4.0"
                val spring = object {
                    val frameworkPlugin = "4.0.5"
                    val dependencyManagement = "1.1.7"
                    val starterWeb = "4.0.5"
                    val starterTest = "4.0.5"
                    val pluginSpring = "2.3.10"
                }

                val kotlinReflect = "1.9.0"
                val compose = object {
                    val plugin = "1.10.1"
                    val material3 = "1.9.0"
                    val materialIconsExtended = "1.7.3"
                    val uiDesktop = "1.10.1"
                    val navigation = "2.9.2"
                    val viewmodel = "2.10.0"
                }
                val maplibre = "0.12.1"
                val android = object {
                    val agp = "8.12.0"
                    val androidCompileSdk = version("androidCompileSdk", "36")
                    val androidMinSdk = version("androidMinSdk", "29")
                    val androidTargetSdk = version("androidTargetSdk", "36")
                    val coreKtx = "1.17.0"
                    val appcompat = "1.7.1"
                    val activity = "1.12.3"
                }
                val bcrypt = "0.10.2"
            }

            plugin("kotlin.multiplatform", "org.jetbrains.kotlin.multiplatform").version(versions.kotlin)
            plugin("kotlin.jvm", "org.jetbrains.kotlin.jvm").version(versions.kotlin)
            plugin("kotlin.android", "org.jetbrains.kotlin.android").version(versions.kotlin)
            plugin("kotlin.serialization", "org.jetbrains.kotlin.plugin.serialization").version(versions.kotlin)
            plugin("composeMultiplatform", "org.jetbrains.compose").version(versions.compose.plugin)
            plugin("composeCompiler", "org.jetbrains.kotlin.plugin.compose").version(versions.kotlin)
            plugin("ksp", "com.google.devtools.ksp").version(versions.ksp)
            plugin("room", "androidx.room").version(versions.room)
            plugin("androidApplication", "com.android.application").version(versions.android.agp)
            plugin("androidMultiplatform", "com.android.kotlin.multiplatform.library").version(versions.android.agp)

            plugin("springframework", "org.springframework.boot").version(versions.spring.frameworkPlugin)
            plugin("dependencyManagement", "io.spring.dependency-management").version(versions.spring.dependencyManagement)
            plugin("springPlugin", "org.jetbrains.kotlin.plugin.spring").version(versions.spring.pluginSpring)

            library("compose.material3", "org.jetbrains.compose.material3", "material3").version(versions.compose.material3)
            library("compose.windowSizeClass", "org.jetbrains.compose.material3", "material3-window-size-class").version(versions.compose.material3)
            library("compose.materialIconsExtended", "org.jetbrains.compose.material", "material-icons-extended").version(versions.compose.materialIconsExtended)
            library("compose.uiDesktop", "org.jetbrains.compose.ui", "ui-desktop").version(versions.compose.uiDesktop)
            library("compose.navigation", "org.jetbrains.androidx.navigation", "navigation-compose").version(versions.compose.navigation)
            library("compose.viewmodel", "org.jetbrains.androidx.lifecycle", "lifecycle-viewmodel-compose").version(versions.compose.viewmodel)

            library("maplibre.composeUi", "org.maplibre.compose", "maplibre-compose").version(versions.maplibre)
            library("maplibre.nativeBindingsJni", "org.maplibre.compose", "maplibre-native-bindings-jni").version(versions.maplibre)

            library("serialization.core", "org.jetbrains.kotlinx", "kotlinx-serialization-core").version(versions.serialization)
            library("serialization.json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version(versions.serialization)

            library("coroutines.core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version(versions.coroutines)
            library("coroutines.desktop", "org.jetbrains.kotlinx", "kotlinx-coroutines-swing").version(versions.coroutines)
            library("coroutines.android", "org.jetbrains.kotlinx", "kotlinx-coroutines-android").version(versions.coroutines)

            library("room.runtime", "androidx.room", "room-runtime").version(versions.room)
            library("room.compiler", "androidx.room", "room-compiler").version(versions.room)
            library("sqlite.jvm", "androidx.sqlite", "sqlite-bundled-jvm").version(versions.sqlite)
            library("sqlite.android", "androidx.sqlite", "sqlite-bundled").version(versions.sqlite)

            library("logging", "io.github.oshai", "kotlin-logging").version(versions.logging)
            library("logback.jvm", "ch.qos.logback", "logback-classic").version(versions.logback.jvm)
            library("logback.android", "com.github.tony19", "logback-android").version(versions.logback.android)

            library("ktor.serialization", "io.ktor", "ktor-serialization-kotlinx-json").version(versions.ktor)
            library("ktor.client.core", "io.ktor", "ktor-client-core").version(versions.ktor)
            library("ktor.client.engine", "io.ktor", "ktor-client-cio").version(versions.ktor)
            library("ktor.client.logging", "io.ktor", "ktor-client-logging").version(versions.ktor)
            library("ktor.client.contentNegotiation", "io.ktor", "ktor-client-content-negotiation").version(versions.ktor)

            library("android.coreKtx", "androidx.core", "core-ktx").version(versions.android.coreKtx)
            library("android.appcompat", "androidx.appcompat", "appcompat").version(versions.android.appcompat)
            library("android.activityKtx", "androidx.activity", "activity-ktx").version(versions.android.activity)
            library("android.activityCompose", "androidx.activity", "activity-compose").version(versions.android.activity)


            library("spring.starterWeb", "org.springframework.boot", "spring-boot-starter-web").version(versions.spring.starterWeb)
            library("spring.starterTest", "org.springframework.boot", "spring-boot-starter-test").version(versions.spring.starterTest)
            library("orgKotlinReflect", "org.jetbrains.kotlin", "kotlin-reflect").version(versions.kotlinReflect)

            library("spring.starterValidation", "org.springframework.boot", "spring-boot-starter-validation").version(versions.spring.starterWeb)
            library("bcrypt", "at.favre.lib", "bcrypt").version(versions.bcrypt)
        }
    }
}
