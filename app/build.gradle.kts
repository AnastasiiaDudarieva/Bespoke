import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

val versionMajor = (project.findProperty("versionMajor") as String).toInt()
val versionMinor = (project.findProperty("versionMinor") as String).toInt()
val versionPatch = (project.findProperty("versionPatch") as String).toInt()
val versionBuild = (project.findProperty("versionBuild") as String).toInt()

fun computeVersionCode(): Int {
    return versionMajor * 1_000_000 + versionMinor * 10_000 + versionPatch * 100 + versionBuild
}

fun computeVersionName(): String {
    return "$versionMajor.$versionMinor.$versionPatch"
}

fun getDate(): String {
    val df = SimpleDateFormat("yyyy-MM-dd")
    df.timeZone = TimeZone.getTimeZone("UTC")
    return df.format(Date())
}

android {
    namespace = "com.bespoke.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bespoke.app"
        minSdk = 31
        targetSdk = 35
        versionCode = computeVersionCode()
        versionName = computeVersionName()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "ENDPOINT", "\"${property("PRODUCTION_ENDPOINT")}\"")
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
            buildConfigField("String", "ENDPOINT", "\"${property("DEV_ENDPOINT")}\"")
        }
    }

    applicationVariants.all {
        outputs.all {
            val appName = when (buildType.name) {
                "release" -> "Bespoke_v.${versionName}_${getDate()}.apk"
                else -> "Bespoke_${buildType.name}.apk"
            }
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                appName
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "2.1.0"
    }

    packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.icons)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage.ktx)

    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.navigation.compose)

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")

    implementation("com.google.dagger:hilt-android:2.56.2")
    implementation(libs.firebase.crashlytics.buildtools)
    ksp("com.google.dagger:hilt-android-compiler:2.56.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.github.CanHub:Android-Image-Cropper:4.4.0")

    implementation("com.google.accompanist:accompanist-placeholder-material:0.30.1")


    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit2.kotlin.coroutines.adapter)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
