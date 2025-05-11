plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.chatmate.kids"
    compileSdk = 35

    val GEMINI_API_KEY: String = if (project.hasProperty("GEMINI_API_KEY")) {
        "\"${project.property("GEMINI_API_KEY")}\""
    } else {
        "\"\""
    }

    defaultConfig {
        applicationId = "com.chatmate.kids"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "GEMINI_API_KEY", "\"${project.properties["GEMINI_API_KEY"]}\"")
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
    buildFeatures {
        buildConfig = true
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Retrofit + OkHttp için:
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation("com.google.firebase:firebase-auth:23.2.0")
    apply(plugin = "com.google.gms.google-services")
    implementation ("com.airbnb.android:lottie:6.1.0")

}


