import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.github.cmenzw01"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            modules("java.instrument", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe)
            packageName = "Art Preview Generator"
            packageVersion = "1.0.0"

            val iconsRoot = project.file("src/jvmMain/resources")

            windows {
                menu = true
                iconFile.set(iconsRoot.resolve("windows/design_decor.ico"))
            }
            macOS {
                iconFile.set(iconsRoot.resolve("macos/design_decor.icns"))
            }
        }
    }
}
