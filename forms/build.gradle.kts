plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    androidTarget()

    listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "forms"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        }
        commonTest.dependencies {
            // add junit test dependencies here
            implementation("org.jetbrains.kotlin:kotlin-test-common")
            implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            implementation("org.jetbrains.kotlin:kotlin-test")
            implementation("org.jetbrains.kotlin:kotlin-test-junit")
        }
    }
}

android {
    namespace = "com.nwagu.forms"
    compileSdk = 34
    defaultConfig {
        minSdk = 17
    }
    kotlin {
        jvmToolchain(17)
    }
}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}

//ext {
//    publishGroupId = 'com.nwagu.forms'
//    publishArtifactId = 'forms'
//    publishVersion = '1.0.3'
//
//    libraryName = 'forms'
//    libraryDescription = 'Kotlin multiplatform library for form management and validation'
//}

//if (project.rootProject.file('local.properties').exists()) {
//    apply from: "${rootProject.projectDir}/publish-mavencentral.gradle"
//}
