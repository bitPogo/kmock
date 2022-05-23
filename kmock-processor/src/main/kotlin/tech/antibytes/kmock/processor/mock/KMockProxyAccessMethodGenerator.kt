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
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNCHECKED
import tech.antibytes.kmock.processor.ProcessorContract.Companion.multibounded
import tech.antibytes.kmock.processor.ProcessorContract.Companion.unit
import tech.antibytes.kmock.processor.ProcessorContract.ProxyAccessMethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyAccessMethodGeneratorFactory
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

internal class KMockProxyAccessMethodGenerator private constructor(
    private val enabled: Boolean,
    private val nullableClassGenerics: Map<String, TypeName>,
) : ProxyAccessMethodGenerator {
    private sealed interface Member

    private interface Method : Member {
        val methodName: String
        val proxyName: String
        val sideEffect: TypeName
        val proxySignature: TypeName
    }

    private interface OverloadedMethod : Method {
        val typeParameter: List<TypeVariableName>
        val arguments: List<ParameterSpec>
        val returnType: TypeName
        val mappedParameterTypes: Map<String, TypeVariableName>
        val unifier: String
    }

    data class Property(
        val propertyName: String,
        val propertyType: TypeName,
        val proxyName: String,
    ) : Member

    data class SyncFunProxy(
        override val methodName: String,
        override val proxyName: String,
        override val proxySignature: TypeName,
        override val sideEffect: TypeName,
    ) : Method

    data class AsyncFunProxy(
        override val methodName: String,
        override val proxyName: String,
        override val proxySignature: TypeName,
        override val sideEffect: TypeName,
    ) : Method

    data class OverloadedSyncFunProxy(
        override val methodName: String,
        override val proxyName: String,
        override val proxySignature: TypeName,
        override val sideEffect: TypeName,
        override val typeParameter: List<TypeVariableName>,
        override val arguments: List<ParameterSpec>,
        override val returnType: TypeName,
        override val mappedParameterTypes: Map<String, TypeVariableName>,
        override val unifier: String,
    ) : OverloadedMethod

    data class OverloadedAsyncFunProxy(
        override val methodName: String,
        override val proxyName: String,
        override val proxySignature: TypeName,
        override val sideEffect: TypeName,
        override val typeParameter: List<TypeVariableName>,
        override val arguments: List<ParameterSpec>,
        override val returnType: TypeName,
        override val mappedParameterTypes: Map<String, TypeVariableName>,
        override val unifier: String,
    ) : OverloadedMethod

    private val properties: MutableList<Property> = mutableListOf()
    private val syncFun: MutableMap<String, MutableList<SyncFunProxy>> = mutableMapOf()
    private val overloadedSyncFun: MutableMap<String, MutableList<OverloadedSyncFunProxy>> = mutableMapOf()
    private val asyncFun: MutableMap<String, MutableList<AsyncFunProxy>> = mutableMapOf()
    private val overloadedAsyncFun: MutableMap<String, MutableList<OverloadedAsyncFunProxy>> = mutableMapOf()

    private fun List<TypeVariableName>.mapTypeParameter(): Map<String, TypeVariableName> {
        return this.associateBy { type -> type.name }
    }

    private fun ParameterSpec.determineArgument(): TypeName {
        return if (this.modifiers.contains(KModifier.VARARG)) {
            array.parameterizedBy(
                TypeVariableName("out $type")
            )
        } else {
            this.type
        }
    }

    private fun List<TypeName>.resolveGenericMark(): String {
        return if (this.isEmpty()) {
            "Any?"
        } else {
            this.joinToString(" & ")
        }
    }

    private fun TypeVariableName.resolveMarking(
        mapping: Map<String, TypeVariableName>
    ): String {
        var currentName = name

        while (currentName in mapping) {
            currentName = mapping[currentName]!!.bounds.resolveGenericMark()
        }

        return currentName
    }

    private fun List<TypeVariableName>.resolveMarking(
        mapping: Map<String, TypeVariableName>
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

    override fun collectProperty(
        propertyName: String,
        propertyType: TypeName,
        proxyName: String,
    ) = guardedCollect {
        properties.add(
            Property(
                propertyName = propertyName,
                propertyType = propertyType,
                proxyName = proxyName,
            )
        )
    }

    private fun collectSyncMethod(
        methodName: String,
        proxyName: String,
        proxySignature: TypeName,
        proxySideEffect: TypeVariableName,
    ) {
        val sideEffect = proxySideEffect.toString()
        val registry = syncFun.getOrElse(sideEffect) { mutableListOf() }
        registry.add(
            SyncFunProxy(
                methodName = methodName,
                proxyName = proxyName,
                proxySignature = proxySignature,
                sideEffect = proxySideEffect,
            )
        )

        syncFun[sideEffect] = registry
    }

    private fun collectOverloadedSyncMethod(
        methodName: String,
        typeParameter: List<TypeVariableName>,
        arguments: List<ParameterSpec>,
        returnType: TypeName,
        proxyName: String,
        proxySignature: TypeName,
        proxySideEffect: TypeVariableName,
    ) {
        val mappedParameterTypes = typeParameter.mapTypeParameter()
        val typeParameterMark = typeParameter.resolveMarking(mappedParameterTypes)
        val key = "$proxySideEffect|[$typeParameterMark]"
        val registry = overloadedSyncFun.getOrElse(key) { mutableListOf() }
        registry.add(
            OverloadedSyncFunProxy(
                methodName = methodName,
                proxyName = proxyName,
                proxySignature = proxySignature,
                sideEffect = proxySideEffect,
                typeParameter = typeParameter,
                arguments = arguments,
                returnType = returnType,
                mappedParameterTypes = mappedParameterTypes,
                unifier = key,
            )
        )

        overloadedSyncFun[key] = registry
    }

    private fun collectSyncMethod(
        methodName: String,
        typeParameter: List<TypeVariableName>,
        arguments: List<ParameterSpec>,
        returnType: TypeName,
        proxyName: String,
        proxySignature: TypeName,
        proxySideEffect: TypeVariableName,
    ) {
        if (methodName != proxyName.drop(1)) {
            collectOverloadedSyncMethod(
                methodName = methodName,
                typeParameter = typeParameter,
                arguments = arguments,
                returnType = returnType,
                proxyName = proxyName,
                proxySignature = proxySignature,
                proxySideEffect = proxySideEffect,
            )
        } else {
            collectSyncMethod(
                methodName = methodName,
                proxyName = proxyName,
                proxySignature = proxySignature,
                proxySideEffect = proxySideEffect,
            )
        }
    }

    private fun collectOverloadedAsyncMethod(
        methodName: String,
        typeParameter: List<TypeVariableName>,
        arguments: List<ParameterSpec>,
        returnType: TypeName,
        proxyName: String,
        proxySignature: TypeName,
        proxySideEffect: TypeVariableName,
    ) {
        val mappedParameterTypes = typeParameter.mapTypeParameter()
        val typeParameterMark = typeParameter.resolveMarking(mappedParameterTypes)
        val key = "$proxySideEffect|[$typeParameterMark]"
        val registry = overloadedAsyncFun.getOrElse(key) { mutableListOf() }
        registry.add(
            OverloadedAsyncFunProxy(
                methodName = methodName,
                proxyName = proxyName,
                proxySignature = proxySignature,
                sideEffect = proxySideEffect,
                typeParameter = typeParameter,
                arguments = arguments,
                returnType = returnType,
                mappedParameterTypes = mappedParameterTypes,
                unifier = key,
            )
        )

        overloadedAsyncFun[key] = registry
    }

    private fun collectAsyncMethod(
        methodName: String,
        proxyName: String,
        proxySignature: TypeName,
        proxySideEffect: TypeVariableName,
    ) {
        val sideEffect = proxySideEffect.toString()
        val registry = asyncFun.getOrElse(sideEffect) { mutableListOf() }
        registry.add(
            AsyncFunProxy(
                methodName = methodName,
                proxyName = proxyName,
                proxySignature = proxySignature,
                sideEffect = proxySideEffect,
            )
        )

        asyncFun[sideEffect] = registry
    }

    private fun collectAsyncMethod(
        methodName: String,
        typeParameter: List<TypeVariableName>,
        arguments: List<ParameterSpec>,
        returnType: TypeName,
        proxyName: String,
        proxySignature: TypeName,
        proxySideEffect: TypeVariableName,
    ) {
        if (methodName != proxyName.drop(1)) {
            collectOverloadedAsyncMethod(
                methodName = methodName,
                typeParameter = typeParameter,
                arguments = arguments,
                returnType = returnType,
                proxyName = proxyName,
                proxySignature = proxySignature,
                proxySideEffect = proxySideEffect,
            )
        } else {
            collectAsyncMethod(
                methodName = methodName,
                proxyName = proxyName,
                proxySignature = proxySignature,
                proxySideEffect = proxySideEffect,
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
        proxySignature: TypeName,
        proxySideEffect: TypeVariableName,
    ) = guardedCollect {
        if (isSuspending) {
            collectAsyncMethod(
                methodName = methodName,
                typeParameter = typeParameter,
                arguments = arguments,
                returnType = returnType ?: unit,
                proxyName = proxyName,
                proxySignature = proxySignature,
                proxySideEffect = proxySideEffect,
            )
        } else {
            collectSyncMethod(
                methodName = methodName,
                typeParameter = typeParameter,
                arguments = arguments,
                returnType = returnType ?: unit,
                proxyName = proxyName,
                proxySignature = proxySignature,
                proxySideEffect = proxySideEffect,
            )
        }
    }

    private fun determineEntry(member: Member): String {
        return when (member) {
            is Property -> {
                "\n\"${member.propertyName}|property\" to ${member.proxyName},"
            }
            is OverloadedMethod -> {
                val unifier = member.unifier.replace(" ", "·")
                "\n\"${member.methodName}|$unifier\" to ${member.proxyName},"
            }
            is Method -> {
                val sideEffect = member.sideEffect.toString().replace(" ", "·")
                "\n\"${member.methodName}|$sideEffect\" to ${member.proxyName},"
            }
        }
    }

    private fun extractReferenceStoreEntries(): String {
        val entries = StringBuilder()

        properties.forEach { property ->
            entries.append(determineEntry(property))
        }

        syncFun.values.forEach { sycFuns ->
            sycFuns.forEach { sycFun ->
                entries.append(determineEntry(sycFun))
            }
        }

        overloadedSyncFun.values.forEach { sycFuns ->
            sycFuns.forEach { sycFun ->
                entries.append(determineEntry(sycFun))
            }
        }

        asyncFun.values.forEach { asyncFuns ->
            asyncFuns.forEach { asyncFun ->
                entries.append(determineEntry(asyncFun))
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
            KModifier.PRIVATE
        )

        referenceStorage.initializer(
            "mapOf(%L\n)",
            extractReferenceStoreEntries()
        )

        return referenceStorage.build()
    }

    private fun createPropertyAccessMethod(): FunSpec {
        return FunSpec.builder("propertyProxyOf")
            .returns(propertyProxy)
            .addParameter("reference", kProperty)
            .addTypeVariable(propertyType)
            .addStatement(
                REFERENCE_STORE_ACCESS,
                "\${reference.name}|property",
                "Unknown property \${reference.name}!",
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

    private fun createNonOverloadedFunProxyAccess(
        proxyAccessMethod: String,
        method: Method,
        proxySignature: TypeName,
        id: Int
    ): FunSpec {
        return FunSpec.builder(proxyAccessMethod)
            .returns(proxySignature)
            .addParameter("reference", method.sideEffect)
            .addStatement(
                REFERENCE_STORE_ACCESS,
                "\${(reference as $kFunction).name}|${method.sideEffect}",
                "Unknown method \${reference.name} with signature ${method.sideEffect}!",
                proxySignature
            )
            .addAnnotation(UNCHECKED)
            .addAnnotation(experimental)
            .addAnnotation(createJvmName(proxyAccessMethod, id))
            .build()
    }

    private fun OverloadedMethod.createSideEffect(): TypeVariableName {
        val sideEffect = StringBuilder(6)
        sideEffect.append("(")

        arguments.forEach { parameter ->
            sideEffect.append(parameter.determineArgument())
            sideEffect.append(", ")
        }

        sideEffect.append(") -> ")
        sideEffect.append(returnType)

        return TypeVariableName(sideEffect.toString())
    }

    private fun List<TypeName>.resolveType(): Pair<TypeName, Boolean> {
        val isNullable: Boolean
        val type = when (this.size) {
            0 -> {
                isNullable = true
                nullableAny
            }
            1 -> {
                val type = this.first()
                isNullable = type.isNullable

                type
            }
            else -> {
                isNullable = this.all { type -> type.isNullable }
                multibounded
            }
        }

        return Pair(type, isNullable)
    }

    private fun TypeVariableName.resolveType(
        mapping: Map<String, TypeVariableName>
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
        nullableClassGenerics: Map<String, TypeName>
    ): TypeName {
        return when {
            multibounded == first && second -> any
            first.toString() in nullableClassGenerics -> nullableClassGenerics[first.toString()]!!
            second -> first
            else -> originalType
        }.copy(nullable = false)
    }

    private fun ParameterSpec.determineNonNullableArgument(
        nullableClassGenerics: Map<String, TypeName>,
        mapping: Map<String, TypeVariableName>
    ): TypeName {
        return when {
            this.modifiers.contains(KModifier.VARARG) -> {
                array.parameterizedBy(
                    TypeVariableName("out $type")
                )
            }
            type is TypeVariableName -> {
                (type as TypeVariableName)
                    .resolveType(mapping)
                    .ensureNonNullableTransitiveParameter(
                        type,
                        nullableClassGenerics
                    )
            }
            else -> type
        }.copy(nullable = false)
    }

    private fun OverloadedMethod.createIndicators(): List<ParameterSpec> {
        return arguments.mapIndexed { idx, parameter ->
            ParameterSpec.builder(
                "type$idx",
                kClass.parameterizedBy(
                    parameter.determineNonNullableArgument(nullableClassGenerics, mappedParameterTypes)
                )
            ).build()
        }
    }

    private fun createOverloadedFunProxyAccess(
        proxyAccessMethod: String,
        method: OverloadedMethod,
        proxySignature: TypeName,
        id: Int
    ): FunSpec {
        val sideEffect = method.createSideEffect()
        val indicators = method.createIndicators()

        return FunSpec.builder(proxyAccessMethod)
            .returns(proxySignature)
            .addTypeVariables(method.typeParameter)
            .addParameter("reference", sideEffect)
            .addParameters(indicators)
            .addStatement(
                REFERENCE_STORE_ACCESS,
                "\${(reference as $kFunction).name}|${method.unifier}",
                "Unknown method \${reference.name} with signature ${method.sideEffect}!",
                proxySignature
            )
            .addAnnotation(unusedAndUnchecked)
            .addAnnotation(experimental)
            .addAnnotation(createJvmName(proxyAccessMethod, id))
            .build()
    }

    private fun TypeName.syncFunProxyToFunProxy(): TypeName {
        val type = this.toString().replace("Sync", "")
        return TypeVariableName(
            name = type
        ).copy(nullable = false)
    }

    private fun createSyncAccessMethod(
        syncMethod: SyncFunProxy,
        id: Int,
    ): FunSpec {
        return createNonOverloadedFunProxyAccess(
            proxyAccessMethod = "syncFunProxyOf",
            method = syncMethod,
            proxySignature = syncMethod.proxySignature.syncFunProxyToFunProxy(),
            id = id,
        )
    }

    private fun createOverloadedSyncAccessMethod(
        syncMethod: OverloadedSyncFunProxy,
        id: Int,
    ): FunSpec {
        return createOverloadedFunProxyAccess(
            proxyAccessMethod = "syncFunProxyOf",
            method = syncMethod,
            proxySignature = syncMethod.proxySignature.syncFunProxyToFunProxy(),
            id = id,
        )
    }

    private fun TypeName.asyncFunProxyToFunProxy(): TypeName {
        val type = this.toString().replace("Async", "")
        return TypeVariableName(
            name = type
        ).copy(nullable = false)
    }

    private fun createAsyncAccessMethod(
        asyncMethod: AsyncFunProxy,
        id: Int,
    ): FunSpec {
        return createNonOverloadedFunProxyAccess(
            proxyAccessMethod = "asyncFunProxyOf",
            method = asyncMethod,
            proxySignature = asyncMethod.proxySignature.asyncFunProxyToFunProxy(),
            id = id,
        )
    }

    private fun createOverloadedAsyncAccessMethod(
        asyncMethod: OverloadedAsyncFunProxy,
        id: Int,
    ): FunSpec {
        return createOverloadedFunProxyAccess(
            proxyAccessMethod = "asyncFunProxyOf",
            method = asyncMethod,
            proxySignature = asyncMethod.proxySignature.asyncFunProxyToFunProxy(),
            id = id,
        )
    }

    override fun createAccessMethods(): List<FunSpec> {
        val accessMethods: MutableList<FunSpec> = mutableListOf()

        if (properties.isNotEmpty()) {
            accessMethods.add(createPropertyAccessMethod())
        }

        syncFun.values.forEachIndexed { idx, proxyGroup ->
            accessMethods.add(
                createSyncAccessMethod(proxyGroup.first(), idx)
            )
        }

        overloadedSyncFun.values.forEachIndexed { idx, proxyGroup ->
            accessMethods.add(
                createOverloadedSyncAccessMethod(proxyGroup.first(), idx)
            )
        }

        asyncFun.values.forEachIndexed { idx, proxyGroup ->
            accessMethods.add(
                createAsyncAccessMethod(proxyGroup.first(), idx)
            )
        }

        overloadedAsyncFun.values.forEachIndexed { idx, proxyGroup ->
            accessMethods.add(
                createOverloadedAsyncAccessMethod(proxyGroup.first(), idx)
            )
        }

        return accessMethods
    }

    companion object : ProxyAccessMethodGeneratorFactory {
        private const val REFERENCE_STORE = "referenceStore"
        private const val REFERENCE_STORE_ACCESS = "return ($REFERENCE_STORE[%P] ?: throw IllegalStateException(%P)) as %L"
        private val safeJvmName = ClassName(
            Mock::class.java.packageName,
            "SafeJvmName"
        )
        private val experimental = ClassName(
            Mock::class.java.packageName,
            "KMockExperimental"
        )
        private val unusedAndUnchecked = AnnotationSpec.builder(Suppress::class).addMember(
            "%S, %S, %S",
            "UNUSED_PARAMETER",
            "UNUSED_EXPRESSION",
            "UNCHECKED_CAST",
        ).build()
        private val starParameter = TypeVariableName("*")
        private val referenceStoreType = Map::class.asClassName().parameterizedBy(
            String::class.asTypeName(),
            Proxy::class.asTypeName().parameterizedBy(
                starParameter,
                starParameter
            ),
        )
        private val propertyType = TypeVariableName("Property")
        private val kProperty = KProperty::class.asClassName().parameterizedBy(propertyType)
        private val kFunction = KFunction::class.asClassName().parameterizedBy(starParameter)
        private val kClass = KClass::class.asClassName()
        private val array = Array::class.asClassName()
        private val any = Any::class.asClassName()
        private val nullableAny = any.copy(nullable = true)

        private val propertyProxy = PropertyProxy::class.asClassName().parameterizedBy(propertyType)

        override fun getInstance(
            enableGenerator: Boolean,
            nullableClassGenerics: Map<String, TypeName>,
        ): ProxyAccessMethodGenerator = KMockProxyAccessMethodGenerator(enableGenerator, nullableClassGenerics)
    }
}
