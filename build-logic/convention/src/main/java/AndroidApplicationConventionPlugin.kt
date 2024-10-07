import com.android.build.api.dsl.ApplicationExtension
import com.plcoding.convention.ExtensionType
import com.plcoding.convention.configureBuildTypes
import com.plcoding.convention.configureKotlinAndroid
import com.plcoding.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.Properties


class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
//            val localPropeties = Properties().apply {
//                val file = rootProject.file("local.properties")
//                if(file.exists()){
//                    load(file.inputStream())
//                }
//            }
//            val mapsApiKey = localPropeties.getProperty("MAPS_API_KEY") ?: ""
            pluginManager.run {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    applicationId = libs.findVersion("projectApplicationId").get().toString()
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()

                    versionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
                    versionName = libs.findVersion("projectVersionName").get().toString()
                    manifestPlaceholders["MAPS_API_KEY"] = ""
                }

                configureKotlinAndroid(this)

                configureBuildTypes(
                    commonExtension = this,
                    extensionType = ExtensionType.APPLICATION
                )
            }
        }
    }
}