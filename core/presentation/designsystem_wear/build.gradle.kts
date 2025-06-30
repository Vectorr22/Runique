plugins {
    alias(libs.plugins.runique.android.library.compose)
}

android {
    namespace = "com.vector.core.presentation.designsystem_wear"


    defaultConfig {
        minSdk = 30
        compileSdk = 35
    }


}

dependencies {
    api(projects.core.presentation.designsystem)

    implementation(libs.androidx.wear.compose.material)
}