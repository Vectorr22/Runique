plugins {
    alias(libs.plugins.runique.android.feature.ui)
    alias(libs.plugins.mapsplatform.secrets.plugin)
}

android {
    namespace = "com.plcoding.run.presentation"

}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.google.maps.android.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)
//    implementation("com.amazonaws:aws-android-sdk-s3:2.78.0")
//    implementation("aws.sdk.kotlin:s3:2.78.0")
//    implementation ("com.amazonaws:aws-android-sdk-core:2.78.0")
    implementation(projects.core.domain)
    implementation(projects.run.domain)
    implementation(projects.core.connectivity.domain)

}