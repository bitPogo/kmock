package multi

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import multi.template.common.CommonContractRegular
import multi.template.common.Regular1
import multi.template.common.nested.Regular3
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonMultiMock<MultiMock>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: MultiMock? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Regular1, CommonContractRegular.Regular2, Regular3 where MultiMock : Regular1, MultiMock :
CommonContractRegular.Regular2, MultiMock : Regular3 {
    private val __spyOn: MultiMock? = spyOn

    public override val something: Int
        get() = _something.onGet {
            useSpyIf(__spyOn) { __spyOn!!.something }
        }

    public val _something: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("multi.CommonMultiMock#_something", collector = verifier,
            freeze = freeze)

    public override val anything: Any
        get() = _anything.onGet {
            useSpyIf(__spyOn) { __spyOn!!.anything }
        }

    public val _anything: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("multi.CommonMultiMock#_anything", collector = verifier,
            freeze = freeze)

    public override val somethingElse: String
        get() = _somethingElse.onGet {
            useSpyIf(__spyOn) { __spyOn!!.somethingElse }
        }

    public val _somethingElse: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("multi.CommonMultiMock#_somethingElse", collector = verifier,
            freeze = freeze)

    public val _doSomething: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("multi.CommonMultiMock#_doSomething", collector = verifier,
            freeze = freeze)

    public val _doAnything: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.CommonMultiMock#_doAnything", collector = verifier,
            freeze = freeze)

    public val _doSomethingElse: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("multi.CommonMultiMock#_doSomethingElse", collector =
        verifier, freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("multi.CommonMultiMock#_toString", collector = verifier,
            freeze = freeze, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        ProxyFactory.createSyncFunProxy("multi.CommonMultiMock#_equals", collector = verifier, freeze
        = freeze, ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("multi.CommonMultiMock#_hashCode", collector = verifier,
            freeze = freeze, ignorableForVerification = true)

    public override fun doSomething(): Int = _doSomething.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.doSomething() }
    }

    public override fun doAnything(): Any = _doAnything.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.doAnything() }
    }

    public override fun doSomethingElse(): String = _doSomethingElse.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.doSomethingElse() }
    }

    public override fun toString(): String = _toString.invoke() {
        useRelaxerIf(true) { super.toString() }
        useSpyIf(__spyOn) { __spyOn!!.toString() }
    }

    public override fun equals(other: Any?): Boolean = _equals.invoke(other) {
        useRelaxerIf(true) { super.equals(other) }
        useSpyOnEqualsIf(
            spyTarget = __spyOn,
            other = other,
            spyOn = { super.equals(other) },
            mockKlass = CommonMultiMock::class
        )
    }

    public override fun hashCode(): Int = _hashCode.invoke() {
        useRelaxerIf(true) { super.hashCode() }
        useSpyIf(__spyOn) { __spyOn!!.hashCode() }
    }

    public fun _clearMock(): Unit {
        _something.clear()
        _anything.clear()
        _somethingElse.clear()
        _doSomething.clear()
        _doAnything.clear()
        _doSomethingElse.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
