plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.cameraserver"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cameraserver"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(files("libs/car-camera-device-1.0.0-RELEASE.jar"))
    implementation(files("libs/slf4j-android-1.6.1-RC1.jar"))
    implementation(libs.netty)
    implementation(libs.multidex)
    implementation(libs.drawee)
    implementation(libs.fresco)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}