/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameCollector
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.utils.titleCase
import java.util.SortedSet

internal class KMockProxyNameSelector(
    enableNewOverloadingNames: Boolean,
    private val customMethodNames: Map<String, String>,
    private val useTypePrefixFor: Map<String, String>,
    private val uselessPrefixes: Set<String>,
) : ProxyNameSelector, ProxyNameCollector {
    private var overloadedProxies: SortedSet<String> = sortedSetOf()
    private val buildIns = mapOf(
        "_toString" to "_toStringWithVoid",
        "_equals" to "_equalsWithAny",
        "_hashCode" to "_hashCodeWithVoid"
    )

    private val prefixResolver: Function1<String, String> = if (enableNewOverloadingNames) {
        { typeName -> typeName.resolvePrefixedTypeName(useTypePrefixFor) }
    } else {
        { typeName -> typeName.removePrefixes(uselessPrefixes).packageNameToVariableName() }
    }

    private fun collectPropertyNames(
        template: KSClassDeclaration,
        nameCollector: MutableList<String>,
        overloadedMethods: MutableSet<String>,
    ) {
        template.getAllProperties().forEach { ksProperty ->
            val name = ksProperty.simpleName.asString()
            when {
                ksProperty.isReceiverMethod() && "_$name$RECEIVER_GETTER" !in nameCollector -> {
                    nameCollector.add("_$name$RECEIVER_GETTER")
                    nameCollector.add("_$name$RECEIVER_SETTER")
                }
                ksProperty.isReceiverMethod() && "_$name$RECEIVER_GETTER" in nameCollector -> {
                    overloadedMethods.add("_$name$RECEIVER_GETTER")
                    overloadedMethods.add("_$name$RECEIVER_SETTER")
                }
                else -> nameCollector.add(name)
            }
        }
    }

    private fun collectMethodNames(
        template: KSClassDeclaration,
        nameCollector: MutableList<String>,
        overloadedMethods: MutableSet<String>,
    ) {
        template.getAllFunctions().forEach { ksFunction ->
            val name = ksFunction.simpleName.asString()

            if (name in nameCollector || "_$name" in nameCollector) {
                overloadedMethods.add("_$name")
            } else {
                nameCollector.add(name)
            }
        }
    }

    override fun collect(template: KSClassDeclaration) {
        val nameCollector: MutableList<String> = mutableListOf()
        val overloadedMethods: MutableSet<String> = mutableSetOf()

        collectPropertyNames(
            template = template,
            nameCollector = nameCollector,
            overloadedMethods = overloadedMethods,
        )
        collectMethodNames(
            template = template,
            nameCollector = nameCollector,
            overloadedMethods = overloadedMethods,
        )

        overloadedProxies = overloadedMethods.toSortedSet()
    }

    override fun selectPropertyName(
        qualifier: String,
        propertyName: String
    ): ProxyInfo {
        return ProxyInfo(
            templateName = propertyName,
            proxyId = "$qualifier#_$propertyName",
            proxyName = "_$propertyName"
        )
    }

    private fun String.resolvePrefixedTypeName(prefixMapping: Map<String, String>): String {
        val className = this.substringAfterLast('.').titleCase()
        val prefix = prefixMapping.getOrDefault(this, "").titleCase()

        return "$prefix$className"
    }

    // Deprecated
    private fun String.removePrefixes(prefixes: Iterable<String>): String {
        var cleaned = this

        prefixes.forEach { prefix ->
            cleaned = removePrefix(prefix)
        }

        return cleaned
    }

    // Deprecated
    private fun String.packageNameToVariableName(): String {
        val partialNames = split('.')

        return if (partialNames.size == 1) {
            this
        } else {
            partialNames.joinToString("") { partialName -> partialName.titleCase() }
        }
    }

    private fun String.trimTypeName(): String {
        return prefixResolver(
            this.substringBefore('<') // Generics
        )
    }

    private fun resolveGenericName(
        boundaries: List<KSTypeReference>?,
        typeResolver: TypeParameterResolver
    ): String? {
        return if (boundaries.isNullOrEmpty()) {
            null
        } else {
            boundaries.joinToString("") { typeName ->
                typeName.toTypeName(typeResolver).toString().trimTypeName()
            }
        }
    }

    private fun determineGenericName(
        name: String,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): String {
        var currentName = name

        do {
            currentName = resolveGenericName(generics[currentName], typeResolver) ?: "Any"
        } while (currentName in generics)

        return "$name${currentName.trimEnd('?')}"
    }

    private fun String.resolveActualName(
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): String {
        return if (this in generics) {
            determineGenericName(this, generics, typeResolver)
        } else {
            this
        }
    }

    private fun String.amendPlural(usePlural: Boolean): String {
        return if (usePlural) {
            "${this}s"
        } else {
            this
        }
    }

    private fun resolveProxySuffixFromArguments(
        arguments: Array<MethodTypeInfo>,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): List<String> {
        return arguments.map { (_, suffix, usePlural) ->
            suffix
                .toString()
                .trimEnd('?')
                .resolveActualName(generics, typeResolver)
                .trimTypeName()
                .amendPlural(usePlural)
        }
    }

    private fun determineSuffixedMethodProxyName(
        methodName: String,
        arguments: Array<MethodTypeInfo>,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): String {
        val titleCasedSuffixes = if (arguments.isNotEmpty()) {
            this.resolveProxySuffixFromArguments(
                arguments = arguments,
                generics = generics,
                typeResolver = typeResolver
            )
        } else {
            listOf("Void")
        }

        return "${methodName}With${titleCasedSuffixes.joinToString("")}"
    }

    private fun selectMethodProxyName(
        proxyMethodNameCandidate: String,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver,
        arguments: Array<MethodTypeInfo>
    ): String {
        return if (proxyMethodNameCandidate in overloadedProxies) {
            determineSuffixedMethodProxyName(
                methodName = proxyMethodNameCandidate,
                arguments = arguments,
                generics = generics,
                typeResolver = typeResolver
            )
        } else {
            proxyMethodNameCandidate
        }
    }

    private fun selectBuildInMethodProxyName(
        proxyMethodNameCandidate: String,
    ): String {
        return if (proxyMethodNameCandidate in overloadedProxies) {
            buildIns[proxyMethodNameCandidate]!!
        } else {
            proxyMethodNameCandidate
        }
    }

    private fun createMethodProxyInfo(
        proxyId: String,
        methodName: String,
        proxyName: String,
    ): ProxyInfo = ProxyInfo(
        templateName = methodName,
        proxyId = proxyId,
        proxyName = proxyName,
    )

    private fun resolveMethodProxyId(
        proxyIdCandidate: String,
        qualifier: String,
        customMethodName: String?,
    ): String {
        return if (customMethodName != null) {
            "$qualifier#$customMethodName"
        } else {
            proxyIdCandidate
        }
    }

    override fun selectBuildInMethodName(
        qualifier: String,
        methodName: String
    ): ProxyInfo {
        val proxyName = selectBuildInMethodProxyName("_$methodName")
        val proxyIdCandidate = "$qualifier#$proxyName"
        val customName = customMethodNames[proxyIdCandidate]

        return createMethodProxyInfo(
            proxyId = resolveMethodProxyId(
                proxyIdCandidate = proxyIdCandidate,
                qualifier = qualifier,
                customMethodName = customName,
            ),
            proxyName = customName ?: proxyName,
            methodName = methodName,
        )
    }

    private fun selectMethodName(
        suffix: String,
        qualifier: String,
        methodName: String,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver,
        arguments: Array<MethodTypeInfo>
    ): ProxyInfo {
        val proxyName = selectMethodProxyName(
            proxyMethodNameCandidate = "_$methodName$suffix",
            arguments = arguments,
            generics = generics,
            typeResolver = typeResolver,
        )

        val proxyIdCandidate = "$qualifier#$proxyName"
        val customName = customMethodNames[proxyIdCandidate]

        return ProxyInfo(
            proxyId = resolveMethodProxyId(
                proxyIdCandidate = proxyIdCandidate,
                qualifier = qualifier,
                customMethodName = customName,
            ),
            proxyName = customName ?: proxyName,
            templateName = methodName,
        )
    }

    override fun selectMethodName(
        qualifier: String,
        methodName: String,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver,
        arguments: Array<MethodTypeInfo>
    ): ProxyInfo = selectMethodName(
        suffix = "",
        qualifier = qualifier,
        methodName = methodName,
        generics = generics,
        typeResolver = typeResolver,
        arguments = arguments
    )

    override fun selectReceiverGetterName(
        qualifier: String,
        propertyName: String,
        receiver: MethodTypeInfo,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): ProxyInfo = selectMethodName(
        suffix = RECEIVER_GETTER,
        qualifier = qualifier,
        methodName = propertyName,
        generics = generics,
        typeResolver = typeResolver,
        arguments = arrayOf(receiver)
    )

    override fun selectReceiverSetterName(
        qualifier: String,
        propertyName: String,
        receiver: MethodTypeInfo,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): ProxyInfo = selectMethodName(
        suffix = RECEIVER_SETTER,
        qualifier = qualifier,
        methodName = propertyName,
        generics = generics,
        typeResolver = typeResolver,
        arguments = arrayOf(receiver)
    )

    override fun selectReceiverMethodName(
        qualifier: String,
        methodName: String,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver,
        arguments: Array<MethodTypeInfo>
    ): ProxyInfo {
        TODO("Not yet implemented")
    }

    private companion object {
        const val RECEIVER_GETTER = "Getter"
        const val RECEIVER_SETTER = "Setter"
    }
}