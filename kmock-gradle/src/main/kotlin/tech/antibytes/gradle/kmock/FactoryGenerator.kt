/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeVariableName
import org.gradle.api.tasks.StopExecutionException
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.COLLECTOR_NAME
import java.io.File

internal object FactoryGenerator : KMockPluginContract.FactoryGenerator {
    private fun guardPackageName(rootPackage: String) {
        if (rootPackage.isEmpty()) {
            throw StopExecutionException("Missing package definition!")
        }
    }

    private fun buildRelaxedParameter(): ParameterSpec {
        return ParameterSpec.builder("relaxed", TypeVariableName("Boolean"))
            .defaultValue("false")
            .build()
    }

    private fun buildVerifierParameter(): ParameterSpec {
        return ParameterSpec.builder("verifier", COLLECTOR_NAME)
            .defaultValue("Collector { _, _ -> Unit }")
            .build()
    }

    private fun buildMockFactory(): FunSpec {
        val factory = FunSpec.builder("kmock")
        val type = TypeVariableName("T")

        return factory.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addTypeVariable(type.copy(reified = true))
            .returns(type)
            .addParameter(buildVerifierParameter())
            .addParameter(buildRelaxedParameter())
            .addModifiers(KModifier.EXPECT)
            .build()
    }

    private fun buildSpyParameter(): ParameterSpec {
        return ParameterSpec.builder("spyOn", TypeVariableName("T"))
            .build()
    }

    private fun buildSpyFactory(): FunSpec {
        val factory = FunSpec.builder("kspy")
        val type = TypeVariableName("T")

        return factory.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addTypeVariable(type.copy(reified = true))
            .returns(type)
            .addParameter(buildVerifierParameter())
            .addParameter(buildSpyParameter())
            .addModifiers(KModifier.EXPECT)
            .build()
    }

    override fun generate(targetDir: File, rootPackage: String) {
        guardPackageName(rootPackage)

        val file = FileSpec.builder(
            rootPackage,
            "MockFactory"
        )

        file.addFunction(buildMockFactory())
        file.addFunction(buildSpyFactory())

        file.build().writeTo(targetDir)
    }
}
