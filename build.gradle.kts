plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.googleGmsGoogleServices) apply false
}

// Force all io.ktor artifacts to 3.2.2 so supabase-kt's transitive 3.2.0 pull is overridden.
subprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "io.ktor") {
                useVersion(libs.versions.ktor.get())
                because("Align ktor with the declared catalog version")
            }
        }
    }
}