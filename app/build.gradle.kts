plugins {
    id("kiddostreak.android.application")
    id("kiddostreak.compose")
    id("kiddostreak.koin")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:presentation"))
    implementation(project(":core:design-system"))
    implementation(project(":core:notifications"))

    implementation(project(":feature:streak:domain"))
    implementation(project(":feature:streak:data"))
    implementation(project(":feature:streak:presentation"))

    implementation(project(":feature:settings:domain"))
    implementation(project(":feature:settings:data"))
    implementation(project(":feature:settings:presentation"))

    implementation(project(":feature:widget"))

    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.android)
}
