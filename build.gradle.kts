/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import tech.antibytes.gradle.dependency.Version
import tech.antibytes.gradle.kmock.config.KMockPublishingConfiguration
import tech.antibytes.gradle.kmock.dependency.addCustomRepositories
import tech.antibytes.gradle.kmock.dependency.ensureKotlinVersion

plugins {
    id("tech.antibytes.gradle.kmock.dependency")

    id("tech.antibytes.gradle.dependency")

    id("tech.antibytes.gradle.coverage")

    id("tech.antibytes.gradle.kmock.script.quality-spotless")

    id("org.owasp.dependencycheck")

    id("tech.antibytes.gradle.publishing")

    id("io.gitlab.arturbosch.detekt") version "1.20.0"

    id("org.sonarqube") version "3.3"
}

antiBytesPublishing {
    versioning = KMockPublishingConfiguration.versioning
    repositoryConfiguration = KMockPublishingConfiguration.repositories
}

allprojects {
    repositories {
        addCustomRepositories()
        mavenCentral()
        google()
        jcenter()
    }

    ensureKotlinVersion(Version.kotlin.language)
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "7.4.2"
    distributionType = Wrapper.DistributionType.ALL
}

detekt {
    toolVersion = "1.20.0"
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    config = files("$projectDir/detekt/config.yml") // point to your custom config defining rules to run, overwriting default behavior
    baseline = file("$projectDir/detekt/baseline.xml") // a way of suppressing issues before introducing detekt
    source = files(projectDir)
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = JavaVersion.VERSION_11.toString()
    exclude(
        "**/.gradle/**",
        "**/.idea/**",
        "**/build/**",
        ".github/**",
        "gradle/**",
        "**/example/**",
        "**/test/resources/**"
    )

    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(false)
        sarif.required.set(false)
    }
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = JavaVersion.VERSION_11.toString()

    exclude(
        "**/.gradle/**",
        "**/.idea/**",
        "**/build/**",
        "**/gradle/wrapper/**",
        ".github/**",
        "assets/**",
        "docs/**",
        "gradle/**",
        "**/example/**",
        "**/*.adoc",
        "**/*.md",
        "**/gradlew",
        "**/LICENSE",
        "**/.java-version",
        "**/gradlew.bat",
        "**/*.png",
        "**/*.properties",
        "**/*.pro",
        "**/*.sq",
        "**/*.xml",
        "**/*.yml",
    )
}

val sources = listOf(
    "$projectDir/kmock/src/commonMain",
    "$projectDir/kmock/src/jvmMain",
    "$projectDir/kmock-gradle/src/main",
    "$projectDir/kmock-processor/src/main"
)

val tests = listOf(
    "$projectDir/kmock/src/commonTest",
    "$projectDir/kmock/src/jvmTest",
    "$projectDir/kmock-gradle/src/main",
    "$projectDir/kmock-processor/src/main"
)

val jacocoReports = listOf(
    "$projectDir/kmock/build/reports/jacoco/jvm/kmock.xml",
    "$projectDir/kmock-gradle/build/reports/jacoco/jvm/kmock-gradle.xml",
    "$projectDir/kmock-processor/build/reports/jacoco/jvm/kmock-processor.xml"
)

sonarqube {
    properties {
        property("sonar.projectKey", "kmock")
        property("sonar.organization", "antibytes")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.jacoco.reportPaths", )
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.sources")
        property("sonar.tests")
        property("sonar.sources", sources.joinToString(","))
        property("sonar.tests",  tests.joinToString(","))
        property("sonar.jacoco.reportPaths", jacocoReports.joinToString(","))
    }
}
