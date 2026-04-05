plugins {
    id("kiddostreak.android.library")
    id("kiddostreak.koin")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":feature:streak:domain"))
    implementation(libs.core.ktx)
    implementation(libs.workmanager)
    implementation(libs.koin.androidx.workmanager)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
}
