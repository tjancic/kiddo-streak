plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "kiddostreak.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "kiddostreak.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "kiddostreak.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("domainModule") {
            id = "kiddostreak.domain.module"
            implementationClass = "DomainModuleConventionPlugin"
        }
        register("compose") {
            id = "kiddostreak.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("koin") {
            id = "kiddostreak.koin"
            implementationClass = "KoinConventionPlugin"
        }
        register("serialization") {
            id = "kiddostreak.kotlinx.serialization"
            implementationClass = "SerializationConventionPlugin"
        }
    }
}
