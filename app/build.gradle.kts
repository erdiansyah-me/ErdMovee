plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //ksp
    id("com.google.devtools.ksp")
    // Safe Args
    id("androidx.navigation.safeargs")
    //parcelize
    id("org.jetbrains.kotlin.plugin.parcelize")

}

android {
    namespace = "com.greildev.erdmovee"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.greildev.erdmovee"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(project(":core"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")

    //testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //recyclerview
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    //viewpager2
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    //glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //lottie
    implementation("com.airbnb.android:lottie:6.3.0")

    //fragment
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    //lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    //fragment navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    //shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    //paging
    api("androidx.paging:paging-runtime-ktx:3.2.1")
    api("androidx.paging:paging-common-ktx:3.2.1")

    //Hilt
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")
    api("androidx.hilt:hilt-navigation:1.2.0")
    api("com.google.dagger:hilt-android:2.50")
}