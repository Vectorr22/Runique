plugins {
    alias(libs.plugins.runique.android.dynamic.feature)
}
android {
    namespace = "com.vector.analytics.analytics_feature"
}

dependencies {
    implementation(project(":app"))
    api(projects.analytics.presentation)
    implementation(libs.androidx.navigation.compose)
    implementation(projects.analytics.domain)
    implementation(projects.analytics.data)
    implementation(projects.core.database)
}