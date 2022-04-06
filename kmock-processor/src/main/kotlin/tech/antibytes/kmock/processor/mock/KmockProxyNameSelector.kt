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
import tech.antibytes.kmock.processor.titleCase
import java.util.SortedSet

internal class KmockProxyNameSelector(
    private val uselessPrefixes: Set<String>,
) : ProxyNameSelector, ProxyNameCollector {
    private var overloadedProxies: SortedSet<String> = sortedSetOf()
    private val buildIns = mapOf(
        "_toString" to "_toStringWithVoid",
        "_equals" to "_equalsWithAny",
        "_hashCode" to "_hashCodeWithVoid"
    )

    private fun collectPropertyNames(
        template: KSClassDeclaration,
        nameCollector: MutableList<String>
    ) {
        template.getAllProperties().forEach { ksProperty ->
            val name = ksProperty.simpleName.asString()
            nameCollector.add(name)
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
            nameCollector = nameCollector
        )
        collectMethodNames(
            template = template,
            nameCollector = nameCollector,
            overloadedMethods = overloadedMethods
        )

        overloadedProxies = overloadedMethods.toSortedSet()
    }

    override fun selectPropertyName(
        qualifier: String,
        propertyName: String
    ): ProxyInfo = ProxyInfo(
        templateName = propertyName,
        proxyId = "$qualifier#_$propertyName",
        proxyName = "_$propertyName"
    )

    private fun String.removePrefixes(prefixes: Iterable<String>): String {
        var cleaned = this

        prefixes.forEach { prefix ->
            cleaned = removePrefix(prefix)
        }

        return cleaned
    }

    private fun String.packageNameToVariableName(): String {
        val partialNames = split('.')

        return if (partialNames.size == 1) {
            this
        } else {
            partialNames.joinToString("") { partialName -> partialName.titleCase() }
        }
    }

    private fun String.trimTypeName(): String {
        return this
            .substringBefore('<') // Generics
            .removePrefixes(uselessPrefixes)
            .packageNameToVariableName()
    }

    private fun resolveGenericName(
        boundaries: List<KSTypeReference>?,
        typeResolver: TypeParameterResolver
    ): String? {
        return if (boundaries.isNullOrEmpty()) {
            null
        } else {
            boundaries.joinToString("") { typeName ->
                typeName
                    .toTypeName(typeResolver)
                    .toString()
                    .trimTypeName()
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

        return currentName
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
        qualifier: String,
        methodName: String,
        proxyName: String,
    ): ProxyInfo = ProxyInfo(
        templateName = methodName,
        proxyId = "$qualifier#$proxyName",
        proxyName = proxyName
    )

    override fun selectBuildInMethodName(
        qualifier: String,
        methodName: String
    ): ProxyInfo {
        val proxyName = selectBuildInMethodProxyName("_$methodName")

        return createMethodProxyInfo(
            qualifier = qualifier,
            proxyName = proxyName,
            methodName = methodName,
        )
    }

    override fun selectMethodName(
        qualifier: String,
        methodName: String,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver,
        arguments: Array<MethodTypeInfo>
    ): ProxyInfo {

        val proxyName = selectMethodProxyName(
            proxyMethodNameCandidate = "_$methodName",
            arguments = arguments,
            generics = generics,
            typeResolver = typeResolver,
        )

        return createMethodProxyInfo(
            qualifier = qualifier,
            proxyName = proxyName,
            methodName = methodName,
        )
    }
}