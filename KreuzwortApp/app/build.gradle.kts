plugins{
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //id("io.ktor.plugin") version "2.3.10"
    kotlin("plugin.serialization") version "2.0.0"
}

android{
    namespace="de.agrothe.kreuzwortapp"
    compileSdk=34

    defaultConfig{
        applicationId = "de.agrothe.kreuzwortapp"
        minSdk=29
        //noinspection OldTargetApi
        targetSdk=34
        versionCode=6
        versionName="1.6"

        testInstrumentationRunner="androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables{
            useSupportLibrary=true
        }
    }

    buildTypes{
        release{
            isMinifyEnabled=false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
            isDebuggable=false
        }
    }
    compileOptions{
        sourceCompatibility=JavaVersion.VERSION_1_8
        targetCompatibility=JavaVersion.VERSION_1_8
    }
    kotlinOptions{
        jvmTarget="1.8"
    }
    buildFeatures{
        compose=true
    }
    composeOptions{
        kotlinCompilerExtensionVersion="1.5.1"
    }
    packaging{
        resources{
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
        }
    }
}

dependencies{
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("org.slf4j:slf4j-simple:2.0.12")
    implementation("io.ktor:ktor-server-html-builder-jvm:2.3.11")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.10.1")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.12")
    /*implementation("io.ktor:ktor-server-jetty-jvm:2.3.12")*/
    implementation("io.github.config4k:config4k:0.6.0")
    implementation("org.jetbrains:kotlin-css-jvm:1.0.0-pre.156-kotlin-1.5.0")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.10")
    implementation("io.ktor:ktor-server-websockets:2.3.10")
    implementation("io.ktor:ktor-network-tls-certificates:2.3.12")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("net.jodah:expiringmap:0.5.11")
}
kotlin{
    jvmToolchain(21)
}