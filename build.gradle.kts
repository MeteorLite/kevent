import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.6.10"
    `java-library`
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.5.30"
    id("org.javamodularity.moduleplugin") version "1.8.8"
}

group = "org.rationalityfrontline"
version = "2.1.3"
val NAME = "kevent"
val DESC = "A powerful in-process event dispatcher based on Kotlin and Coroutines"
val GITHUB_REPO = "RationalityFrontline/kevent"

repositories {
    mavenCentral()
}

dependencies {
    val coroutinesVersion = "1.6.0"
    /** Kotlin --------------------------------------------------------- */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$coroutinesVersion")
    /** Logging -------------------------------------------------------- */
    implementation("io.github.microutils:kotlin-logging:2.1.21")
    val spekVersion = "2.0.18"
    /** Logging -------------------------------------------------------- */
    testImplementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${getKotlinPluginVersion()}")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    testRuntimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

tasks {
    withType(JavaCompile::class.java) {
        options.release.set(11)
    }
    withType(KotlinCompile::class.java) {
        kotlinOptions.jvmTarget = "11"
    }
    test {
        testLogging.showStandardStreams = true
        useJUnitPlatform {
            doFirst { classpath.forEach { it.mkdirs() } }
            jvmArgs = listOf(
                "--add-exports", "org.junit.platform.commons/org.junit.platform.commons.util=ALL-UNNAMED",
                "--add-exports", "org.junit.platform.commons/org.junit.platform.commons.logging=ALL-UNNAMED",
                "--add-reads", "kevent=spek.dsl.jvm",
                "--add-reads", "kevent=kotlin.test",
                "--add-reads", "kevent=java.desktop"
            )
            includeEngines("spek2")
        }
    }
    register<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        from(dokkaHtml)
    }
    register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    jar {
        manifest.attributes(mapOf(
            "Implementation-Title" to NAME,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "RationalityFrontline"
        ))
    }
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

tasks.compileKotlin {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()

    kotlinOptions {
        jvmTarget = "1.8"
        apiVersion = "1.6"
        languageVersion = "1.6"
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenLocal") {
            version = this.version
            from(components["kotlin"])
        }
    }
}