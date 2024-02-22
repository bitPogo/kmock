/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import tech.antibytes.gradle.dependency.helper.GradleCompositeBuilds
import tech.antibytes.gradle.dependency.helper.addCustomRepositories
import tech.antibytes.gradle.dependency.helper.ensureKotlinVersion
import tech.antibytes.gradle.kmock.config.publishing.KMockPublishingConfiguration
import tech.antibytes.gradle.kmock.config.quality.Linter
import tech.antibytes.gradle.kmock.config.quality.SonarConfiguration
import tech.antibytes.gradle.kmock.config.quality.StableApi
import tech.antibytes.gradle.kmock.config.repositories.Repositories.kmockRepositories
import tech.antibytes.gradle.quality.api.CodeAnalysisConfiguration

plugins {
    id("tech.antibytes.gradle.setup")

    alias(antibytesCatalog.plugins.gradle.antibytes.dependencyHelper)
    alias(antibytesCatalog.plugins.gradle.antibytes.publishing)
    alias(antibytesCatalog.plugins.gradle.antibytes.quality)
}

antibytesQuality {
    linter.set(Linter.spotless)
    codeAnalysis.set(CodeAnalysisConfiguration(project = project))
    stableApi.set(StableApi.api)
    qualityGate.set(SonarConfiguration(project).configuration)
}

val publishing = KMockPublishingConfiguration(project)

antibytesPublishing {
    versioning.set(publishing.versioning)
    repositories.set(publishing.repositories)
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "7.5.1"
    distributionType = Wrapper.DistributionType.ALL
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
        addCustomRepositories(kmockRepositories)
    }

    ensureKotlinVersion()
}

GradleCompositeBuilds.configure(project)
evaluationDependsOnChildren()

tasks.withType<Detekt>().configureEach {
    setExcludes(
        listOf("**/kmock-test-plugin/**"),
    )
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    setExcludes(
        listOf("**/kmock-test-plugin/**"),
    )
}
