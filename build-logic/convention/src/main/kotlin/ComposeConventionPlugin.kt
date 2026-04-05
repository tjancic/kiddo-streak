import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            dependencies {
                val bom = platform(libs.findLibrary("compose.bom").get())
                "implementation"(bom)
                "implementation"(libs.findLibrary("compose.ui").get())
                "implementation"(libs.findLibrary("compose.ui.graphics").get())
                "implementation"(libs.findLibrary("compose.ui.tooling.preview").get())
                "implementation"(libs.findLibrary("compose.material3").get())
                "implementation"(libs.findLibrary("compose.animation").get())
                "debugImplementation"(libs.findLibrary("compose.ui.tooling").get())
            }
        }
    }
}

internal val Project.libs
    get() = extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)
        .named("libs")
