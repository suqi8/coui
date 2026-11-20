// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

object BuildConfig {
    const val LIBRARY_VERSION = "2.1.0"
    const val LIBRARY_ID = "io.github.suqi8.coui.kmp"
    const val APPLICATION_NAME = "COUI"
    // The example app carries the library version it demonstrates.
    const val APPLICATION_VERSION_NAME = LIBRARY_VERSION
    const val APPLICATION_ID = "io.github.suqi8.coui.uitest"
    const val APPLICATION_SHARED_ID = "io.github.suqi8.coui.shared"
    const val COMPILE_SDK = 37
    const val COMPILE_SDK_MINOR = 0
    const val TARGET_SDK = 37
    const val MIN_SDK = 23
    const val BUILD_TOOLS_VERSION = "37.0.0"
    const val JDK_VERSION = 21
}

fun org.gradle.api.Project.getGitVersionCode(): Int {
    return providers.exec {
        commandLine("git", "rev-list", "--count", "HEAD")
    }.standardOutput.asText.get().trim().toInt()
}

fun org.gradle.api.Project.getGitHashShort(): String {
    return providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
    }.standardOutput.asText.get().trim()
}
