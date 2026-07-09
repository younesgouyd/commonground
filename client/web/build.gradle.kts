plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
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
            implementation(project(":client:multiplatform"))
            implementation(libs.compose.material3)
            implementation(libs.compose.windowSizeClass)
        }
    }
}
