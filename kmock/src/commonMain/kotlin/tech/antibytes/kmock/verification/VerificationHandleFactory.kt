/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.ArgumentConstraint
import tech.antibytes.kmock.KMockContract.FunProxy
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.Proxy

private fun <T> traverseMockAndShare(
    proxy: Proxy<*, T>,
    action: T.() -> Boolean
): VerificationHandle {
    val callIndices = mutableListOf<Int>()

    for (idx in 0 until proxy.calls) {
        if (action(proxy.getArgumentsForCall(idx))) {
            callIndices.add(idx)
        }
    }

    val handle = VerificationHandle(proxy.id, callIndices)
    shareHandle(proxy, handle)

    return handle
}

private fun shareHandle(
    proxy: Proxy<*, *>,
    handle: VerificationHandle
) {
    if (proxy.verificationBuilderReference != null) {
        proxy.verificationBuilderReference!!.add(handle)
    }
}

/**
 * VerificationHandle Factory, which collects all invocation of an FunProxy.
 * @return VerificationHandle
 * @author Matthias Geisler
 */
fun FunProxy<*, *>.hasBeenCalled(): VerificationHandle = traverseMockAndShare(this) { hasBeenCalledWith() }

/**
 * VerificationHandle Factory, which collects all invocation of an FunProxy which contain no Arguments.
 * @return VerificationHandle
 * @author Matthias Geisler
 */
fun FunProxy<*, *>.hasBeenCalledWithVoid(): VerificationHandle = traverseMockAndShare(this) { hasBeenCalledWithVoid() }

/**
 * VerificationHandle Factory, which collects all invocation of an FunProxy which matches the given Arguments.
 * @param arguments or constraints which calls must match. The arguments/constraints must follow the order of the mocked/stubbed function but can contain gaps and do not need to all arguments.
 * @return VerificationHandle
 * @see ArgumentConstraint
 * @author Matthias Geisler
 */
fun FunProxy<*, *>.hasBeenCalledWith(
    vararg arguments: Any?
): VerificationHandle = traverseMockAndShare(this) { hasBeenCalledWith(*arguments) }

/**
 * VerificationHandle Factory, which collects all invocation of an FunProxy which matches the given Arguments.
 * @param arguments or constraints which calls must match. The arguments/constraints must follow the order of the mocked/stubbed function and need to contain all arguments/constraints.
 * @return VerificationHandle
 * @see ArgumentConstraint
 * @author Matthias Geisler
 */
fun FunProxy<*, *>.hasBeenStrictlyCalledWith(
    vararg arguments: Any?
): VerificationHandle = traverseMockAndShare(this) { hasBeenStrictlyCalledWith(*arguments) }

/**
 * VerificationHandle Factory, which collects all invocation of an FunProxy which matches the given Arguments.
 * @param illegal arguments/constraints which calls does not match.
 * @return VerificationHandle
 * @see ArgumentConstraint
 * @author Matthias Geisler
 */
fun FunProxy<*, *>.hasBeenCalledWithout(
    vararg illegal: Any?
): VerificationHandle = traverseMockAndShare(this) { hasBeenCalledWithout(*illegal) }

/**
 * VerificationHandle Factory, which collects all invocation of an PropertyProxy Getter.
 * @return VerificationHandle
 * @author Matthias Geisler
 */
fun PropertyProxy<*>.wasGotten(): VerificationHandle = traverseMockAndShare(this) { wasGotten() }

/**
 * VerificationHandle Factory, which collects all invocation of an PropertyProxy Setter.
 * @return VerificationHandle
 * @author Matthias Geisler
 */
fun PropertyProxy<*>.wasSet(): VerificationHandle = traverseMockAndShare(this) { wasSet() }

/**
 * VerificationHandle Factory, which collects all invocation of an PropertyProxy Setter with the given Value.
 * @return VerificationHandle
 * @param value argument/constraint which calls must match.
 * @see ArgumentConstraint
 * @author Matthias Geisler
 */
fun PropertyProxy<*>.wasSetTo(
    value: Any?
): VerificationHandle = traverseMockAndShare(this) { wasSetTo(value) }
