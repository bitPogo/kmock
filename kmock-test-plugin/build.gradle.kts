/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */
import tech.antibytes.gradle.dependency.helper.addCustomRepositories
import tech.antibytes.gradle.dependency.helper.ensureKotlinVersion
import tech.antibytes.gradle.kmock.config.repositories.Repositories.kmockRepositories

plugins {
    id("tech.antibytes.gradle.setup")

    alias(antibytesCatalog.plugins.gradle.antibytes.dependencyHelper)
    alias(antibytesCatalog.plugins.gradle.antibytes.quality)
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

evaluationDependsOnChildren()

tasks.named<Wrapper>("wrapper") {
    gradleVersion = antibytesCatalog.versions.gradle.gradle.get()
    distributionType = Wrapper.DistributionType.ALL
}
