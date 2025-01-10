plugins {
    alias(libs.plugins.runique.android.feature.ui)
    alias(libs.plugins.mapsplatform.secrets.plugin)
}

android {
    namespace = "com.plcoding.run.presentation"
    buildTypes {
        debug {
            buildConfigField("String", "S3_BUCKET_NAME", "\"${project.findProperty("S3_BUCKET_NAME")}\"")
            buildConfigField("String", "S3_ACCESS_KEY", "\"${project.findProperty("S3_ACCESS_KEY")}\"")
            buildConfigField("String", "S3_SECRET_ACCESS_KEY", "\"${project.findProperty("S3_SECRET_ACCESS_KEY")}\"")
        }
        release {
            buildConfigField("String", "S3_BUCKET_NAME", "\"${project.findProperty("S3_BUCKET_NAME")}\"")
            buildConfigField ("String", "S3_ACCESS_KEY", "\"${project.findProperty("S3_ACCESS_KEY")}\"")
            buildConfigField ("String", "S3_SECRET_ACCESS_KEY", "\"${project.findProperty("S3_SECRET_ACCESS_KEY")}\"")
        }
    }
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.google.maps.android.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)
    implementation("com.amazonaws:aws-android-sdk-s3:2.+")
    implementation ("com.amazonaws:aws-android-sdk-core:2.+")
    implementation(projects.core.domain)
    implementation(projects.run.domain)

}