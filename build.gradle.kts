// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "9.2.0" apply false
    id("com.android.library") version "9.2.0" apply false
    id("org.jetbrains.kotlin.android") version "2.3.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.21" apply false
    id("com.google.dagger.hilt.android") version "2.59.2" apply false
    id("com.google.devtools.ksp") version "2.3.7" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.21" apply false
    id("org.jetbrains.dokka") version "2.0.0"
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.addAll("-Xjvm-default=all")
        }
    }
}
