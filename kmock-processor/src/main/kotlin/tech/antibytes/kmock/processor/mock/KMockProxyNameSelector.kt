/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import java.util.SortedSet
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.MemberArgumentTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameCollector
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.kotlinpoet.rawType
import tech.antibytes.kmock.processor.utils.isReceiverMethod
import tech.antibytes.kmock.processor.utils.titleCase

internal class KMockProxyNameSelector(
    enableFineGrainedNames: Boolean,
    private val customMethodNames: Map<String, String>,
    private val useTypePrefixFor: Map<String, String>,
) : ProxyNameSelector, ProxyNameCollector {
    private var overloadedProxies: SortedSet<String> = sortedSetOf()
    private val buildIns = mapOf(
        "_toString" to "_toStringWithVoid",
        "_equals" to "_equalsWithAny",
        "_hashCode" to "_hashCodeWithVoid",
    )

    private val suffixResolver: Function2<Array<MemberArgumentTypeInfo>, Map<String, GenericDeclaration>, String> =
        if (enableFineGrainedNames) {
            { arguments, generics ->
                resolveLongProxySuffixFromArguments(
                    arguments = arguments,
                    generics = generics,
                )
            }
        } else {
            { arguments, generics ->
                resolveProxySuffixFromArguments(
                    arguments = arguments,
                    generics = generics,
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
        propertyName: String,
    ): ProxyInfo {
        return ProxyInfo(
            templateName = propertyName,
            proxyId = "$qualifier#_$propertyName",
            proxyName = "_$propertyName",
        )
    }

    private fun GenericDeclaration.determineNullabilityPrefix(): String {
        return if (isNullable) {
            NULLABLE_INDICATOR
        } else {
            ""
        }
    }

    private fun String.trimTypeName(): String = substringAfterLast('.')

    private fun ClassName.resolveClassPrefix(): String = useTypePrefixFor[this.toString()]?.titleCase() ?: ""

    private fun TypeName.determineNullabilityPrefix(): String {
        return if (isNullable) {
            NULLABLE_INDICATOR
        } else {
            ""
        }
    }

    private fun StringBuilder.appendVarargIndicator(): StringBuilder {
        return append(ARRAY).append(NESTED_TYPE_SEPARATOR)
    }

    private fun ClassName.resolveExhaustiveName(): String {
        val zero = determineNullabilityPrefix()
        val prefix = resolveClassPrefix()
        val name = resolveRawClassFlatName()

        return "$prefix$zero$name"
    }

    private fun WildcardTypeName.resolveExhaustiveName(
        generics: Map<String, GenericDeclaration>,
    ): String {
        val name = StringBuilder(0)
        val types = inTypes.ifEmpty { outTypes }

        types.forEach { type -> name.resolveExhaustiveName(type, generics) }

        return name.toString()
    }

    private fun GenericDeclaration.mapExhaustiveBoundaries(
        typePrefix: String,
        generics: Map<String, GenericDeclaration>,
    ): String {
        val name = StringBuilder()

        types.forEach { type ->
            name.append(typePrefix)
                .resolveExhaustiveName(type, generics)
        }

        return name.toString()
    }

    private fun TypeVariableName.resolveExhaustiveName(
        generics: Map<String, GenericDeclaration>,
    ): String {
        val zero = determineNullabilityPrefix()
        val typePrefix = "$zero$name"

        return if (name in generics) {
            generics[name]!!.mapExhaustiveBoundaries(typePrefix, generics)
        } else {
            typePrefix
        }
    }

    private fun ParameterizedTypeName.resolveParameterNames(
        generics: Map<String, GenericDeclaration>,
    ): String {
        val parameterNames = StringBuilder()
        typeArguments.forEach { type ->
            parameterNames
                .append(NESTED_TYPE_SEPARATOR)
                .resolveExhaustiveName(type, generics)
        }

        return parameterNames.toString()
    }

    private fun ParameterizedTypeName.resolveExhaustiveName(
        generics: Map<String, GenericDeclaration>,
    ): String {
        val zero = determineNullabilityPrefix()
        val parameter = resolveParameterNames(generics)
        val name = rawType().resolveRawClassFlatName()

        return "$zero$name$parameter"
    }

    private fun StringBuilder.resolveExhaustiveName(
        type: TypeName,
        generics: Map<String, GenericDeclaration>,
    ): StringBuilder {
        val name = when (type) {
            is ParameterizedTypeName -> type.resolveExhaustiveName(generics)
            is ClassName -> type.resolveExhaustiveName()
            is TypeVariableName -> type.resolveExhaustiveName(generics)
            is WildcardTypeName -> type.resolveExhaustiveName(generics)
            else -> {
                throw IllegalStateException("Unexpected Type ${type::class.simpleName}!")
            }
        }

        return append(name)
    }

    private fun resolveLongProxySuffixFromArguments(
        arguments: Array<MemberArgumentTypeInfo>,
        generics: Map<String, GenericDeclaration>,
    ): String {
        val suffix = StringBuilder()

        arguments.forEach { (_, type, _, isVararg) ->
            if (isVararg) {
                suffix.appendVarargIndicator()
            }

            suffix.resolveExhaustiveName(type, generics)
        }

        return suffix.toString()
    }

    private fun StringBuilder.amendPlural(usePlural: Boolean): StringBuilder {
        return if (usePlural) {
            append('s')
        } else {
            this
        }
    }

    private fun ClassName.resolveRawClassFlatName(): String = simpleName.trimTypeName().titleCase()

    private fun TypeName.resolveFlatBoundary(): String {
        return if (this is TypeVariableName) {
            name
        } else {
            rawType().resolveRawClassFlatName()
        }
    }

    private fun GenericDeclaration.mapFlatBoundaries(
        typePrefix: String,
    ): String {
        val name = StringBuilder()

        types.forEach { type ->
            val boundaryName = type.resolveFlatBoundary()

            name.append(typePrefix)
            name.append(boundaryName)
        }

        return name.toString()
    }

    private fun TypeVariableName.resolveFlatName(
        generics: Map<String, GenericDeclaration>,
    ): String {
        return if (name in generics) {
            val genericDeclaration = generics[name]!!
            val zero = genericDeclaration.determineNullabilityPrefix()
            val typePrefix = "$zero$name"

            genericDeclaration.mapFlatBoundaries(typePrefix)
        } else {
            name
        }
    }

    private fun ClassName.resolveFlatName(): String {
        val prefix = resolveClassPrefix()
        val className = resolveRawClassFlatName()

        return "$prefix$className"
    }

    private fun ParameterizedTypeName.resolveFlatName(): String = rawType.resolveFlatName()

    private fun StringBuilder.resolveFlatName(
        type: TypeName,
        generics: Map<String, GenericDeclaration>,
    ): StringBuilder {
        val name = when (type) {
            is ParameterizedTypeName -> type.resolveFlatName()
            is ClassName -> type.resolveFlatName()
            is TypeVariableName -> type.resolveFlatName(generics)
            else -> {
                throw IllegalStateException("Unexpected Type ${type::class.simpleName}!")
            }
        }

        return append(name)
    }

    private fun resolveProxySuffixFromArguments(
        arguments: Array<MemberArgumentTypeInfo>,
        generics: Map<String, GenericDeclaration>,
    ): String {
        val suffix = StringBuilder()

        arguments.forEach { (_, type, _, usePlural) ->
            suffix.resolveFlatName(type, generics).amendPlural(usePlural)
        }

        return suffix.toString()
    }

    private fun determineSuffixedMethodProxyName(
        methodName: String,
        arguments: Array<MemberArgumentTypeInfo>,
        generics: Map<String, GenericDeclaration>,
    ): String {
        val titleCasedSuffixes = if (arguments.isNotEmpty()) {
            suffixResolver(arguments, generics)
        } else {
            VOID_SUFFIX
        }

        return "$methodName$METHOD_SUFFIX_SEPARATOR$titleCasedSuffixes"
    }

    private fun selectMethodProxyName(
        proxyMethodNameCandidate: String,
        generics: Map<String, GenericDeclaration>,
        arguments: Array<MemberArgumentTypeInfo>,
    ): String {
        return if (proxyMethodNameCandidate in overloadedProxies) {
            determineSuffixedMethodProxyName(
                methodName = proxyMethodNameCandidate,
                arguments = arguments,
                generics = generics,
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
        methodName: String,
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
        generics: Map<String, GenericDeclaration>,
        arguments: Array<MemberArgumentTypeInfo>,
    ): ProxyInfo {
        val proxyName = selectMethodProxyName(
            proxyMethodNameCandidate = "_$methodName$suffix",
            arguments = arguments,
            generics = generics,
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
        generics: Map<String, GenericDeclaration>,
        arguments: Array<MemberArgumentTypeInfo>,
    ): ProxyInfo = selectMethodName(
        suffix = "",
        qualifier = qualifier,
        methodName = methodName,
        generics = generics,
        arguments = arguments,
    )

    override fun selectReceiverGetterName(
        qualifier: String,
        propertyName: String,
        receiver: MemberArgumentTypeInfo,
        generics: Map<String, GenericDeclaration>,
    ): ProxyInfo = selectMethodName(
        suffix = RECEIVER_GETTER,
        qualifier = qualifier,
        methodName = propertyName,
        generics = generics,
        arguments = arrayOf(receiver),
    )

    override fun selectReceiverSetterName(
        qualifier: String,
        propertyName: String,
        receiver: MemberArgumentTypeInfo,
        generics: Map<String, GenericDeclaration>,
    ): ProxyInfo = selectMethodName(
        suffix = RECEIVER_SETTER,
        qualifier = qualifier,
        methodName = propertyName,
        generics = generics,
        arguments = arrayOf(receiver),
    )

    override fun selectReceiverMethodName(
        qualifier: String,
        methodName: String,
        generics: Map<String, GenericDeclaration>,
        arguments: Array<MemberArgumentTypeInfo>,
    ): ProxyInfo = selectMethodName(
        suffix = RECEIVER_METHOD,
        qualifier = qualifier,
        methodName = methodName,
        generics = generics,
        arguments = arguments,
    )

    private companion object {
        const val RECEIVER_GETTER = "Getter"
        const val RECEIVER_SETTER = "Setter"
        const val RECEIVER_METHOD = "Receiver"
        const val NULLABLE_INDICATOR = "Z"
        const val VOID_SUFFIX = "Void"
        const val METHOD_SUFFIX_SEPARATOR = "With"
        const val NESTED_TYPE_SEPARATOR = "_"
        const val ARRAY = "Array"
    }
}
