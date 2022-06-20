/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.dependency

object Version {

    val gradle = Gradle
    const val kotlin = "1.6.21"

    object Gradle {
        /**
         * [Kotlin](https://github.com/JetBrains/kotlin)
         */
        const val kotlin = Version.kotlin

        /**
         * [AnitBytes GradlePlugins](https://github.com/bitPogo/gradle-plugins)
         */
        const val antibytes = "a5c6f90"

        /**
         * [Spotless](https://plugins.gradle.org/plugin/com.diffplug.gradle.spotless)
         */
        const val spotless = "6.4.2"
    }

    val antibytes = Antibytes

    object Antibytes {
        const val test = "f25b461"
        const val kfixture = "0.2.0"
    }

    val google = Google

    object Google {
        /**
         * [KSP](https://github.com/google/ksp)
         */
        /**
         * [KSP DevTools on MavenCentral](https://mvnrepository.com/artifact/com.google.devtools.ksp/com.google.devtools.ksp.gradle.plugin)
         */
        const val ksp = "1.6.21-1.0.6"
    }

    val square = Square

    object Square {
        /**
         * [Kotlin Poet](https://square.github.io/kotlinpoet/)
         */
        const val kotlinPoet = "1.11.0"
    }

    /**
     * [Compiler Test](https://github.com/tschuchortdev/kotlin-compile-testing)
     */
    const val compilerTest = "1.4.8"

    val mkdocs = MkDoccs

    object MkDoccs {
        /**
         * [MkDocs Include Plugin](https://github.com/mondeja/mkdocs-include-markdown-plugin/releases)
         */
        const val includeMarkdown = "3.5.1"

        /**
         * [MkDocs Kroki](https://pypi.org/project/mkdocs-kroki-plugin/)
         */
        const val kroki = "0.3.0"

        /**
         * [MkDocs Extra Injected Data](https://github.com/rosscdh/mkdocs-markdownextradata-plugin/releases)
         */
        const val extraData = "0.2.5"

        /**
         * [MkDocs Material](https://github.com/squidfunk/mkdocs-material/releases)
         */
        const val material = "8.2.15"

        /**
         * [MkDocs Minfier](https://pypi.org/project/mkdocs-minify-plugin/)
         */
        const val minify = "0.5.0"

        /**
         * [MkDocs Redirects](https://github.com/mkdocs/mkdocs-redirects/releases)
         */
        const val redirects = "1.0.4"

        /**
         * [Pygments](https://pypi.org/project/Pygments/)
         */
        const val pygments = "2.12.0"

        /**
         * (PyMdown)(https://github.com/facelessuser/pymdown-extensions/releases)
         */
        const val pymdown = "9.4"
    }
}
