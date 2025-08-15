plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.finanzas_personales.app"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.finanzas_personales.app"
    minSdk = 26
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions { jvmTarget = "1.8" }
  buildFeatures { compose = true }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)

  val room_version = "2.7.2"

  implementation("androidx.room:room-runtime:$room_version")
  implementation("androidx.room:room-ktx:$room_version")
  ksp("androidx.room:room-compiler:$room_version")

  val lifecycle_version = "2.9.2"

  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")

  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.material3:material3-window-size-class")
  implementation("androidx.compose.material:material-icons-extended:1.6.0-alpha06")

  val nav_version = "2.9.3"

  implementation("androidx.navigation:navigation-compose:$nav_version")

  coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
}
