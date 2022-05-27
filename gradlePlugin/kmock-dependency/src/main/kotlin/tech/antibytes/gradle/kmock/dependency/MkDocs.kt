/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.dependency

object MkDocs {
    const val includeMarkdown = "mkdocs-include-markdown-plugin:${Version.mkdocs.includeMarkdown}"
    const val kroki = "mkdocs-kroki-plugin:${Version.mkdocs.kroki}"
    const val extraData = "mkdocs-markdownextradata-plugin:${Version.mkdocs.extraData}"
    const val material = "mkdocs-material:${Version.mkdocs.material}"
    const val minify = "mkdocs-minify-plugin:${Version.mkdocs.minify}"
    const val redirects = "mkdocs-redirects:${Version.mkdocs.redirects}"
    const val pygments = "pygments:${Version.mkdocs.pygments}"
    const val pymdown = "pymdown-extensions:${Version.mkdocs.pymdown}"
}
