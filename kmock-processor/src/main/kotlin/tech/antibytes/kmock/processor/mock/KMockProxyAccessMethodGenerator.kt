/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.tags.TypeAliasTag
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import tech.antibytes.kmock.Hint0
import tech.antibytes.kmock.Hint1
import tech.antibytes.kmock.Hint10
import tech.antibytes.kmock.Hint11
import tech.antibytes.kmock.Hint12
import tech.antibytes.kmock.Hint2
import tech.antibytes.kmock.Hint3
import tech.antibytes.kmock.Hint4
import tech.antibytes.kmock.Hint5
import tech.antibytes.kmock.Hint6
import tech.antibytes.kmock.Hint7
import tech.antibytes.kmock.Hint8
import tech.antibytes.kmock.Hint9
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ARRAY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.MULTI_BOUNDED
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NULLABLE_ANY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNCHECKED
import tech.antibytes.kmock.processor.ProcessorContract.ProxyAccessMethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyAccessMethodGeneratorFactory
import tech.antibytes.kmock.processor.kotlinpoet.rawType

internal class KMockProxyAccessMethodGenerator private constructor(
    private val enabled: Boolean,
    private val preventResolvingOfAliases: Set<String>,
    private val nullableClassGenerics: Map<String, TypeName>,
) : ProxyAccessMethodGenerator {
    private sealed interface Member {
        val memberName: String
        val proxyName: String
        val indicator: String
    }

    private interface Method : Member {
        val proxySignature: ParameterizedTypeName
        val sideEffect: LambdaTypeName
        val typeParameter: List<TypeVariableName>
        val mappedParameterTypes: Map<String, TypeVariableName>
    }

    private interface OverloadedMethod : Method

    data class Property(
        override val memberName: String,
        val propertyType: TypeName,
        override val proxyName: String,
        override val indicator: String,
    ) : Member

    data class SyncFunProxy(
        override val memberName: String,
        override val proxyName: String,
        override val indicator: String,
        override val proxySignature: ParameterizedTypeName,
        override val sideEffect: LambdaTypeName,
        override val typeParameter: List<TypeVariableName>,
        override val mappedParameterTypes: Map<String, TypeVariableName>,
    ) : Method

    data class AsyncFunProxy(
        override val memberName: String,
        override val proxyName: String,
        override val indicator: String,
        override val proxySignature: ParameterizedTypeName,
        override val sideEffect: LambdaTypeName,
        override val typeParameter: List<TypeVariableName>,
        override val mappedParameterTypes: Map<String, TypeVariableName>,
    ) : Method

    data class OverloadedSyncFunProxy(
        override val memberName: String,
        override val proxyName: String,
        override val indicator: String,
        override val proxySignature: ParameterizedTypeName,
        override val sideEffect: LambdaTypeName,
        override val typeParameter: List<TypeVariableName>,
        override val mappedParameterTypes: Map<String, TypeVariableName>,
    ) : OverloadedMethod

    data class OverloadedAsyncFunProxy(
        override val memberName: String,
        override val proxyName: String,
        override val indicator: String,
        override val proxySignature: ParameterizedTypeName,
        override val sideEffect: LambdaTypeName,
        override val typeParameter: List<TypeVariableName>,
        override val mappedParameterTypes: Map<String, TypeVariableName>,
    ) : OverloadedMethod

    private val properties: MutableList<Property> = mutableListOf()
    private val syncFun: MutableMap<String, MutableList<SyncFunProxy>> = mutableMapOf()
    private val overloadedSyncFun: MutableMap<String, MutableList<OverloadedSyncFunProxy>> = mutableMapOf()
    private val asyncFun: MutableMap<String, MutableList<AsyncFunProxy>> = mutableMapOf()
    private val overloadedAsyncFun: MutableMap<String, MutableList<OverloadedAsyncFunProxy>> = mutableMapOf()

    private fun String.toIndicator(): String {
        return this.replace("\n", "")
    }

    private fun LambdaTypeName.toIndicator(): String = this.toString().toIndicator()

    private fun List<TypeVariableName>.mapTypeParameter(): Map<String, TypeVariableName> {
        return this.associateBy { type -> type.name }
    }

    private fun TypeName.resolveTypeName(): TypeName {
        val alias = tag(TypeAliasTag::class)

        return if (alias == null || this.rawType().toString().trimEnd('?') in preventResolvingOfAliases) {
            this
        } else {
            alias.abbreviatedType
        }
    }

    private fun TypeName.resolveTypeNames(): TypeName {
        return when (val type = resolveTypeName()) {
            is ParameterizedTypeName -> {
                type.rawType().parameterizedBy(
                    type.typeArguments.resolveTypeNames(),
                ).copy(nullable = type.isNullable)
            }
            else -> type
        }
    }

    private fun WildcardTypeName.resolveTypeName(): TypeName {
        return if (outTypes.isNotEmpty()) {
            WildcardTypeName
                .producerOf(
                    outTypes.first().resolveTypeNames(),
                )
        } else {
            WildcardTypeName
                .consumerOf(
                    inTypes.first().resolveTypeNames(),
                )
        }
    }

    @JvmName("resolveTypeNamesTypeName")
    private fun List<TypeName>.resolveTypeNames(): List<TypeName> {
        return map { type ->
            if (type is WildcardTypeName) {
                type.resolveTypeName()
            } else {
                type.resolveTypeNames()
            }
        }
    }

    @JvmName("resolveTypeNamesTypeVariableName")
    private fun List<TypeVariableName>.resolveTypeNames(): List<TypeVariableName> {
        return map { type ->
            val boundaries = type.bounds.resolveTypeNames()

            (type.resolveTypeNames() as TypeVariableName).copy(bounds = boundaries, nullable = type.isNullable)
        }
    }

    private fun ParameterSpec.determineArgument(): TypeName {
        return if (this.modifiers.contains(KModifier.VARARG)) {
            ARRAY.parameterizedBy(
                WildcardTypeName.producerOf(type.resolveTypeNames()),
            )
        } else {
            this.type.resolveTypeNames()
        }
    }

    private fun List<ParameterSpec>.determineArguments(): Array<TypeName> {
        return map { parameter ->
            parameter.determineArgument()
        }.toTypedArray()
    }

    private fun createSideEffect(
        arguments: List<ParameterSpec>,
        returnType: TypeName,
        isSuspending: Boolean,
    ): LambdaTypeName {
        val sideEffect = LambdaTypeName.get(
            parameters = arguments.determineArguments(),
            returnType = returnType.resolveTypeNames(),
        )

        return sideEffect.copy(suspending = isSuspending)
    }

    private fun LambdaTypeName.toFunProxySignature(): ParameterizedTypeName {
        return funProxy.parameterizedBy(
            returnType,
            this,
        )
    }

    private fun List<TypeName>.resolveGenericMark(): String {
        return if (this.isEmpty()) {
            "Any?"
        } else {
            this.joinToString(" & ") { typeName ->
                typeName.resolveTypeNames().toString()
            }
        }
    }

    private fun TypeVariableName.resolveMarking(
        mapping: Map<String, TypeVariableName>,
    ): String {
        var currentName = name

        while (currentName in mapping) {
            currentName = mapping[currentName]!!.bounds.resolveGenericMark()
        }

        return currentName
    }

    private fun List<TypeVariableName>.resolveMarking(
        mapping: Map<String, TypeVariableName>,
    ): String {
        return this.joinToString { type ->
            "[${type.resolveMarking(mapping)}]"
        }
    }

    private fun guardedCollect(action: () -> Unit) {
        if (enabled) {
            action()
        }
    }

    private fun String.toProxyKey(): String {
        return replace(KEY_SPACE_PATTERN, SPACE_REPLACEMENT)
    }

    override fun collectProperty(
        propertyName: String,
        propertyType: TypeName,
        proxyName: String,
    ) = guardedCollect {
        properties.add(
            Property(
                memberName = propertyName,
                propertyType = propertyType,
                proxyName = proxyName,
                indicator = PROPERTY_INDICATOR,
            ),
        )
    }

    private fun addMethod(
        methodName: String,
        typeParameter: List<TypeVariableName>,
        arguments: List<ParameterSpec>,
        returnType: TypeName,
        proxySideEffect: LambdaTypeName,
        proxyName: String,
        suspending: Boolean,
        addToRegistry: (
            methodName: String,
            proxyName: String,
            proxySignature: ParameterizedTypeName,
            sideEffect: LambdaTypeName,
            typeParameter: List<TypeVariableName>,
            mappedParameterTypes: Map<String, TypeVariableName>,
            indicator: String,
        ) -> Unit,
    ) {
        val mappedParameterTypes = typeParameter.mapTypeParameter()
        val typeParameterMark = typeParameter.resolveMarking(mappedParameterTypes)
        val sideEffect = createSideEffect(
            arguments = proxySideEffect.parameters,
            returnType = proxySideEffect.returnType,
            isSuspending = suspending,
        )
        val referenceSideEffect = createSideEffect(
            arguments = arguments,
            returnType = returnType,
            isSuspending = suspending,
        )
        val proxySignature = sideEffect.toFunProxySignature()
        val key = "${sideEffect.toIndicator()}|[${typeParameterMark.toIndicator()}]"
        addToRegistry(
            methodName, // methodName
            proxyName, // proxyName
            proxySignature, // proxySignature
            referenceSideEffect, // sideEffect
            typeParameter, // typeParameter
            mappedParameterTypes, // mappedParameterTypes
            key, // indicator
        )
    }

    private fun isOverloaded(
        methodName: String,
        proxyName: String,
        typeParameter: List<TypeVariableName>,
    ): Boolean = methodName != proxyName.drop(1) || typeParameter.isNotEmpty()

    private fun collectSyncMethod(
        methodName: String,
        proxyName: String,
        proxySignature: ParameterizedTypeName,
        sideEffect: LambdaTypeName,
        typeParameter: List<TypeVariableName>,
        mappedParameterTypes: Map<String, TypeVariableName>,
        indicator: String,
    ) {
        val registry = syncFun.getOrElse(indicator) { mutableListOf() }

        registry.add(
            SyncFunProxy(
                memberName = methodName,
                proxyName = proxyName,
                proxySignature = proxySignature,
                sideEffect = sideEffect,
                typeParameter = typeParameter,
                mappedParameterTypes = mappedParameterTypes,
                indicator = indicator,
            ),
        )

        syncFun[indicator] = registry
    }

    private fun collectOverloadedSyncMethod(
        methodName: String,
        proxyName: String,
        proxySignature: ParameterizedTypeName,
        sideEffect: LambdaTypeName,
        typeParameter: List<TypeVariableName>,
        mappedParameterTypes: Map<String, TypeVariableName>,
        indicator: String,
    ) {
        val registry = overloadedSyncFun.getOrElse(indicator) { mutableListOf() }

        registry.add(
            OverloadedSyncFunProxy(
                memberName = methodName,
                proxyName = proxyName,
                proxySignature = proxySignature,
                sideEffect = sideEffect,
                typeParameter = typeParameter,
                mappedParameterTypes = mappedParameterTypes,
                indicator = indicator,
            ),
        )

        overloadedSyncFun[indicator] = registry
    }

    private fun collectSyncMethod(
        methodName: String,
        typeParameter: List<TypeVariableName>,
        arguments: List<ParameterSpec>,
        returnType: TypeName,
        proxySideEffect: LambdaTypeName,
        proxyName: String,
    ) {
        if (isOverloaded(methodName, proxyName, typeParameter)) {
            addMethod(
                methodName = methodName,
                typeParameter = typeParameter,
                arguments = arguments,
                returnType = returnType,
                proxySideEffect = proxySideEffect,
                proxyName = proxyName,
                suspending = false,
                addToRegistry = ::collectOverloadedSyncMethod,
            )
        } else {
            addMethod(
                methodName = methodName,
                typeParameter = typeParameter,
                arguments = arguments,
                returnType = returnType,
                proxySideEffect = proxySideEffect,
                proxyName = proxyName,
                suspending = false,
                addToRegistry = ::collectSyncMethod,
            )
        }
    }

    private fun collectAsyncMethod(
        methodName: String,
        proxyName: String,
        proxySignature: ParameterizedTypeName,
        sideEffect: LambdaTypeName,
        typeParameter: List<TypeVariableName>,
        mappedParameterTypes: Map<String, TypeVariableName>,
        indicator: String,
    ) {
        val registry = asyncFun.getOrElse(indicator) { mutableListOf() }

        registry.add(
            AsyncFunProxy(
                memberName = methodName,
                proxyName = proxyName,
                proxySignature = proxySignature,
                sideEffect = sideEffect,
                typeParameter = typeParameter,
                mappedParameterTypes = mappedParameterTypes,
                indicator = indicator,
            ),
        )

        asyncFun[indicator] = registry
    }

    private fun collectOverloadedAsyncMethod(
        methodName: String,
        proxyName: String,
        proxySignature: ParameterizedTypeName,
        sideEffect: LambdaTypeName,
        typeParameter: List<TypeVariableName>,
        mappedParameterTypes: Map<String, TypeVariableName>,
        indicator: String,
    ) {
        val registry = overloadedAsyncFun.getOrElse(indicator) { mutableListOf() }

        registry.add(
            OverloadedAsyncFunProxy(
                memberName = methodName,
                proxyName = proxyName,
                proxySignature = proxySignature,
                sideEffect = sideEffect,
                typeParameter = typeParameter,
                mappedParameterTypes = mappedParameterTypes,
                indicator = indicator,
            ),
        )

        overloadedAsyncFun[indicator] = registry
    }

    private fun collectAsyncMethod(
        methodName: String,
        typeParameter: List<TypeVariableName>,
        arguments: List<ParameterSpec>,
        returnType: TypeName,
        proxySideEffect: LambdaTypeName,
        proxyName: String,
    ) {
        if (isOverloaded(methodName, proxyName, typeParameter)) {
            addMethod(
                methodName = methodName,
                typeParameter = typeParameter,
                arguments = arguments,
                returnType = returnType,
                proxySideEffect = proxySideEffect,
                proxyName = proxyName,
                suspending = true,
                addToRegistry = ::collectOverloadedAsyncMethod,
            )
        } else {
            addMethod(
                methodName = methodName,
                typeParameter = typeParameter,
                arguments = arguments,
                returnType = returnType,
                proxySideEffect = proxySideEffect,
                proxyName = proxyName,
                suspending = true,
                addToRegistry = ::collectAsyncMethod,
            )
        }
    }

    override fun collectMethod(
        methodName: String,
        isSuspending: Boolean,
        typeParameter: List<TypeVariableName>,
        arguments: List<ParameterSpec>,
        returnType: TypeName?,
        proxyName: String,
        proxySignature: ParameterizedTypeName,
        proxySideEffect: LambdaTypeName,
    ) = guardedCollect {
        val returns = returnType ?: UNIT

        if (isSuspending) {
            collectAsyncMethod(
                methodName = methodName,
                typeParameter = typeParameter,
                arguments = arguments,
                returnType = returns,
                proxySideEffect = proxySideEffect,
                proxyName = proxyName,
            )
        } else {
            collectSyncMethod(
                methodName = methodName,
                typeParameter = typeParameter,
                proxySideEffect = proxySideEffect,
                arguments = arguments,
                returnType = returns,
                proxyName = proxyName,
            )
        }
    }

    private fun determineEntry(
        member: Member,
    ): String = "\n\"${member.memberName}|${member.indicator.toProxyKey()}\" to ${member.proxyName},"

    private fun extractReferenceStoreEntries(): String {
        val entries = StringBuilder()

        properties.forEach { property ->
            entries.append(determineEntry(property))
        }

        syncFun.forEach { (indicator, sycFuns) ->
            if (indicator !in overloadedSyncFun) {
                sycFuns.forEach { sycFun ->
                    entries.append(determineEntry(sycFun))
                }
            }
        }

        overloadedSyncFun.values.forEach { sycFuns ->
            sycFuns.forEach { sycFun ->
                entries.append(determineEntry(sycFun))
            }
        }

        asyncFun.forEach { (indicator, asyncFuns) ->
            if (indicator !in overloadedAsyncFun) {
                asyncFuns.forEach { asyncFun ->
                    entries.append(determineEntry(asyncFun))
                }
            }
        }

        overloadedAsyncFun.values.forEach { asyncFuns ->
            asyncFuns.forEach { asyncFun ->
                entries.append(determineEntry(asyncFun))
            }
        }

        return entries.toString()
    }

    override fun createReferenceStorage(): PropertySpec {
        val referenceStorage = PropertySpec.builder(
            REFERENCE_STORE,
            referenceStoreType,
            KModifier.PRIVATE,
        )

        referenceStorage.initializer(
            "mapOf(%L\n)",
            extractReferenceStoreEntries(),
        )

        return referenceStorage.build()
    }

    private fun createPropertyAccessMethod(): FunSpec {
        return FunSpec.builder("propertyProxyOf")
            .returns(propertyProxy)
            .addParameter(REFERENCE, kProperty)
            .addTypeVariable(propertyType)
            .addStatement(
                REFERENCE_STORE_ACCESS,
                "\${$REFERENCE.name}|$PROPERTY_INDICATOR",
                "Unknown property \${$REFERENCE.name}!",
                propertyProxy,
            )
            .addAnnotation(UNCHECKED)
            .addAnnotation(experimental)
            .build()
    }

    private fun createJvmName(accessType: String, id: Int): AnnotationSpec {
        return AnnotationSpec
            .builder(safeJvmName)
            .addMember("%S", "$accessType$id")
            .build()
    }

    private fun List<TypeName>.resolveType(): Pair<TypeName, Boolean> {
        val isNullable: Boolean
        val type = when (this.size) {
            0 -> {
                isNullable = true
                NULLABLE_ANY
            }
            1 -> {
                val type = this.first()
                isNullable = type.isNullable

                type
            }
            else -> {
                isNullable = this.all { type -> type.isNullable }
                MULTI_BOUNDED
            }
        }

        return Pair(type, isNullable)
    }

    private fun TypeVariableName.resolveType(
        mapping: Map<String, TypeVariableName>,
    ): Pair<TypeName, Boolean> {
        var currentName = name
        var currentType: TypeName = this
        var isNullable = false

        while (currentName in mapping) {
            val (type, nullability) = mapping[currentName]!!.bounds.resolveType()

            isNullable = nullability
            currentType = type
            currentName = type.toString()
        }

        return Pair(currentType, isNullable)
    }

    private fun Pair<TypeName, Boolean>.ensureNonNullableTransitiveParameter(
        originalType: TypeName,
        nullableClassGenerics: Map<String, TypeName>,
    ): TypeName {
        return when {
            MULTI_BOUNDED == first && second -> ANY
            first.toString() in nullableClassGenerics -> nullableClassGenerics[first.toString()]!!
            second -> first
            else -> originalType
        }.copy(nullable = false, tags = originalType.tags)
    }

    private fun ParameterSpec.determineNonNullableArgument(
        nullableClassGenerics: Map<String, TypeName>,
        mapping: Map<String, TypeVariableName>,
    ): TypeName {
        return when {
            this.modifiers.contains(KModifier.VARARG) -> {
                ARRAY.parameterizedBy(
                    WildcardTypeName.producerOf(type),
                )
            }
            type is TypeVariableName -> {
                (type as TypeVariableName)
                    .resolveType(mapping)
                    .ensureNonNullableTransitiveParameter(
                        type,
                        nullableClassGenerics,
                    )
            }
            else -> type
        }.copy(nullable = false, tags = type.tags)
    }

    private fun ClassName.hintWith(types: List<TypeName>): TypeName {
        return if (types.isEmpty()) {
            this
        } else {
            this.parameterizedBy(types)
        }
    }

    private fun List<TypeName>.toHint(): TypeName = hints["Hint${this.size}"]!!.hintWith(this)

    private fun OverloadedMethod.createIndicators(): ParameterSpec {
        val hints = sideEffect.parameters.map { parameter ->
            parameter
                .determineNonNullableArgument(nullableClassGenerics, mappedParameterTypes)
                .resolveTypeNames()
        }

        return ParameterSpec.builder(
            "hint",
            hints.toHint(),
        ).build()
    }

    private fun FunSpec.Builder.addIndicators(
        method: Method,
    ): FunSpec.Builder {
        return if (method is OverloadedMethod) {
            val indicators = method.createIndicators()

            addParameter(indicators)
        } else {
            this
        }
    }

    private fun createFunProxyAccess(
        proxyAccessMethod: String,
        method: Method,
        id: Int,
    ): FunSpec {
        return FunSpec.builder(proxyAccessMethod)
            .returns(method.proxySignature)
            .addTypeVariables(method.typeParameter.resolveTypeNames())
            .addParameter(REFERENCE, method.sideEffect)
            .addIndicators(method)
            .addStatement(
                REFERENCE_STORE_ACCESS,
                "\${($REFERENCE as $kFunction).name}|${method.indicator}",
                "Unknown method \${$REFERENCE.name} with signature ${method.sideEffect.toIndicator()}!",
                method.proxySignature,
            )
            .addAnnotation(unusedAndUnchecked)
            .addAnnotation(experimental)
            .addAnnotation(createJvmName(proxyAccessMethod, id))
            .build()
    }

    private fun createSyncAccessMethod(
        syncMethod: Method,
        id: Int,
    ): FunSpec {
        return createFunProxyAccess(
            proxyAccessMethod = SYNC_PROXY_OF,
            method = syncMethod,
            id = id,
        )
    }

    private fun createAsyncAccessMethod(
        asyncMethod: Method,
        id: Int,
    ): FunSpec {
        return createFunProxyAccess(
            proxyAccessMethod = ASYNC_PROXY_OF,
            method = asyncMethod,
            id = id,
        )
    }

    override fun createAccessMethods(): List<FunSpec> {
        val accessMethods: MutableList<FunSpec> = mutableListOf()
        var idx = 0

        if (properties.isNotEmpty()) {
            accessMethods.add(createPropertyAccessMethod())
        }

        syncFun.values.forEach { proxyGroup ->
            accessMethods.add(
                createSyncAccessMethod(proxyGroup.first(), idx),
            )
            idx++
        }

        overloadedSyncFun.values.forEach { proxyGroup ->
            accessMethods.add(
                createSyncAccessMethod(proxyGroup.first(), idx),
            )
            idx++
        }

        asyncFun.values.forEach { proxyGroup ->
            accessMethods.add(
                createAsyncAccessMethod(proxyGroup.first(), idx),
            )
            idx++
        }

        overloadedAsyncFun.values.forEach { proxyGroup ->
            accessMethods.add(
                createAsyncAccessMethod(proxyGroup.first(), idx),
            )
            idx++
        }

        return accessMethods
    }

    companion object : ProxyAccessMethodGeneratorFactory {
        private const val PROPERTY_INDICATOR = "property"
        private const val KEY_SPACE_PATTERN = " "
        private const val SPACE_REPLACEMENT = "·"
        private const val REFERENCE_STORE = "referenceStore"
        private const val REFERENCE_STORE_ACCESS = "return ($REFERENCE_STORE[%P] ?: throw IllegalStateException(%P)) as %L"
        private val safeJvmName = ClassName(
            Mock::class.java.packageName,
            "SafeJvmName",
        )
        private val experimental = ClassName(
            Mock::class.java.packageName,
            "KMockExperimental",
        )
        private val unusedAndUnchecked = AnnotationSpec.builder(Suppress::class).addMember(
            "%S, %S, %S",
            "UNUSED_PARAMETER",
            "UNUSED_EXPRESSION",
            "UNCHECKED_CAST",
        ).build()
        private val referenceStoreType = Map::class.asClassName().parameterizedBy(
            String::class.asTypeName(),
            Proxy::class.asTypeName().parameterizedBy(
                STAR,
                STAR,
            ),
        )
        private const val REFERENCE = "reference"
        private const val SYNC_PROXY_OF = "syncFunProxyOf"
        private const val ASYNC_PROXY_OF = "asyncFunProxyOf"
        private val propertyType = TypeVariableName("Property")
        private val propertyProxy = PropertyProxy::class.asClassName().parameterizedBy(propertyType)
        private val funProxy = KMockContract.FunProxy::class.asClassName()
        private val kProperty = KProperty::class.asClassName().parameterizedBy(propertyType)
        private val kFunction = KFunction::class.asClassName().parameterizedBy(STAR)

        private val hints = mapOf(
            "Hint0" to Hint0::class.asClassName(),
            "Hint1" to Hint1::class.asClassName(),
            "Hint2" to Hint2::class.asClassName(),
            "Hint3" to Hint3::class.asClassName(),
            "Hint4" to Hint4::class.asClassName(),
            "Hint5" to Hint5::class.asClassName(),
            "Hint6" to Hint6::class.asClassName(),
            "Hint7" to Hint7::class.asClassName(),
            "Hint8" to Hint8::class.asClassName(),
            "Hint9" to Hint9::class.asClassName(),
            "Hint10" to Hint10::class.asClassName(),
            "Hint11" to Hint11::class.asClassName(),
            "Hint12" to Hint12::class.asClassName(),
        )

        override fun getInstance(
            enableGenerator: Boolean,
            preventResolvingOfAliases: Set<String>,
            nullableClassGenerics: Map<String, TypeName>,
        ): ProxyAccessMethodGenerator = KMockProxyAccessMethodGenerator(
            enabled = enableGenerator,
            preventResolvingOfAliases = preventResolvingOfAliases,
            nullableClassGenerics = nullableClassGenerics,
        )
    }
}
