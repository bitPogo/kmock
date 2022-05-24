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
import tech.antibytes.kmock.processor.ProcessorContract.MemberArgumentTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameCollector
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.utils.isReceiverMethod
import tech.antibytes.kmock.processor.utils.titleCase
import java.util.SortedSet

internal class KMockProxyNameSelector(
    enableFineGrainedNames: Boolean,
    private val customMethodNames: Map<String, String>,
    private val useTypePrefixFor: Map<String, String>,
) : ProxyNameSelector, ProxyNameCollector {
    private var overloadedProxies: SortedSet<String> = sortedSetOf()
    private val buildIns = mapOf(
        "_toString" to "_toStringWithVoid",
        "_equals" to "_equalsWithAny",
        "_hashCode" to "_hashCodeWithVoid"
    )

    private val suffixResolver: Function3<Array<MemberArgumentTypeInfo>, Map<String, List<KSTypeReference>>, TypeParameterResolver, List<String>> =
        if (enableFineGrainedNames) {
            { arguments, generics, typeResolver: TypeParameterResolver ->
                resolveLongProxySuffixFromArguments(
                    arguments = arguments,
                    generics = generics,
                    typeResolver = typeResolver,
                )
            }
        } else {
            { arguments, generics, typeResolver: TypeParameterResolver ->
                resolveProxySuffixFromArguments(
                    arguments = arguments,
                    generics = generics,
                    typeResolver = typeResolver,
                )
            }
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

            when {
                ksFunction.isReceiverMethod() && "_$name$RECEIVER_METHOD" !in nameCollector -> {
                    nameCollector.add("_$name$RECEIVER_METHOD")
                }
                ksFunction.isReceiverMethod() && "_$name$RECEIVER_METHOD" in nameCollector -> {
                    overloadedMethods.add("_$name$RECEIVER_METHOD")
                }
                (name in nameCollector || "_$name" in nameCollector) -> overloadedMethods.add("_$name")
                else -> nameCollector.add(name)
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

    private fun String.trimNullable(): String = this.trimEnd('?')

    private fun String.prefixNullable(): String {
        return when {
            this == "?" -> this
            this.endsWith('?') -> "$NULLABLE_INDICATOR$this".dropLast(1)
            else -> this
        }
    }

    private fun String.resolvePrefixedTypeName(): String {
        val className = this.substringAfterLast('.').titleCase()
        val prefix = useTypePrefixFor.getOrDefault(this, "").titleCase()

        return "$prefix$className"
    }

    private fun String.trimTypeName(): String {
        return this.substringBefore('<') // Generics
            .resolvePrefixedTypeName()
    }

    private fun determineNullablePrefix(isNullable: Boolean): String {
        return if (isNullable) {
            NULLABLE_INDICATOR
        } else {
            ""
        }
    }

    private fun resolveGenericName(
        boundaries: List<KSTypeReference>?,
        typeResolver: TypeParameterResolver
    ): Pair<String, Boolean>? {
        var isNullable = true
        return if (boundaries.isNullOrEmpty()) {
            null
        } else {
            val boundaryNames = boundaries.joinToString("") { typeName ->
                val type = typeName.toTypeName(typeResolver)
                isNullable = isNullable && type.isNullable

                type.toString().trimTypeName().trimNullable()
            }

            Pair(boundaryNames, isNullable)
        }
    }

    private fun determineGenericName(
        name: String,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): String {
        var currentName = name
        var isNullable = true

        do {
            val (boundaryName, nullable) = resolveGenericName(generics[currentName], typeResolver)
                ?: nullableAnyGeneric

            isNullable = isNullable && nullable
            currentName = boundaryName
        } while (currentName in generics)

        val zero = determineNullablePrefix(isNullable)

        return "$zero$name${currentName.trimNullable()}"
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
        arguments: Array<MemberArgumentTypeInfo>,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): List<String> {
        return arguments.map { (_, suffix, _, usePlural) ->
            suffix
                .toString()
                .trimNullable()
                .resolveActualName(generics, typeResolver)
                .trimTypeName()
                .amendPlural(usePlural)
        }
    }

    private fun String.transformGenerics(
        generics: Map<String, String>
    ): String {
        return this.split('<', '>', ',')
            .filterNot { part -> part.isBlank() }
            .joinToString("_") { part ->
                val resolved = generics.getOrElse(part.trim().trimNullable()) { part }

                resolved.resolvePrefixedTypeName().prefixNullable()
            }
            .prefixNullable()
            .trimEnd('_')
    }

    private fun List<KSTypeReference>?.resolveBoundaries(
        typeResolver: TypeParameterResolver
    ): String {
        return if (this.isNullOrEmpty()) {
            NULLABLE_ANY_LONG_NAME
        } else {
            this.joinToString("") { typeName ->
                val type = typeName.toTypeName(typeResolver)
                type.toString().transformGenerics(emptyMap()).trimNullable()
            }
        }
    }

    private fun Map<String, List<KSTypeReference>>.determineGenericName(
        name: String,
        typeResolver: TypeParameterResolver
    ): String {
        val resolved = this[name].resolveBoundaries(typeResolver)

        return "$name${resolved.prefixNullable()}"
    }

    private fun Map<String, List<KSTypeReference>>.resolve(typeResolver: TypeParameterResolver): Map<String, String> {
        return this.keys.associateWith { key ->
            this.determineGenericName(key, typeResolver)
        }
    }

    private fun String.addArrayPrefix(addPrefix: Boolean): String {
        return if (addPrefix) {
            "Array_$this"
        } else {
            this
        }
    }

    private fun resolveLongProxySuffixFromArguments(
        arguments: Array<MemberArgumentTypeInfo>,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): List<String> {
        val resolved = generics.resolve(typeResolver)
        return arguments.map { (_, suffix, _, addPrefix) ->
            suffix
                .toString()
                .transformGenerics(resolved)
                .prefixNullable()
                .addArrayPrefix(addPrefix)
        }
    }

    private fun determineSuffixedMethodProxyName(
        methodName: String,
        arguments: Array<MemberArgumentTypeInfo>,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): String {
        val titleCasedSuffixes = if (arguments.isNotEmpty()) {
            this.suffixResolver(arguments, generics, typeResolver)
        } else {
            listOf("Void")
        }

        return "${methodName}With${titleCasedSuffixes.joinToString("")}"
    }

    private fun selectMethodProxyName(
        proxyMethodNameCandidate: String,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver,
        arguments: Array<MemberArgumentTypeInfo>
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
        arguments: Array<MemberArgumentTypeInfo>
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
        arguments: Array<MemberArgumentTypeInfo>,
        typeResolver: TypeParameterResolver,
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
        receiver: MemberArgumentTypeInfo,
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
        receiver: MemberArgumentTypeInfo,
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
        arguments: Array<MemberArgumentTypeInfo>,
        typeResolver: TypeParameterResolver,
    ): ProxyInfo = selectMethodName(
        suffix = RECEIVER_METHOD,
        qualifier = qualifier,
        methodName = methodName,
        generics = generics,
        typeResolver = typeResolver,
        arguments = arguments
    )

    private companion object {
        const val RECEIVER_GETTER = "Getter"
        const val RECEIVER_SETTER = "Setter"
        const val RECEIVER_METHOD = "Receiver"
        const val NULLABLE_INDICATOR = "Z"
        const val NULLABLE_ANY_LONG_NAME = "ZAny"
        private val nullableAnyGeneric = Pair("Any", true)
    }
}
