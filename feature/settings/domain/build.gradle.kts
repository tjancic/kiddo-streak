plugins {
    id("kiddostreak.domain.module")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":feature:streak:domain"))
}
