/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Relaxer
import tech.antibytes.kmock.KMockContract.ParameterizedRelaxer
import tech.antibytes.kmock.KMockContract.NonIntrusiveFunConfiguration
import kotlin.reflect.KClass

internal class NonIntrusiveFunConfigurator<ReturnValue, SideEffect : Function<ReturnValue>> :
    KMockContract.NonIntrusiveFunConfigurator<ReturnValue, SideEffect>,
    KMockContract.NonIntrusiveConfigurationReceiver<NonIntrusiveFunConfiguration<ReturnValue, SideEffect>> {

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
    override fun useToStringRelaxer(parent: Any) {
        buildInRelaxer = ParameterizedRelaxer { parent.toString() as ReturnValue }
    }

    @Suppress("UNCHECKED_CAST")
    override fun useHashCodeRelaxer(parent: Any) {
        buildInRelaxer = ParameterizedRelaxer { parent.hashCode() as ReturnValue }
    }

    @Suppress("UNCHECKED_CAST")
    override fun useEqualsRelaxer(parent: Any) {
        buildInRelaxer = ParameterizedRelaxer { other -> (parent == other) as ReturnValue }
    }

    override fun useRelaxerIf(
        condition: Boolean,
        relaxer: Function1<String, ReturnValue>
    ) {
        this.relaxer = condition.guardRelaxer(relaxer)
    }

    override fun useSpyIf(spy: Any?, spyOn: SideEffect) {
        this.spyOn = spy.guardSpy(spyOn)
    }

    @Suppress("UNCHECKED_CAST")
    override fun useSpyOnEqualIf(
        spy: Any?,
        parent: Any,
        mockKlass: KClass<out Any>
    ) {
        this.spyOn = spy.guardSpy(
            { other: Any? ->
                val toCompareWith = if (other != null && other::class == mockKlass) {
                    parent
                } else {
                    spy
                }

                toCompareWith == other
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
