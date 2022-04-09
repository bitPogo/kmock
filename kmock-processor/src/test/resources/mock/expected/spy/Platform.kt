package mock.template.spy

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false,
) : Platform<K, L> where L : Any, L : Comparable<L> {
    private val __spyOn: Platform<K, L>? = spyOn

    public override var template: L
        get() = _template.onGet {
            useSpyIf(__spyOn) { __spyOn!!.template }
        }
        set(`value`) = _template.onSet(value) {
            useSpyIf(__spyOn) { __spyOn!!.template = value }
        }

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.spy.PlatformMock#_template", collector =
        verifier, freeze = freeze)

    public override val ozz: Int
        get() = _ozz.onGet {
            useSpyIf(__spyOn) { __spyOn!!.ozz }
        }

    public val _ozz: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.spy.PlatformMock#_ozz", collector = verifier,
            freeze = freeze)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_fooWithAny", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _fooWithAnys: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) -> kotlin.Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_fooWithAnys", collector =
    verifier, freeze = freeze) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public val _barWithInt: KMockContract.SyncFunProxy<Any, (kotlin.Int) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_barWithInt", collector =
        verifier, freeze = freeze)

    public val _barWithInts: KMockContract.SyncFunProxy<Any, (kotlin.IntArray) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_barWithInts", collector =
        verifier, freeze = freeze)

    public val _buzzWithString: KMockContract.AsyncFunProxy<L, suspend (kotlin.String) -> L> =
        ProxyFactory.createAsyncFunProxy("mock.template.spy.PlatformMock#_buzzWithString", collector =
        verifier, freeze = freeze)

    public val _buzzWithStrings: KMockContract.AsyncFunProxy<L, suspend (Array<out kotlin.String>) ->
    L> = ProxyFactory.createAsyncFunProxy("mock.template.spy.PlatformMock#_buzzWithStrings",
        collector = verifier, freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_toString", collector =
        verifier, freeze = freeze, ignorableForVerification = true) {
            useToStringRelaxer { super.toString() }
        }

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_equals", collector =
        verifier, freeze = freeze, ignorableForVerification = true) {
            useEqualsRelaxer { other ->
                super.equals(other)
            }
        }

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_hashCode", collector =
        verifier, freeze = freeze, ignorableForVerification = true) {
            useHashCodeRelaxer { super.hashCode() }
        }

    public override fun <T> foo(payload: T): Unit = _fooWithAny.invoke(payload) {
        useSpyIf(__spyOn) { __spyOn!!.foo(payload) }
    }

    public override fun <T> foo(vararg payload: T): Unit = _fooWithAnys.invoke(payload) {
        useSpyIf(__spyOn) { __spyOn!!.foo(*payload) }
    }

    public override fun bar(arg0: Int): Any = _barWithInt.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.bar(arg0) }
    }

    public override fun bar(vararg arg0: Int): Any = _barWithInts.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.bar(*arg0) }
    }

    public override suspend fun buzz(arg0: String): L = _buzzWithString.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.buzz(arg0) }
    }

    public override suspend fun buzz(vararg arg0: String): L = _buzzWithStrings.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.buzz(*arg0) }
    }

    public override fun toString(): String = _toString.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.toString() }
    }

    public override fun equals(other: Any?): Boolean = _equals.invoke(other) {
        useSpyOnEqualsIf(
            spyTarget = __spyOn,
            other = other,
            spyOn = { super.equals(other) },
            mockKlass = PlatformMock::class
        )
    }

    public override fun hashCode(): Int = _hashCode.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.hashCode() }
    }

    public fun _clearMock(): Unit {
        _template.clear()
        _ozz.clear()
        _fooWithAny.clear()
        _fooWithAnys.clear()
        _barWithInt.clear()
        _barWithInts.clear()
        _buzzWithString.clear()
        _buzzWithStrings.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
