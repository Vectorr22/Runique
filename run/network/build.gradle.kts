plugins {
    alias(libs.plugins.runique.android.library)
    alias(libs.plugins.runique.jvm.ktor)
}

android {
    namespace = "com.plcoding.run.network"
}

dependencies {
    implementation(libs.bundles.koin)

    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation("com.amazonaws:aws-android-sdk-s3:2.78.0")
    implementation ("com.amazonaws:aws-android-sdk-core:2.78.0")

}