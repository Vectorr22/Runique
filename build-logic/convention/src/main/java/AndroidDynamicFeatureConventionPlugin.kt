import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.plcoding.convention.ExtensionType
import com.plcoding.convention.addUiLayerDependencies
import com.plcoding.convention.configureAndroidCompose
import com.plcoding.convention.configureBuildTypes
import com.plcoding.convention.configureKotlinAndroid
import com.plcoding.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import java.util.Properties


class AndroidDynamicFeatureConventionPlugin : Plugin<Project> {

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
                apply("com.android.dynamic-feature")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<DynamicFeatureExtension> {
                configureKotlinAndroid(this)
                configureAndroidCompose(this)


                configureBuildTypes(
                    commonExtension = this,
                    extensionType = ExtensionType.DYNAMIC_FEATURE
                )
            }
            dependencies {
                addUiLayerDependencies(target)
                "testImplementation"(kotlin("test"))
            }
        }
    }
}