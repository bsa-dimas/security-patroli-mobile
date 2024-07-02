plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinAndroidKsp)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.hiltAndroid)
}

android {
    namespace = "com.bsalogistics.securitypatroli"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bsalogistics.securitypatroli"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            buildConfigField("Boolean", "IS_DEBUG", "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            buildConfigField("Boolean", "IS_DEBUG", "true")
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
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
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.play.services.mlkit.text.recognition)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.multidex)
    implementation(libs.material)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)

    // landscapist
    implementation(libs.landscapist.coil)
    implementation(libs.landscapist.placeholder)
    implementation(libs.landscapist.animation)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    implementation(libs.timber)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.7.0-beta02")

    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")

    implementation("androidx.camera:camera-camera2:1.2.2")
    implementation("androidx.camera:camera-lifecycle:1.2.2")
    implementation("androidx.camera:camera-view:1.2.2")

    implementation ("com.google.zxing:core:3.4.0")
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")

    implementation("network.chaintech:qr-kit:1.0.2")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation ("id.zelory:compressor:3.0.1")
}