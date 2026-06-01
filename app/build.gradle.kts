plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("jacoco")
}

android {
    namespace = "com.example.medireminder"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.medireminder"
        minSdk = 24
        targetSdk = 35
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
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    jvmToolchain(11)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Accompanist Permissions
    implementation(libs.accompanist.permissions)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// JaCoCo coverage configuration
jacoco {
    toolVersion = "0.8.12"
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "**/di/**",
        "**/hilt_/**",
        "**/Hilt_*",
        "**/databinding/**",
        "**/generated/**",
        "**/android/databinding/**",
        "**/data/datastore/**",
        "**/data/mapper/**",
        "**/*_Factory*",
        "**/*Module*",
        "**/*EntryPoint*",
        "**/*_MembersInjector*",
        "**/*_Impl*",
        "**/*Activity*",
        "**/*Application*",
        "**/core/alarm/AlarmConstants*",
        "**/core/alarm/RescheduleResult*",
        "**/core/background/*",
        "**/core/common/*",
        "**/core/notification/*",
        "**/core/permissions/*"
    )

    val kotlinClasses = fileTree("$buildDir/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val javaClasses = fileTree("$buildDir/intermediates/javac/debug/classes") {
        exclude(fileFilter)
    }

    classDirectories.setFrom(kotlinClasses, javaClasses)
    executionData.setFrom(fileTree(buildDir) {
        include("jacoco/*.exec", "outputs/code-coverage/connected/*.ec")
    })
    sourceDirectories.setFrom(files("$projectDir/src/main/java"))
}

tasks.check {
    dependsOn("jacocoTestReport")
}
