// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    id("module.kotlin-jvm-toolchain")
    id("module.spotless")
}

kotlin {
    // The canvas ships as a web app; the JVM target only runs the unit tests of the document model
    // and the prompt / code generators.
    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "canvas"
        browser {
            commonWebpackConfig {
                outputFileName = "canvas.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.couiUi)
            implementation(projects.couiPreference)
            implementation(projects.couiIcons)
            implementation(libs.kotlinx.serialization.json)
        }
        val desktopTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
