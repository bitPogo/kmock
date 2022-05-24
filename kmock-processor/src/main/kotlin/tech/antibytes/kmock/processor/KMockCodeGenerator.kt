/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

// based on: https://github.com/google/ksp/blob/main/compiler-plugin/src/main/kotlin/com/google/devtools/ksp/processing/impl/CodeGeneratorImpl.kt
internal class KMockCodeGenerator(
    private val kspDir: String,
    private val kspGenerator: CodeGenerator,
) : ProcessorContract.KmpCodeGenerator {
    private val files = mutableMapOf<String, File>()
    private val outputStreams = mutableMapOf<String, FileOutputStream>()

    private var oneTimeSourceSet: String? = null

    override fun setOneTimeSourceSet(sourceSet: String) {
        oneTimeSourceSet = sourceSet
    }

    override fun closeFiles() {
        outputStreams.values.forEach { stream -> stream.close() }
        outputStreams.clear()
    }

    override val generatedFile: Collection<File>
        get() = files.values

    private fun String.packageToPath(): String = this.replace('.', separator)

    private fun String.toCompliantKspPath(): String {
        val platform = this.substring(0, this.length - 4)

        return "$platform$separator$this${separator}kotlin"
    }

    private fun pathOf(
        sourceSet: String,
        packageName: String,
        fileName: String,
    ): String {
        val packageDirs = packageName.packageToPath()
        val sourceDirs = sourceSet.toCompliantKspPath()

        return "$kspDir$separator$sourceDirs$separator$packageDirs$separator$fileName$fileExtension"
    }

    private fun guardFilePath(
        file: File
    ) {
        val parent = file.parentFile

        if (!parent.exists() && !parent.mkdirs()) {
            throw IllegalStateException("Failed to make parent directories of ${file.nameWithoutExtension}.")
        }
    }

    private fun createSharedSourceFile(absoluteFilePath: String): OutputStream {
        val file = File(absoluteFilePath)

        guardFilePath(file)

        file.writeText("")

        files[absoluteFilePath] = file
        return file.outputStream().also { stream ->
            outputStreams[absoluteFilePath] = stream
        }
    }

    private fun guardAgainstRewriteFile(absolutePath: String) {
        if (absolutePath in files) {
            throw FileAlreadyExistsException(File(absolutePath))
        }
    }

    override fun createNewFile(
        dependencies: Dependencies,
        packageName: String,
        fileName: String,
        extensionName: String
    ): OutputStream {
        return if (oneTimeSourceSet != null) {
            val absoluteFilePath = pathOf(
                oneTimeSourceSet!!,
                packageName,
                fileName
            )
            oneTimeSourceSet = null

            guardAgainstRewriteFile(absoluteFilePath)
            createSharedSourceFile(absoluteFilePath)
        } else {
            kspGenerator.createNewFile(
                dependencies = dependencies,
                packageName = packageName,
                fileName = fileName,
                extensionName = extensionName
            )
        }
    }

    override fun associate(
        sources: List<KSFile>,
        packageName: String,
        fileName: String,
        extensionName: String
    ) = TODO("Not yet implemented")

    override fun associateWithClasses(
        classes: List<KSClassDeclaration>,
        packageName: String,
        fileName: String,
        extensionName: String
    ) = TODO("Not yet implemented")

    private companion object {
        private val separator = File.separatorChar
        private const val fileExtension = ".kt"
    }
}
