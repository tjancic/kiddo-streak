plugins {
    id("kiddostreak.android.library")
    id("kiddostreak.kotlinx.serialization")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(libs.datastore)
    implementation(libs.kotlinx.coroutines.android)
}
