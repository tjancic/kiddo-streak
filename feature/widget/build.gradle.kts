plugins {
    id("kiddostreak.android.library")
    id("kiddostreak.koin")
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(project(":feature:streak:domain"))
    implementation(project(":core:domain"))
    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
}
