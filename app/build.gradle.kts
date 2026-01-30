plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.travellog"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.travellog"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Room Database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // Google Services (Location)
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Coil (do wyświetlania zdjęć)
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Icons
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // Accompanist (Permissions)
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    implementation("androidx.compose.material3:material3:1.3.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

}
