import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.openjfx)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

javafx {
    version = libs.versions.javafx.get()
    modules("javafx.controls", "javafx.media")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.jaudiotagger)
    implementation("net.java.dev.jna:jna:5.6.0")
    implementation("net.java.dev.jna:jna-platform:5.6.0")
}

compose.desktop {
    application {
        mainClass = "com.theveloper.pixelplay.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi)
            packageName = "PPlayer Desktop"
            packageVersion = "0.1.0"
            vendor = "KigenJaeger"
            description = "Windows desktop music player companion for PPlayer."
            modules("java.desktop", "java.logging", "jdk.unsupported")

            windows {
                iconFile.set(project.file("src/main/resources/pplayer_icon.ico"))
            }
        }
    }
}
