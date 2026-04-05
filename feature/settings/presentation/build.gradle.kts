plugins {
    id("kiddostreak.android.feature")
    id("kiddostreak.kotlinx.serialization")
}

dependencies {
    implementation(project(":feature:settings:domain"))
    implementation(project(":feature:streak:domain"))
    implementation(project(":core:domain"))
    implementation(project(":core:presentation"))
    implementation(project(":core:design-system"))
    implementation(project(":core:notifications"))
}
