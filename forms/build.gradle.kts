plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.dokka")
    id("maven-publish")
    id("signing")
}

kotlin {

    androidTarget {
        publishLibraryVariants("release", "debug")
    }

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
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        }
        commonTest.dependencies {
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

val dokkaOutputDir = "${layout.buildDirectory}/dokka"

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

// begin region
// Fix Gradle warning about signing tasks using publishing task outputs without explicit dependencies
// https://github.com/gradle/gradle/issues/26091
tasks.withType<AbstractPublishToMaven>().configureEach {
    val signingTasks = tasks.withType<Sign>()
    mustRunAfter(signingTasks)
}
tasks.withType<Sign>().configureEach {
    val bundlingTasks = tasks.withType<AbstractArchiveTask>()
    mustRunAfter(bundlingTasks)
}
// end region

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
            groupId = "com.nwagu.forms"
            artifactId = "forms"
            version = "2.0.0-alpha08"

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
                    url = "https://github.com/nwagu/forms/tree/master"
                }
            }
        }
    }

}

signing {
    sign(publishing.publications)
}