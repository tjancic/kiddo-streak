import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(AndroidLibraryConventionPlugin::class.java)
            pluginManager.apply(ComposeConventionPlugin::class.java)
            pluginManager.apply(KoinConventionPlugin::class.java)

            dependencies {
                "implementation"(libs.findLibrary("lifecycle.viewmodel.compose").get())
                "implementation"(libs.findLibrary("lifecycle.runtime.compose").get())
                "implementation"(libs.findLibrary("navigation.compose").get())
                "implementation"(libs.findLibrary("compose.material.icons.extended").get())
            }
        }
    }
}
