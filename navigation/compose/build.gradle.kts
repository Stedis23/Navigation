import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    `maven-publish`
    id("com.vanniktech.maven.publish") version "0.29.0"
}

tasks.withType<AbstractPublishToMaven>().configureEach {
    val signingTasks = tasks.withType<Sign>()
    mustRunAfter(signingTasks)
}

group = "io.github.stedis23"
version = "0.4.1"

android {
    namespace = "com.stedis.navigation.compose"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {}
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    implementation(kotlin("reflect"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("io.github.stedis23:navigation-core:0.4.1")
}

publishing {
    publications {
        create<MavenPublication>("release") {

            afterEvaluate {
                from(components["release"])
            }

            groupId = project.group.toString()
            artifactId = "navigation-compose"
            version = project.version.toString()

            pom {
                name = "Navigation"
                description = "A library for managing navigation in kotlin project"
                url = "https://github.com/Stedis23/Navigation"

                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/license-2.0.txt"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/Stedis23/Navigation.git"
                    developerConnection = "scm:git:ssh://github.com/Stedis23/Navigation.git"
                    url = "https://github.com/Stedis23/Navigation"
                }

                issueManagement {
                    system = "GitHub"
                    url = "https://github.com/Stedis23/Navigation/issues"
                }

                developers {
                    developer {
                        id = "Stedis23"
                        name = "Stepan Tokarev"
                        email = "stedis02@gmail.com"
                    }
                }
            }
        }

        // Список репозиториев куда публикуются артефакты
        repositories {
            // mavenCentral() // Публикация в Maven Central делается через REST API с помошью отдельного плагина
            mavenLocal()
            // Ищете файлы в директории ~/.m2/repository

            // Репозиторий в build папке корня проекта
            maven(url = uri(rootProject.layout.buildDirectory.file("maven-repo"))) {
                name = "BuildDir"
            }
        }
    }
}

mavenPublishing {
    pom {
        name = "Navigation"
        description = "A library for managing navigation in compose kotlin project"
        url = "https://github.com/Stedis23/Navigation"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/license-2.0.txt"
            }
        }

        scm {
            connection = "scm:git:git://github.com/Stedis23/Navigation.git"
            developerConnection = "scm:git:ssh://github.com/Stedis23/Navigation.git"
            url = "https://github.com/Stedis23/Navigation"
        }

        issueManagement {
            system = "GitHub"
            url = "https://github.com/Stedis23/Navigation/issues"
        }

        developers {
            developer {
                id = "Stedis23"
                name = "Stepan Tokarev"
                email = "stedis02@gmail.com"
            }
        }
    }

    // Публикация в https://central.sonatype.com/
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}