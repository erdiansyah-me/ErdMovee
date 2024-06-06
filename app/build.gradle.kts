import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //ksp
    id("com.google.devtools.ksp")
    // Safe Args
    id("androidx.navigation.safeargs")
    //parcelize
    id("org.jetbrains.kotlin.plugin.parcelize")
    //google and firebase
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    //hilt
    id("dagger.hilt.android.plugin")
    //detekt
    id("io.gitlab.arturbosch.detekt")
    id("jacoco")
}

private val coverageExclusions = listOf(
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*"
)

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    configure<JacocoPluginExtension> {
        toolVersion = "0.8.10"
    }

    val jacocoTestReport = tasks.create("jacocoTestReport")

    androidComponents.onVariants { variant ->
        val testTaskName = "test${variant.name.capitalize()}UnitTest"

        val reportTask =
            tasks.register("jacoco${testTaskName.capitalize()}Report", JacocoReport::class) {
                dependsOn(testTaskName)

                reports {
                    html.required.set(true)
                }

                classDirectories.setFrom(
                    fileTree("$buildDir/tmp/kotlin-classes/${variant.name}") {
                        exclude(coverageExclusions)
                    }
                )

                sourceDirectories.setFrom(
                    files("$projectDir/src/main/java")
                )
                executionData.setFrom(file("$buildDir/jacoco/$testTaskName.exec"))
//                executionData.setFrom(file("$buildDir/outputs/unit_test_code_coverage/${variant.name}UnitTest/$testTaskName.exec"))
            }

        jacocoTestReport.dependsOn(reportTask)
    }

    tasks.withType<Test>().configureEach {
        configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }

    detekt {
        toolVersion = "1.23.3"
        buildUponDefaultConfig = true // preconfigure defaults
        allRules = false // activate all available (even unstable) rules.
        config.setFrom(file("$projectDir/config/detekt.yml"))// point to your custom config defining rules to run, overwriting default behavior
        baseline =
            file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
    }
    tasks.withType<Detekt>().configureEach {
        reports {
            html.required.set(true) // observe findings in your browser with structure and code snippets
            xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
            txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
            sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
            md.required.set(true) // simple Markdown format
        }
    }
    // Kotlin DSL
    tasks.withType<Detekt>().configureEach {
        jvmTarget = "17"
    }
    tasks.withType<DetektCreateBaselineTask>().configureEach {
        jvmTarget = "17"
    }
}

dependencies {

    api(project(":core"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("junit:junit:4.13.2")

    //Firebase
    api(platform("com.google.firebase:firebase-bom:32.7.2"))
    api("com.google.firebase:firebase-crashlytics")
    api("com.google.firebase:firebase-analytics")
    api("com.google.firebase:firebase-config-ktx")
    api("com.google.firebase:firebase-messaging-ktx")
    api("com.google.firebase:firebase-storage-ktx")

    //testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //mockito
    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito:mockito-inline:4.4.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    androidTestImplementation("org.mockito:mockito-core:5.4.0")

    //recyclerview
    api("androidx.recyclerview:recyclerview:1.3.2")

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

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    //test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    //paging
    api("androidx.paging:paging-runtime-ktx:3.2.1")
    api("androidx.paging:paging-common-ktx:3.2.1")

    //Hilt
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")
    api("androidx.hilt:hilt-navigation:1.2.0")
    api("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")

    //leakCanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")

    //detekt
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:1.23.5")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-ruleauthors:1.23.5")
}