/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.RelaxationFunConfiguration
import tech.antibytes.kmock.KMockContract.ParameterizedRelaxer
import tech.antibytes.kmock.KMockContract.Relaxer

internal class NonIntrusiveFunConfigurator<ReturnValue, SideEffect : Function<ReturnValue>> :
    KMockContract.RelaxationFunConfigurator<ReturnValue, SideEffect>,
    KMockContract.RelaxationConfigurationExtractor<RelaxationFunConfiguration<ReturnValue, SideEffect>> {

    private var unitFunRelaxer: Relaxer<ReturnValue?>? = null
    private var buildInRelaxer: ParameterizedRelaxer<Any?, ReturnValue>? = null
    private var relaxer: Relaxer<ReturnValue>? = null

    private fun finalizeRelaxers() {
        if (unitFunRelaxer != null || buildInRelaxer != null) {
            relaxer = null
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

    override fun getConfiguration(): RelaxationFunConfiguration<ReturnValue, SideEffect> {
        finalizeRelaxers()

        return RelaxationFunConfiguration(
            unitFunRelaxer = unitFunRelaxer,
            relaxer = relaxer,
            buildInRelaxer = buildInRelaxer,
        )
    }
}
