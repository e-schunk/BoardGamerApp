plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.firebase.crashlytics")
    id("com.github.triplet.play") version "3.12.1"
    id("com.google.gms.google-services")
}

android {
    namespace = "de.eduardschunk.boardplay"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.eduardschunk.boardplay"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val storeFilePath = System.getenv("SIGNING_STORE_FILE")
                ?: error("SIGNING_STORE_FILE fehlt")
            storeFile = file(storeFilePath)
            storePassword = System.getenv("SIGNING_STORE_PASSWORD") ?: error("SIGNING_STORE_PASSWORD fehlt")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS") ?: error("SIGNING_KEY_ALIAS fehlt")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD") ?: error("SIGNING_KEY_PASSWORD fehlt")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release") // nicht findByName
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

play {
    // In GitHub-Action erzeugte Datei
    serviceAccountCredentials.set(file("play-service-account.json"))

    // Track = Zielbereich in der Play Console (hier: interner Test)
    track.set("internal")

    // Immer .aab statt .apk
    defaultToAppBundles.set(true)

    // Release sofort abschließen
    releaseStatus.set(com.github.triplet.gradle.androidpublisher.ReleaseStatus.COMPLETED)

    // Vorhandene Drafts automatisch überschreiben
    resolutionStrategy.set(
        com.github.triplet.gradle.androidpublisher.ResolutionStrategy.AUTO
    )
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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.database)
    implementation(libs.androidx.material.icons.extended)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.firebase.analytics)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}