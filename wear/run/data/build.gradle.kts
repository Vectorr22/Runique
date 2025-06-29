plugins {
    alias(libs.plugins.runique.android.library)
}

android {
    namespace = "com.vector.wear.run.data"
    defaultConfig{
        minSdk = 30
    }
}

dependencies {

    implementation(libs.androidx.health.services.client)
    implementation(libs.bundles.koin)
}