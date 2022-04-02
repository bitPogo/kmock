/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.NonIntrusiveFunConfiguration
import tech.antibytes.kmock.KMockContract.ParameterizedRelaxer
import tech.antibytes.kmock.KMockContract.Relaxer
import kotlin.reflect.KClass

internal class NonIntrusiveFunConfigurator<ReturnValue, SideEffect : Function<ReturnValue>> :
    KMockContract.NonIntrusiveFunConfigurator<ReturnValue, SideEffect>,
    KMockContract.NonIntrusiveConfigurationExtractor<NonIntrusiveFunConfiguration<ReturnValue, SideEffect>> {

    private var unitFunRelaxer: Relaxer<ReturnValue?>? = null
    private var buildInRelaxer: ParameterizedRelaxer<Any?, ReturnValue>? = null
    private var relaxer: Relaxer<ReturnValue>? = null
    private var spyOn: SideEffect? = null

    private fun finalizeRelaxers() {
        if (unitFunRelaxer != null || buildInRelaxer != null) {
            relaxer = null
        }

        if (spyOn != null) {
            relaxer = null
            unitFunRelaxer = null
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun useUnitFunRelaxerIf(condition: Boolean) {
        unitFunRelaxer = condition.guardRelaxer(::kmockUnitFunRelaxer as Function1<String, ReturnValue?>)
    }

    @Suppress("UNCHECKED_CAST")
    override fun useToStringRelaxer(toString: Function0<String>) {
        buildInRelaxer = ParameterizedRelaxer { toString.invoke() as ReturnValue }
    }

    @Suppress("UNCHECKED_CAST")
    override fun useHashCodeRelaxer(hashCode: Function0<Int>) {
        buildInRelaxer = ParameterizedRelaxer { hashCode.invoke() as ReturnValue }
    }

    @Suppress("UNCHECKED_CAST")
    override fun useEqualsRelaxer(equals: Function1<Any?, Boolean>) {
        buildInRelaxer = ParameterizedRelaxer { other -> equals.invoke(other) as ReturnValue }
    }

    override fun useRelaxerIf(
        condition: Boolean,
        relaxer: Function1<String, ReturnValue>
    ) {
        this.relaxer = condition.guardRelaxer(relaxer)
    }

    override fun useSpyIf(spyTarget: Any?, spyOn: SideEffect) {
        this.spyOn = spyTarget.guardSpy(spyOn)
    }

    @Suppress("UNCHECKED_CAST")
    override fun useSpyOnEqualsIf(
        spyTarget: Any?,
        equals: Function1<Any?, Boolean>,
        mockKlass: KClass<out Any>
    ) {
        this.spyOn = spyTarget.guardSpy(
            { other: Any? ->
                if (other != null && other::class == mockKlass) {
                    equals.invoke(other)
                } else {
                    spyTarget == other
                }
            } as SideEffect
        )
    }

    override fun getConfiguration(): NonIntrusiveFunConfiguration<ReturnValue, SideEffect> {
        finalizeRelaxers()

        return NonIntrusiveFunConfiguration(
            unitFunRelaxer = unitFunRelaxer,
            relaxer = relaxer,
            buildInRelaxer = buildInRelaxer,
            spyOn = spyOn,
        )
    }
}
