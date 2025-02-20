import com.android.build.api.dsl.Lint
import com.android.build.api.dsl.LintOptions
import com.android.build.api.dsl.ManagedVirtualDevice
import dev.icerock.gradle.MRVisibility
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
    kotlin("native.cocoapods")
    id("com.google.gms.google-services")
    id("dev.icerock.mobile.multiplatform-resources") version "0.24.1"
    
}


kotlin {
    
    cocoapods {
        version = "1.0"
        summary = "Compose app"
        homepage = "not published"
        ios.deploymentTarget = "13.0"
        
        pod("WebRTC-SDK") {
            version = libs.versions.webrtc.ios.sdk.get()
            moduleName = "WebRTC"
            packageName = "WebRTC"
        }
    }
    
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "${JavaVersion.VERSION_1_8}"
                freeCompilerArgs += "-Xjdk-release=${JavaVersion.VERSION_1_8}"
            }
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)
            dependencies {
                debugImplementation(libs.androidx.testManifest)
                implementation(libs.androidx.junit4)
            }
        }
    }
    
//    listOf(
//        iosX64 { configureWebRtcCinterops() },
//        iosArm64 { configureWebRtcCinterops() },
//        iosSimulatorArm64 { configureWebRtcCinterops() }
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            export("io.github.mirzemehdi:kmpnotifier:1.0.0")
//            export(libs.resources)
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//    }
    
    listOf(
        iosX64 { configureWebRtcCinterops() },
        iosArm64 { configureWebRtcCinterops() },
        iosSimulatorArm64 { configureWebRtcCinterops() }
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true // Уберите, если проблемы сохраняются
            export("io.github.mirzemehdi:kmpnotifier:1.0.0")
            export("com.example:resources:1.0.0") // Замените, если `libs.resources` не работает
        }
    }
    
    sourceSets {
        
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.material)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)
            implementation(libs.cafe.voyager.tab.navigator)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.composeImageLoader)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.moko.mvvm)
            implementation(libs.ktor.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.multiplatformSettings)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.stately.common)
            implementation(libs.webrtc.kmp)
            implementation(libs.kermit)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json.v132)
            implementation(libs.kotlinx.datetime)
            implementation(libs.peekaboo.ui)
            implementation(libs.peekaboo.image.picker)
            implementation(libs.sonner)
            api(libs.image.loader.v181)
            api(libs.kmpnotifier)
            implementation(libs.firebase.common)
            implementation(libs.filekit.core)
            implementation(libs.coil.compose.core)
            implementation(libs.coil.compose)
            implementation(libs.coil.mp)
            implementation(libs.coil.network.ktor)
            api(libs.resources)
            api(libs.resources.compose) // for compose multiplatfor
            implementation(libs.compressor)
        }

        //Для версии приложения
        commonMain {
            val versionName = project.findProperty("VERSION_NAME") as String
            kotlin.srcDir("build/generated/kotlin")
        }
        
        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.kotlinx.coroutines.test)
        }
        
        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.messaging.ktx)
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.lifecycle.process)
            implementation(libs.itext7.core) // Добавлено для работы с PDF
            implementation(libs.lz4.java)
        }
        
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}


tasks.register("generateBuildConfig") {
    val versionName = project.findProperty("VERSION_NAME") ?: "1.1.0"

    val outputDir = File(buildDir, "generated/kotlin")
    outputDir.mkdirs()

    val buildConfigFile = File(outputDir, "BuildConfig.kt")
    buildConfigFile.writeText("""
        object BuildConfig {
            const val VERSION_NAME = "$versionName"
        }
    """.trimIndent())
}

android {
    namespace = "org.videotrade.shopot"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 27
        targetSdk = 34
        
        applicationId = "org.videotrade.shopot.androidApp"
        versionCode = 21
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        externalNativeBuild {
            cmake {
                cppFlags += ""
                arguments += listOf("-DANDROID_TOOLCHAIN=clang")
                abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            }
        }
    }
    
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/res")
    }
    
    
    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices {
            devices {
                maybeCreate<ManagedVirtualDevice>("pixel5").apply {
                    device = "Pixel 5"
                    apiLevel = 34
                    systemImageSource = "aosp"
                }
            }
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    
    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

multiplatformResources {
    resourcesPackage.set("org.videotrade.shopot") // required
    resourcesClassName.set("MokoRes") // optional, default MR
}


dependencies {
    implementation(libs.androidx.animation.core.android)
    implementation(libs.androidx.core.i18n)
}

buildConfig {
    // BuildConfig configuration here.
    // https://github.com/gmazzo/gradle-buildconfig-plugin#usage-in-kts
}

fun KotlinNativeTarget.configureWebRtcCinterops() {
    val webRtcFrameworkPath = file("$buildDir/cocoapods/synthetic/IOS/Pods/WebRTC-SDK")
        .resolveArchPath(konanTarget, "WebRTC")
    compilations.getByName("main") {
        cinterops.getByName("WebRTC") {
            compilerOpts("-framework", "WebRTC", "-F$webRtcFrameworkPath")
        }
    }
    
    binaries {
        getTest("DEBUG").apply {
            linkerOpts(
                "-framework",
                "WebRTC",
                "-F$webRtcFrameworkPath",
                "-rpath",
                "$webRtcFrameworkPath",
                "-ObjC"
            )
        }
    }
}

fun File.resolveArchPath(target: KonanTarget, framework: String): File? {
    val archPaths = resolve("$framework.xcframework")
        .listFiles { _, name -> target.matches(name) }
        ?: return null
    
    check(archPaths.size == 1) { "Resolving framework '$framework' arch path failed: $archPaths" }
    
    return archPaths.first()
}

private fun KonanTarget.matches(dir: String): Boolean {
    return when (this) {
        KonanTarget.IOS_SIMULATOR_ARM64,
        KonanTarget.IOS_X64 -> dir.startsWith("ios") && dir.endsWith("simulator")
        KonanTarget.IOS_ARM64 -> dir.startsWith("ios-arm64") && !dir.contains("x86")
        else -> error("Unsupported target $name")
    }
}
