plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.demosrobotsciente"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.demosrobotsciente"
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //  esta es la libreria para los GET y los POST.
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    //  esto nos ayuda a coger un JSON y convertirlo en una clase de Kotlin, es decir pasar de JSON a data class y vicebersa.
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // esto es para los corrutinas que hacen la peticion GET o POST al servidor únicamente cada vez que se pulsa el botón, es decir, cuando se ejecuta las lineas que contiene "runBlocking"
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.6")


    // esta dependencia la uso para poder escanera con la cámara del teléfono el código QR del evento.
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    // esta dependencia la uso para la libreria "okhttp" que es una variable en la que se almacenan los resultados de cada una de las peticiones.
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // esto es para las corrutinas del activity, que hacen espera activa al servidor de FLASK.
    implementation ("androidx.activity:activity-ktx:1.7.1")
}