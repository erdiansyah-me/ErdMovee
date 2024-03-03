plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    //ksp
    id("com.google.devtools.ksp")
    //google and firebase
    id("com.google.firebase.crashlytics")
    //parcelize
    id("org.jetbrains.kotlin.plugin.parcelize")
    //hilt
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.greildev.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    api("com.google.firebase:firebase-database-ktx")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")

    //Room
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    api("androidx.room:room-paging:2.6.1")

    //hilt
    api("com.google.dagger:hilt-android:2.50")
    ksp("androidx.hilt:hilt-compiler:1.2.0")
    ksp("com.google.dagger:hilt-compiler:2.50")

    //paging
    api("androidx.paging:paging-runtime-ktx:3.2.1")
    api("androidx.paging:paging-common-ktx:3.2.1")

    //DataStore
    implementation("androidx.datastore:datastore-core:1.0.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}