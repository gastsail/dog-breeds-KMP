import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization") version "1.9.22"
    id("com.android.library")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp") version "1.9.0-1.0.11"
    id("app.cash.sqldelight") version "2.0.1"
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.expenseApp.db")
        }
    }
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)

            dependencies {
                implementation("androidx.compose.ui:ui-test-junit4-android:1.5.4")
                debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.material)
                api(compose.materialIconsExtended)
                api(compose.foundation)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                //Navigation
                api(compose.animation)
                api(libs.precompose)
                //ViewModel
                api(libs.precompose.viewmodel)

                //Koin
                implementation(project.dependencies.platform("io.insert-koin:koin-bom:3.5.1"))
                implementation("io.insert-koin:koin-core")
                implementation("io.insert-koin:koin-compose")
                api(libs.precompose.koin)

                //Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.content.negotiation)
                implementation(libs.ktor.serialization)
                implementation(libs.kotlinx.coroutines.core)

                implementation(compose.components.resources)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.mockative)
                implementation(kotlin("test"))

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
            }
        }

        val androidMain by getting {
            dependencies {
                api(libs.activity.compose)
                api(libs.appcompat)
                api(libs.core.ktx)
                api(compose.preview)
                api(compose.uiTooling)
                implementation(libs.ui.tooling.preview)

                //Koin
                implementation(project.dependencies.platform("io.insert-koin:koin-bom:3.5.1"))
                implementation("io.insert-koin:koin-core")
                implementation("io.insert-koin:koin-android")

                //SQLDelight
                implementation(libs.android.driver)

                // Ktor
                implementation(libs.ktor.client.okhttp)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.junit)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.ktor.client.darwin.v233)
                implementation(libs.native.driver)
                implementation(libs.stately.common)
                // Ktor
                implementation(libs.ktor.client.darwin)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.myapplication.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        task("testClasses")
        jvmToolchain(17)
    }
    packagingOptions {
        resources.excludes.add("META-INF/*")
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/versions")
    }
}
dependencies {
    implementation("androidx.core:core-ktx:+")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    configurations
        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
        .forEach {
            add(it.name, "io.mockative:mockative-processor:2.0.1")
        }
}
