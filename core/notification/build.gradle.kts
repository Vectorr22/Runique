plugins {
    alias(libs.plugins.runique.android.library)
}

android {
    namespace = "com.vector.core.notification"
    compileSdk = 35


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bundles.koin)
    implementation(projects.core.presentation.ui)
    implementation(projects.core.presentation.designsystem)
}