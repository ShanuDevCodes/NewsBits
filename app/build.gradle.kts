import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.devtools.ksp")
}

val localProps = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        load(FileInputStream(localPropsFile))
    }
}

android {
    namespace = "com.shanudevcodes.newsbits"
    compileSdk = 36


    defaultConfig {
        applicationId = "com.shanudevcodes.newsbits"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "4.1.1-Beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "ALGOLIA_APP_ID", "\"${localProps.getProperty("ALGOLIA_APP_ID")}\"")
        buildConfigField("String", "ALGOLIA_SEARCH_KEY", "\"${localProps.getProperty("ALGOLIA_SEARCH_KEY")}\"")
        buildConfigField("String", "ALGOLIA_INDEX", "\"${localProps.getProperty("ALGOLIA_INDEX")}\"")
        buildConfigField("String", "Gemini_API_Key", "\"${localProps.getProperty("Gemini_API_Key")}\"")
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        compose = true
        buildConfig = true
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
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.coil.compose)
    implementation (libs.accompanist.swiperefresh)
    implementation(libs.androidx.paging.compose)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.gson)
    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.androidx.adaptive.navigation)
    implementation(platform("com.algolia:algoliasearch-client-kotlin-bom:3.24.2"))
    implementation("com.algolia:algoliasearch-client-kotlin")
    implementation(platform("io.ktor:ktor-bom:2.3.2"))
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-okhttp")
    implementation("io.ktor:ktor-client-plugins")
    implementation("com.airbnb.android:lottie-compose:6.6.7")
}