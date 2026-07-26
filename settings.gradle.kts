// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

@file:Suppress("UnstableApiUsage")

rootProject.name = "compose-coui-ui"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-plugins")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":coui-core")
include(":coui-ui")
include(":coui-preference")
include(":coui-shader")
include(":coui-blur")
include(":coui-squircle")
include(":coui-icons")
include(":coui-navigation3-ui")

include(":baselineprofile")

include(":example:shared")
include(":example:android")
include(":example:desktop")
include(":example:web")
include(":example:macos")

include(":docs:demo")
include(":docs:iconGen")
