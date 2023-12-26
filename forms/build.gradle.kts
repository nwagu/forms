plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.dokka")
    id("maven-publish")
    id("signing")
}

group = "com.nwagu.forms"
version = "2.0.0-alpha09"

kotlin {
    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    jvm()
    mingwX64()
    linuxX64()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
        macosX64(),
        tvosArm64(),
        tvosX64(),
        watchosArm32(),
        watchosArm64(),
        watchosX64()
    ).forEach {
        it.binaries.framework {
            baseName = "forms"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
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

val dokkaOutputDir = "${buildDir}/dokka"

tasks.dokkaHtml {
    outputDirectory.set(file(dokkaOutputDir))
}

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}

// Fix Gradle warning about signing tasks using publishing task outputs without explicit dependencies
// https://github.com/gradle/gradle/issues/26091
tasks.withType<AbstractPublishToMaven>().configureEach {
    val signingTasks = tasks.withType<Sign>()
    mustRunAfter(signingTasks)
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            setUrl { "https://oss.sonatype.org/service/local/staging/deploy/maven2/" }
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
    }
    publications {
        withType<MavenPublication> {
            artifact(javadocJar)

            pom {
                name = "forms"
                description = "Kotlin multiplatform library for form management and validation"
                url = "https://github.com/nwagu/forms"

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }
                developers {
                    developer {
                        name = "Chukwuemeka Nwagu"
                        email = "developer.nwagu@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:github.com/nwagu/forms.git"
                    developerConnection = "scm:git:ssh://github.com/nwagu/forms.git"
                    url = "https://github.com/nwagu/forms/tree/main"
                }
            }
        }
    }

}

signing {
    isRequired = true
    sign(publishing.publications)
}