// Application CallNotes : point d'entrée, navigation entre les onglets et
// assemblage de l'injection de dépendances (Hilt).
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "app.callnotes"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        // Identifiant unique sur le téléphone et sur Google Play. Il est basé
        // sur le compte GitHub pour être garanti libre ; modifiable avant la
        // première publication sur Play, plus après.
        applicationId = "io.github.sisi45451.callnotes"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"
    }

    // Deux variantes de distribution (voir docs/architecture.md § 11) :
    // - perso : usage personnel, identification via le journal d'appels
    //   (permission READ_CALL_LOG, restreinte sur Google Play) ;
    // - play  : future version Google Play, sans journal d'appels.
    flavorDimensions += "distribution"
    productFlavors {
        create("perso") {
            dimension = "distribution"
            // Suffixe : les deux variantes peuvent cohabiter sur le téléphone.
            applicationIdSuffix = ".perso"
            versionNameSuffix = "-perso"
            buildConfigField("boolean", "CALL_LOG_ENABLED", "true")
        }
        create("play") {
            dimension = "distribution"
            buildConfigField("boolean", "CALL_LOG_ENABLED", "false")
        }
    }

    signingConfigs {
        // Clé de DEBUG commune, versionnée dans le dépôt : les APK compilés par
        // Android Studio et par GitHub Actions ont la même signature, donc l'un
        // peut mettre à jour l'autre sans désinstaller (et sans perdre les
        // données). Elle ne sert jamais pour Google Play : la version release
        // sera signée par une clé secrète, hors du dépôt (étape 8).
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:security"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:feed"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material3.navigation.suite)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
}
