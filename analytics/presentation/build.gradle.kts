plugins {
    alias(libs.plugins.runique.android.feature.ui)
}

android {
    namespace = "com.vector.analytics.presentation"

}

dependencies {

    implementation(projects.analytics.domain)
}