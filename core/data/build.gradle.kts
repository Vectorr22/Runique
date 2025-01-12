import java.util.Properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
plugins {
    alias(libs.plugins.runique.android.library)
    alias(libs.plugins.runique.jvm.ktor)
}
android {
    namespace = "com.plcoding.core.data"
    defaultConfig {
        buildConfigField("String", "S3_ACCESS_KEY", "\"${localProperties["S3_ACCESS_KEY"]}\"")
        buildConfigField("String", "S3_SECRET_ACCESS_KEY", "\"${localProperties["S3_SECRET_ACCESS_KEY"]}\"")
        buildConfigField("String", "S3_BUCKET_NAME", "\"${localProperties["S3_BUCKET_NAME"]}\"")
    }
}

dependencies {
    implementation(libs.timber)
    implementation(libs.bundles.koin)
    implementation(projects.core.domain)
    implementation(projects.core.database)
    implementation("aws.sdk.kotlin:s3:1.3.110")

}


