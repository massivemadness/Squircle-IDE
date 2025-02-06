/*
 * Copyright 2023 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("com.blacksquircle.feature")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.blacksquircle.ui.feature.editor"

    buildFeatures {
        viewBinding = true
        compose = true
    }
}

dependencies {

    // Core
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.timber)

    // UI
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.materialdesign)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.compose.manifest)

    // AAC
    implementation(libs.androidx.viewmodel)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.navigation)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    testImplementation(libs.coroutines.test)

    // DI
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)

    // Modules
    implementation(project(":feature-editor:api"))
    implementation(project(":feature-explorer:api"))
    implementation(project(":feature-shortcuts:api"))
    implementation(project(":feature-themes:api"))
    implementation(project(":feature-fonts:api"))
    implementation(project(":common-core"))
    implementation(project(":common-ui"))

    // Tests
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockk)
    androidTestImplementation(libs.test.junit.ext)
    androidTestImplementation(libs.test.runner)
}