plugins {
    id("kiddostreak.android.library")
    id("kiddostreak.koin")
    id("kiddostreak.kotlinx.serialization")
}

dependencies {
    implementation(project(":feature:streak:domain"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(libs.datastore)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.android)
}
