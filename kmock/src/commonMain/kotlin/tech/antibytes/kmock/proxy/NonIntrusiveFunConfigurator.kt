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

// FIXME: CLEAR according to hierarchies
internal class NonIntrusiveFunConfigurator<ReturnValue, SideEffect : Function<ReturnValue>> :
    KMockContract.NonIntrusiveFunConfigurator<ReturnValue, SideEffect>,
    KMockContract.NonIntrusiveFunConfigurationReceiver<ReturnValue, SideEffect> {

    private var unitFunRelaxer: Relaxer<ReturnValue?>? = null
    private var buildInRelaxer: ParameterizedRelaxer<Any?, ReturnValue>? = null
    private var relaxer: Relaxer<ReturnValue>? = null
    private var spyOn: SideEffect? = null

    @Suppress("UNCHECKED_CAST")
    override fun relaxUnitFunIf(condition: Boolean) {
        unitFunRelaxer = if (condition) {
            kmockUnitFunRelaxer as Relaxer<ReturnValue?>?
        } else {
            null
        }
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

    override fun useRelaxerIf(condition: Boolean, relaxer: Function1<String, ReturnValue>) {
        this.relaxer = if (condition) {
            Relaxer { mockId -> relaxer.invoke(mockId) }
        } else {
            null
        }
    }

    override fun useSpyIf(spy: Any?, spyOn: SideEffect) {
        this.spyOn = if (spy != null) {
            spyOn
        } else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun useSpyOnEqualIf(
        spy: Any?,
        parent: Any,
        mockKlass: KClass<out Any>
    ) {
        this.spyOn = if (spy != null) {
            { other: Any? ->
                val toCompareWith = if (other != null && other::class == mockKlass) {
                    parent
                } else {
                    spy
                }

                toCompareWith == other
            } as SideEffect
        } else {
            null
        }
    }

    override fun getConfiguration(): NonIntrusiveFunConfiguration<ReturnValue, SideEffect> {
        return NonIntrusiveFunConfiguration(
            unitFunRelaxer = unitFunRelaxer,
            relaxer = relaxer,
            buildInRelaxer = buildInRelaxer,
            spyOn = spyOn,
        )
    }
}
