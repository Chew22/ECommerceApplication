plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
}


android {
    namespace = "com.example.ecommerceapplication"
    compileSdk = 34



    defaultConfig {
        applicationId = "com.example.ecommerceapplication"
        minSdk = 26
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        dataBinding = true
        viewBinding = true
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

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-firestore:24.10.2")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    // Slider image
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // Razor payment method
    implementation("com.razorpay:checkout:1.6.33")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // Add the dependency for the Realtime Database library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-database")

    // FirebaseUI for Cloud Firestore
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")



    // Circle Image View
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.rengwuxian.materialedittext:library:2.1.4")
    implementation("com.github.CanHub:Android-Image-Cropper:4.3.0")

    // Add story
    implementation("com.github.shts:StoriesProgressView:3.0.0")

    // Day Night mode Switch
    implementation ("com.github.Mahfa:DayNightSwitch:1.5")

    // StepView
    implementation ("com.github.shuhart:stepview:1.5.1")

    // Flex Box
    implementation ("com.google.android.flexbox:flexbox:3.0.0")

    implementation ("com.squareup.okhttp3:okhttp:4.10.0")


    // Navigation component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Loading button "not working "
    // implementation("br.com.simplepass:loading-button-android:2.2.0")
    implementation("com.github.leandroborgesferreira:loading-button-android:2.3.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.13.0")

    // Circular image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // ViewPager2 indicator "not working "
    // implementation("io.github.vejei.viewpagerindicator:viewpagerindicator:1.0.0-alpha.1")

    // StepView "not working "
    // implementation("com.shuhart.stepview:stepview:1.5.1")

    // Android Ktx
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")

    // Dagger hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")

    // Firebase
    implementation("com.google.firebase:firebase-auth:22.3.1")

    // Coroutines with Firebase
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Color picker
    implementation("com.github.skydoves:colorpickerview:2.2.4")

    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("com.google.android.material:material:1.4.0") // Use the latest version
}



