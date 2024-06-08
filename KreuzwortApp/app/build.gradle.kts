plugins{
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //id("io.ktor.plugin") version "2.3.10"
    kotlin("plugin.serialization") version "2.0.0"
}

android{
    namespace = "de.agrothe.kreuzwortapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "de.agrothe.kreuzwortapp"
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
            //pickFirsts += "/resources/**"
            pickFirsts += "/data/**"
            pickFirsts += "/imgs/**"
            //pickFirsts += "/puzzles/**"
            pickFirsts += "/puzzles/generated/**"
        }
    }

    sourceSets{
        getByName("main"){
            assets{
                //srcDirs("src\\main\\resources")
                srcDirs("src\\main\\assets\\data")
                srcDirs("src\\main\\assets\\imgs")
                srcDirs("src\\main\\assets\\puzzles\\generated")
            }
        }
    }
}

dependencies{
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    //implementation("androidx.webkit:webkit:1.11.0")

    implementation("org.slf4j:slf4j-simple:2.0.12")
    implementation("io.ktor:ktor-server-html-builder-jvm:2.3.11")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.10.1")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.11")
    implementation("io.github.config4k:config4k:0.6.0")
    implementation("org.jetbrains:kotlin-css-jvm:1.0.0-pre.129-kotlin-1.4.20")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.10")
    implementation("io.ktor:ktor-server-websockets:2.3.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("net.jodah:expiringmap:0.5.11")
}
kotlin{
    jvmToolchain(19)
}