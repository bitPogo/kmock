/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.RelaxationPropertyConfiguration
import tech.antibytes.kmock.KMockContract.Relaxer

internal class NonIntrusivePropertyConfigurator<Value> :
    KMockContract.RelaxationPropertyConfigurator<Value>,
    KMockContract.RelaxationConfigurationExtractor<RelaxationPropertyConfiguration<Value>> {
    private var relaxer: Relaxer<Value>? = null

    override fun useRelaxerIf(condition: Boolean, relaxer: Function1<String, Value>) {
        this.relaxer = condition.guardRelaxer(relaxer)
    }

    override fun getConfiguration(): RelaxationPropertyConfiguration<Value> {
        return RelaxationPropertyConfiguration(relaxer = relaxer,)
    }
}
