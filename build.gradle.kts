plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0" apply false
}

subprojects {
    tasks.matching {
        it.name == "runKtlintCheckOverKotlinScripts"
    }.configureEach {
        enabled = false
    }
}