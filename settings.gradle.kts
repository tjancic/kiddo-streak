pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "KiddoStreak"

include(":app")

include(":core:domain")
include(":core:data")
include(":core:presentation")
include(":core:design-system")
include(":core:notifications")

include(":feature:streak:domain")
include(":feature:streak:data")
include(":feature:streak:presentation")

include(":feature:settings:domain")
include(":feature:settings:data")
include(":feature:settings:presentation")

include(":feature:widget")
