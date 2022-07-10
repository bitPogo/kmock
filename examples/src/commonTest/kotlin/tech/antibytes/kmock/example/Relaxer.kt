/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.example

import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass
import tech.antibytes.kfixture.PublicApi
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.Relaxer

@ThreadLocal
object Fixture {
    var fixture: PublicApi.Fixture? = null
}

@Relaxer
@Suppress("UNUSED_PARAMETER")
internal inline fun <reified T> relax(id: String): T {
    if (Fixture.fixture == null) {
        Fixture.fixture = kotlinFixture()
    }

    return Fixture.fixture!!.fixture()
}

@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
internal fun <T> relax(id: String, type0: KClass<CharSequence>, type1: KClass<Comparable<*>>): T {
    return Fixture.fixture!!.fixture<String>() as T
}

@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
internal fun <T> relax(id: String, type0: KClass<Any>): T {
    return Fixture.fixture!!.fixture<Int>() as T
}
