package multi

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import multi.template.shared.Regular1
import multi.template.shared.SharedContractRegular
import multi.template.shared.nested.Regular3
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SharedMultiMock<MultiMock>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: MultiMock? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Regular1, SharedContractRegular.Regular2, Regular3 where MultiMock : Regular1, MultiMock :
SharedContractRegular.Regular2, MultiMock : Regular3 {
    private val __spyOn: MultiMock? = spyOn

    public override val something: Int
        get() = _something.executeOnGet {
            useSpyIf(__spyOn) { __spyOn!!.something }
        }

    public val _something: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("multi.SharedMultiMock#_something", collector = collector,
            freeze = freeze)

    public override val anything: Any
        get() = _anything.executeOnGet {
            useSpyIf(__spyOn) { __spyOn!!.anything }
        }

    public val _anything: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("multi.SharedMultiMock#_anything", collector = collector,
            freeze = freeze)

    public override val somethingElse: String
        get() = _somethingElse.executeOnGet {
            useSpyIf(__spyOn) { __spyOn!!.somethingElse }
        }

    public val _somethingElse: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("multi.SharedMultiMock#_somethingElse", collector =
        collector, freeze = freeze)

    public val _doSomething: KMockContract.SyncFunProxy<Int, () -> Int> =
        ProxyFactory.createSyncFunProxy("multi.SharedMultiMock#_doSomething", collector = collector,
            freeze = freeze)

    public val _doAnything: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("multi.SharedMultiMock#_doAnything", collector = collector,
            freeze = freeze)

    public val _doSomethingElse: KMockContract.SyncFunProxy<String, () -> String> =
        ProxyFactory.createSyncFunProxy("multi.SharedMultiMock#_doSomethingElse", collector =
        collector, freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> String> =
        ProxyFactory.createSyncFunProxy("multi.SharedMultiMock#_toString", collector = collector,
            freeze = freeze, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (Any?) -> Boolean> =
        ProxyFactory.createSyncFunProxy("multi.SharedMultiMock#_equals", collector = collector, freeze
        = freeze, ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> Int> =
        ProxyFactory.createSyncFunProxy("multi.SharedMultiMock#_hashCode", collector = collector,
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
            mockKlass = SharedMultiMock::class
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
