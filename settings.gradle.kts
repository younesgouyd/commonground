rootProject.name = "commonground"

include(":core")
include(":server")
include(":client:multiplatform")
include(":client:android")
include(":client:desktop")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            val versions = object {
                val java = version("java", "17")
                val kotlin = version("kotlin", "2.3.10")
                val serialization = version("serialization", "1.10.0")
                val coroutines = version("coroutines", "1.10.2")
                val room = version("room", "2.8.4")
                val sqlite = version("sqlite", "2.6.2")
                val ksp = version("ksp", "2.3.5")
                val logging = version("logging", "8.0.01")
                val logback = object {
                    val jvm = version("logback.jvm", "1.5.31")
                    val android = version("logback.android", "3.0.0")
                }
                val ktor = version("ktor", "3.4.0")
                val spring = object {
                    val frameworkPlugin = version("spring.frameworkPlugin", "4.0.5")
                    val dependencyManagement = version("spring.dependencyManagement", "1.1.7")
                    val starterWeb = version("spring.starterWeb", "4.0.5")
                    val starterTest = version("spring.starter", "4.0.5")
                    val pluginSpring = version("spring.pluginSpring", "2.3.10")
                }

                val kotlinReflect = version("kotlinReflect", "1.9.0")
                val compose = object {
                    val plugin = version("compose.jetbrains", "1.10.1")
                    val material3 = version("compose.material3", "1.9.0")
                    val materialIconsExtended = version("compose.materialIconsExtended", "1.7.3")
                    val uiDesktop = version("compose.uiDesktop", "1.10.1")
                    val navigation = version("compose.navigation", "2.9.2")
                    val viewmodel = version("compose.viewmodel", "2.10.0")
                }
                val android = object {
                    val agp = version("agp", "8.12.0")
                    val coreKtx = version("coreKtx", "1.17.0")
                    val appcompat = version("appcompat", "1.7.1")
                    val activity = version("android.activity", "1.12.3")
                }
                val bcrypt = version("bcrypt", "0.10.2")
            }

            plugin("kotlin.multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef(versions.kotlin)
            plugin("kotlin.jvm", "org.jetbrains.kotlin.jvm").versionRef(versions.kotlin)
            plugin("kotlin.android", "org.jetbrains.kotlin.android").versionRef(versions.kotlin)
            plugin("kotlin.serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef(versions.kotlin)
            plugin("composeMultiplatform", "org.jetbrains.compose").versionRef(versions.compose.plugin)
            plugin("composeCompiler", "org.jetbrains.kotlin.plugin.compose").versionRef(versions.kotlin)
            plugin("ksp", "com.google.devtools.ksp").versionRef(versions.ksp)
            plugin("room", "androidx.room").versionRef(versions.room)
            plugin("androidApplication", "com.android.application").versionRef(versions.android.agp)
            plugin("androidMultiplatform", "com.android.kotlin.multiplatform.library").versionRef(versions.android.agp)

            plugin("springframework", "org.springframework.boot").versionRef(versions.spring.frameworkPlugin)
            plugin("dependencyManagement", "io.spring.dependency-management").versionRef(versions.spring.dependencyManagement)
            plugin("springPlugin", "org.jetbrains.kotlin.plugin.spring").versionRef(versions.spring.pluginSpring)

            library("compose.material3", "org.jetbrains.compose.material3", "material3").versionRef(versions.compose.material3)
            library("compose.windowSizeClass", "org.jetbrains.compose.material3", "material3-window-size-class").versionRef(versions.compose.material3)
            library("compose.materialIconsExtended", "org.jetbrains.compose.material", "material-icons-extended").versionRef(versions.compose.materialIconsExtended)
            library("compose.uiDesktop", "org.jetbrains.compose.ui", "ui-desktop").versionRef(versions.compose.uiDesktop)
            library("compose.navigation", "org.jetbrains.androidx.navigation", "navigation-compose").versionRef(versions.compose.navigation)
            library("compose.viewmodel", "org.jetbrains.androidx.lifecycle", "lifecycle-viewmodel-compose").versionRef(versions.compose.viewmodel)

            library("serialization.core", "org.jetbrains.kotlinx", "kotlinx-serialization-core").versionRef(versions.serialization)
            library("serialization.json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef(versions.serialization)

            library("coroutines.core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef(versions.coroutines)
            library("coroutines.desktop", "org.jetbrains.kotlinx", "kotlinx-coroutines-swing").versionRef(versions.coroutines)
            library("coroutines.android", "org.jetbrains.kotlinx", "kotlinx-coroutines-android").versionRef(versions.coroutines)

            library("room.runtime", "androidx.room", "room-runtime").versionRef(versions.room)
            library("room.compiler", "androidx.room", "room-compiler").versionRef(versions.room)
            library("sqlite.jvm", "androidx.sqlite", "sqlite-bundled-jvm").versionRef(versions.sqlite)
            library("sqlite.android", "androidx.sqlite", "sqlite-bundled").versionRef(versions.sqlite)

            library("logging", "io.github.oshai", "kotlin-logging").versionRef(versions.logging)
            library("logback.jvm", "ch.qos.logback", "logback-classic").versionRef(versions.logback.jvm)
            library("logback.android", "com.github.tony19", "logback-android").versionRef(versions.logback.android)

            library("ktor.serialization", "io.ktor", "ktor-serialization-kotlinx-json").versionRef(versions.ktor)
            library("ktor.client.core", "io.ktor", "ktor-client-core").versionRef(versions.ktor)
            library("ktor.client.engine", "io.ktor", "ktor-client-cio").versionRef(versions.ktor)
            library("ktor.client.logging", "io.ktor", "ktor-client-logging").versionRef(versions.ktor)
            library("ktor.client.contentNegotiation", "io.ktor", "ktor-client-content-negotiation").versionRef(versions.ktor)

            library("android.coreKtx", "androidx.core", "core-ktx").versionRef(versions.android.coreKtx)
            library("android.appcompat", "androidx.appcompat", "appcompat").versionRef(versions.android.appcompat)
            library("android.activityKtx", "androidx.activity", "activity-ktx").versionRef(versions.android.activity)
            library("android.activityCompose", "androidx.activity", "activity-compose").versionRef(versions.android.activity)


            library("spring.starterWeb", "org.springframework.boot", "spring-boot-starter-web").versionRef(versions.spring.starterWeb)
            library("spring.starterTest", "org.springframework.boot", "spring-boot-starter-test").versionRef(versions.spring.starterTest)
            library("orgKotlinReflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef(versions.kotlinReflect)

            library("spring.starterValidation", "org.springframework.boot", "spring-boot-starter-validation").versionRef(versions.spring.starterWeb)
            library("bcrypt", "at.favre.lib", "bcrypt").versionRef(versions.bcrypt)

        }
    }
}
