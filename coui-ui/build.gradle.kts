// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlinMultiplatform)
    id("module.kotlin-jvm-toolchain")
    id("module.publication")
    id("module.spotless")
}

couiPublication {
    description.set("A UI library for Compose Multiplatform")
}

kotlin {
    withSourcesJar(true)

    android {
        buildToolsVersion = BuildConfig.BUILD_TOOLS_VERSION
        compileSdk {
            version =
                release(BuildConfig.COMPILE_SDK) {
                    minorApiLevel = BuildConfig.COMPILE_SDK_MINOR
                }
        }
        minSdk = BuildConfig.MIN_SDK
        namespace = "${BuildConfig.LIBRARY_ID}.ui"
    }

    jvm("desktop")

    iosArm64()
    iosSimulatorArm64()
    macosArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    js(IR) {
        browser()
    }

    applyCOUISourceSetHierarchy()

    sourceSets {
        commonMain.dependencies {
            api(projects.couiCore)
            api(projects.couiSquircle)
            api(libs.jetbrains.compose.foundation)

            implementation(libs.androidx.navigationevent)
            implementation(libs.jetbrains.compose.window.size)

            implementation(libs.materialKolor.utilities) // Material Color for Multiplatform
        }
    }
}

baselineProfile {
    filter {
        include("com.suqi8.coui.kmp.**")
    }
}

val convertBaselineProfile by tasks.registering(ConvertBaselineProfileTask::class) {
    description = "convertBaselineProfile"
    inputFile.set(
        layout.projectDirectory.file("src/androidMain/generated/baselineProfiles/baseline-prof.txt"),
    )
    outputFile.set(
        layout.projectDirectory.file("src/androidMain/baselineProfiles/baseline-prof.txt"),
    )
    targetPackage.set("com/suqi8/coui/kmp/")
    excludePackages.set(
        listOf(
            "com/suqi8/coui/kmp/icon/extended/",
            "com/suqi8/coui/kmp/shared/",
        ),
    )
    additionalOutputs.put(
        "com/suqi8/coui/kmp/preference/",
        rootProject.layout.projectDirectory
            .file(
                "coui-preference/src/androidMain/baselineProfiles/baseline-prof.txt",
            ).asFile.absolutePath,
    )
    additionalOutputs.put(
        "com/suqi8/coui/kmp/blur/",
        rootProject.layout.projectDirectory
            .file(
                "coui-blur/src/androidMain/baselineProfiles/baseline-prof.txt",
            ).asFile.absolutePath,
    )
    additionalOutputs.put(
        "com/suqi8/coui/kmp/navigation3/ui/",
        rootProject.layout.projectDirectory
            .file(
                "coui-navigation3-ui/src/androidMain/baselineProfiles/baseline-prof.txt",
            ).asFile.absolutePath,
    )
    additionalOutputs.put(
        "com/suqi8/coui/kmp/shader/",
        rootProject.layout.projectDirectory
            .file(
                "coui-shader/src/androidMain/baselineProfiles/baseline-prof.txt",
            ).asFile.absolutePath,
    )
    additionalOutputs.put(
        "com/suqi8/coui/kmp/squircle/",
        rootProject.layout.projectDirectory
            .file(
                "coui-squircle/src/androidMain/baselineProfiles/baseline-prof.txt",
            ).asFile.absolutePath,
    )
}

tasks.matching { it.name == "generateBaselineProfile" }.configureEach {
    finalizedBy(convertBaselineProfile)
}

dependencies {
    baselineProfile(project(":baselineprofile"))
}
