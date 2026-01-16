plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val revenueCatApiKey: String = project.findProperty("REVENUECAT_API_KEY") as? String
    ?: throw GradleException(
        """
        |
        |RevenueCat API key not configured!
        |
        |To configure the API key:
        |1. Open gradle.properties in the project root
        |2. Replace 'must-inject' with your RevenueCat Google Play API key:
        |   REVENUECAT_API_KEY=goog_xxx
        |
        |Get your API key from: https://app.revenuecat.com
        |
        """.trimMargin()
    )

if (revenueCatApiKey == "must-inject") {
    throw GradleException(
        """
        |
        |RevenueCat API key not configured!
        |
        |To configure the API key:
        |1. Open gradle.properties in the project root
        |2. Replace 'must-inject' with your RevenueCat Google Play API key:
        |   REVENUECAT_API_KEY=goog_xxx
        |
        |Get your API key from: https://app.revenuecat.com
        |
        """.trimMargin()
    )
}

val entitlementId: String = project.findProperty("REVENUECAT_ENTITLEMENT_ID") as? String ?: "premium_plus"
val applicationIdProperty: String = project.findProperty("APPLICATION_ID") as? String ?: "com.revenuecat.samplecat"

android {
    namespace = applicationIdProperty
    compileSdk = 35

    defaultConfig {
        applicationId = applicationIdProperty
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "REVENUECAT_API_KEY", "\"$revenueCatApiKey\"")
        buildConfigField("String", "ENTITLEMENT_ID", "\"$entitlementId\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.revenuecat.purchases)

    debugImplementation(libs.androidx.ui.tooling)
}
