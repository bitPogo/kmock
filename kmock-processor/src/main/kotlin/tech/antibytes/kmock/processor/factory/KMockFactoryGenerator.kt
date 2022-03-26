/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_CONTRACT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal class KMockFactoryGenerator(
    private val logger: KSPLogger,
    private val spyOn: Set<String>,
    private val allowInterfacesOnKmock: Boolean,
    private val allowInterfacesOnKspy: Boolean,
    private val utils: ProcessorContract.MockFactoryGeneratorUtil,
    private val genericResolver: ProcessorContract.GenericResolver,
    private val codeGenerator: CodeGenerator,
) : ProcessorContract.MockFactoryGenerator {
    private val unused = AnnotationSpec.builder(Suppress::class).addMember(
        "%S, %S",
        "UNUSED_PARAMETER",
        "UNUSED_EXPRESSION"
    ).build()

    private fun buildGenericsInfo(
        generics: List<TypeName>
    ): String {
        return if (generics.isEmpty()) {
            ""
        } else {
            "<${generics.joinToString(", ")}>"
        }
    }

    private fun createAliasName(
        alias: String?,
        packageName: String
    ): String? {
        return if (alias != null) {
            "$packageName.$alias"
        } else {
            null
        }
    }

    private fun determineMockTemplates(
        relaxer: Relaxer?
    ): Pair<String, String> {
        return if (relaxer == null) {
            Pair(
                "%L::class -> %LMock%L(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as $KMOCK_FACTORY_TYPE_NAME",
                "%LMock::class -> %LMock%L(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as $KMOCK_FACTORY_TYPE_NAME",
            )
        } else {
            Pair(
                "%L::class -> %LMock%L(verifier = verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze) as $KMOCK_FACTORY_TYPE_NAME",
                "%LMock::class -> %LMock%L(verifier = verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze) as $KMOCK_FACTORY_TYPE_NAME",
            )
        }
    }

    private fun addMock(
        kmock: FunSpec.Builder,
        qualifiedName: String,
        interfaceName: String,
        aliasInterfaceName: String?,
        typeInfo: String,
        relaxer: Relaxer?
    ) {
        val (interfaceInvocationTemplate, mockInvocationTemplate) = determineMockTemplates(relaxer)
        if (allowInterfacesOnKmock) {
            kmock.addStatement(
                interfaceInvocationTemplate,
                qualifiedName,
                aliasInterfaceName ?: interfaceName,
                typeInfo,
            )
        }

        kmock.addStatement(
            mockInvocationTemplate,
            aliasInterfaceName ?: interfaceName,
            aliasInterfaceName ?: interfaceName,
            typeInfo,
        )
    }

    private fun buildMockSelector(
        kmock: FunSpec.Builder,
        templateSources: List<TemplateSource>,
        generics: List<TypeName>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        val typeInfo = buildGenericsInfo(generics)

        kmock.beginControlFlow("return when ($KMOCK_FACTORY_TYPE_NAME::class)")
        templateSources.forEach { source ->
            val packageName = source.template.packageName.asString()
            val qualifiedName = source.template.qualifiedName!!.asString()
            val aliasInterfaceName = createAliasName(source.alias, packageName)
            val interfaceName = "$packageName.${source.template.simpleName.asString()}"

            addMock(
                kmock = kmock,
                qualifiedName = qualifiedName,
                aliasInterfaceName = aliasInterfaceName,
                interfaceName = interfaceName,
                typeInfo = typeInfo,
                relaxer = relaxer,
            )
        }
        kmock.addStatement("else -> throw RuntimeException(\"Unknown Interface \${$KMOCK_FACTORY_TYPE_NAME::class.simpleName}.\")")
        kmock.endControlFlow()

        return kmock
    }

    private fun resolveModifier(isKmp: Boolean): KModifier? {
        return if (isKmp) {
            KModifier.ACTUAL
        } else {
            null
        }
    }

    private fun fillMockFactory(
        type: TypeVariableName,
        generics: List<TypeVariableName>,
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        val modifier = resolveModifier(isKmp)
        val kmock = utils.generateKmockSignature(
            type = type,
            generics = generics,
            hasDefault = !isKmp,
            modifier = modifier
        )

        return buildMockSelector(
            kmock = kmock,
            templateSources = templateSources,
            generics = generics,
            relaxer = relaxer
        )
    }

    private fun buildMockFactory(
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): FunSpec {
        val type = TypeVariableName(KMOCK_FACTORY_TYPE_NAME)

        return fillMockFactory(
            type = type.copy(reified = true),
            generics = emptyList(),
            isKmp = isKmp,
            templateSources = templateSources,
            relaxer = relaxer
        ).build()
    }

    private fun addSpy(
        kspy: FunSpec.Builder,
        qualifiedName: String,
        source: TemplateSource,
        typeInfo: String,
    ) {
        if (qualifiedName in spyOn) {
            val packageName = source.template.packageName.asString()
            val aliasInterfaceName = createAliasName(source.alias, packageName)
            val interfaceName = "$packageName.${source.template.simpleName.asString()}"

            if (allowInterfacesOnKspy) {
                kspy.addStatement(
                    "%L::class -> %LMock(verifier = verifier, freeze = freeze, spyOn = spyOn as %L%L) as $KMOCK_FACTORY_TYPE_NAME",
                    qualifiedName,
                    aliasInterfaceName ?: interfaceName,
                    qualifiedName,
                    typeInfo,
                )
            }

            kspy.addStatement(
                "%LMock::class -> %LMock(verifier = verifier, freeze = freeze, spyOn = spyOn as %L%L) as $KMOCK_FACTORY_TYPE_NAME",
                aliasInterfaceName ?: interfaceName,
                aliasInterfaceName ?: interfaceName,
                qualifiedName,
                typeInfo,
            )
        }
    }

    private fun buildSpySelector(
        kspy: FunSpec.Builder,
        generics: List<TypeName>,
        templateSources: List<TemplateSource>,
    ): FunSpec.Builder {
        val typeInfo = buildGenericsInfo(generics)

        kspy.beginControlFlow("return when ($KMOCK_FACTORY_TYPE_NAME::class)")
        templateSources.forEach { source ->
            val qualifiedName = source.template.qualifiedName!!.asString()
            addSpy(
                kspy = kspy,
                qualifiedName = qualifiedName,
                source = source,
                typeInfo = typeInfo
            )
        }
        kspy.addStatement("else -> throw RuntimeException(\"Unknown Interface \${$KMOCK_FACTORY_TYPE_NAME::class.simpleName}.\")")
        kspy.endControlFlow()

        return kspy
    }

    private fun fillSpyFactory(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        generics: List<TypeVariableName>,
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
    ): FunSpec.Builder {
        val modifier = resolveModifier(isKmp)
        val kspy = utils.generateKspySignature(
            mockType = mockType,
            spyType = spyType,
            generics = generics,
            hasDefault = !isKmp,
            modifier = modifier
        )

        return buildSpySelector(kspy, generics, templateSources)
    }

    private fun buildSpyFactory(
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
    ): FunSpec {
        val spyType = TypeVariableName(KSPY_FACTORY_TYPE_NAME)
        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(spyType))

        return fillSpyFactory(
            mockType = mockType,
            spyType = spyType,
            generics = emptyList(),
            templateSources = templateSources,
            isKmp = isKmp,
        ).build()
    }

    private fun buildGenericMockFactory(
        isKmp: Boolean,
        templateSource: TemplateSource,
        relaxer: Relaxer?
    ): FunSpec {
        val generics = utils.resolveGenerics(templateSource)

        val type = genericResolver.resolveKMockFactoryType(
            KMOCK_FACTORY_TYPE_NAME,
            templateSource
        )

        return fillMockFactory(
            templateSources = listOf(templateSource),
            generics = generics,
            type = type,
            isKmp = isKmp,
            relaxer = relaxer
        ).build()
    }

    private fun buildGenericSpyFactory(
        isKmp: Boolean,
        templateSource: TemplateSource,
    ): FunSpec {
        val generics = utils.resolveGenerics(templateSource)

        val spyType = genericResolver.resolveKMockFactoryType(
            KSPY_FACTORY_TYPE_NAME,
            templateSource
        )

        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(spyType))

        return fillSpyFactory(
            mockType = mockType,
            spyType = spyType,
            templateSources = listOf(templateSource),
            generics = generics,
            isKmp = isKmp,
        ).build()
    }

    private fun buildGenericFactories(
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): List<Pair<FunSpec, FunSpec>> {
        return templateSources.map { template ->
            Pair(
                buildGenericMockFactory(isKmp, template, relaxer),
                buildGenericSpyFactory(isKmp, template)
            )
        }
    }

    private fun writeFactoryImplementation(
        options: ProcessorContract.Options,
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        val file = FileSpec.builder(
            options.rootPackage,
            "MockFactory"
        )
        file.addAnnotation(unused)
        file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)

        val (regular, generics) = utils.splitInterfacesIntoRegularAndGenerics(templateSources)

        val genericFactories = buildGenericFactories(
            templateSources = generics,
            relaxer = relaxer,
            isKmp = options.isKmp,
        )

        file.addFunction(
            buildMockFactory(
                templateSources = regular,
                relaxer = relaxer,
                isKmp = options.isKmp,
            )
        )

        file.addFunction(
            buildSpyFactory(
                templateSources = regular,
                isKmp = options.isKmp,
            )
        )

        genericFactories.forEach { factories ->
            val (kmock, kspy) = factories

            file.addFunction(kmock)
            file.addFunction(kspy)
        }

        file.build().writeTo(
            codeGenerator = codeGenerator,
            aggregating = false,
            originatingKSFiles = dependencies
        )
    }

    override fun writeFactories(
        options: ProcessorContract.Options,
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        if (templateSources.isNotEmpty()) { // TODO: Solve multi Rounds in a better way
            writeFactoryImplementation(
                options = options,
                templateSources = templateSources,
                dependencies = dependencies,
                relaxer = relaxer
            )
        }
    }
}
