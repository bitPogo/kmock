/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.kmock.dependency.MkDocs
import tech.antibytes.gradle.kmock.config.KMockPublishingConfiguration
import tech.antibytes.gradle.verisoning.Versioning

// see: https://github.com/bitfunk/gradle-plugins/blob/29c798a0e0fc572fa94f49da85407c3769dc11cc/docs/build.gradle.kts
plugins {
    id("ru.vyarus.mkdocs") version "2.3.0"
    id("com.palantir.git-version")
}

python {
    pip(
        MkDocs.includeMarkdown,
        MkDocs.kroki,
        MkDocs.extraData,
        MkDocs.material,
        MkDocs.minify,
        MkDocs.redirects,
        MkDocs.pygments,
        MkDocs.pymdown,
    )
}

val version = Versioning.getInstance(
    project = project,
    configuration = KMockPublishingConfiguration().versioning
).versionName()

mkdocs {
    sourcesDir = projectDir.absolutePath

    publish.docPath = version
    if (version.endsWith("SNAPSHOT")) {
        publish.rootRedirect = false
    } else {
        publish.rootRedirect = true
        publish.rootRedirectTo = "latest"
        publish.setVersionAliases("latest")
    }
    publish.generateVersionsFile = true

    strict = true

    extras = mapOf(
        "version" to version,
    )
}


tasks.register<Delete>("clean") {
    delete("build")
}
