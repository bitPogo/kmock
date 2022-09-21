package mock.template.spy

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Int
import kotlin.IntArray
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class AliasPlatformMock<K : Any, L>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform<K, L>? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Platform<K, L> where L : Any, L : Comparable<L> {
    private val __spyOn: Platform<K, L>? = spyOn

    public override var template: L
        get() = _template.executeOnGet {
            useSpyIf(__spyOn) { __spyOn!!.template }
        }
        set(`value`) = _template.executeOnSet(value) {
            useSpyIf(__spyOn) { __spyOn!!.template = value }
        }

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.spy.AliasPlatformMock#_template", collector =
        collector, freeze = freeze)

    public override val ozz: Int
        get() = _ozz.executeOnGet {
            useSpyIf(__spyOn) { __spyOn!!.ozz }
        }

    public val _ozz: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.spy.AliasPlatformMock#_ozz", collector =
        collector, freeze = freeze)

    public val _fooWithZTAny: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AliasPlatformMock#_fooWithZTAny", collector
        = collector, freeze = freeze)

    public val _fooWithZTAnys: KMockContract.SyncFunProxy<Unit, (Array<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AliasPlatformMock#_fooWithZTAnys",
            collector = collector, freeze = freeze)

    public val _barWithInt: KMockContract.SyncFunProxy<Any, (Int) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AliasPlatformMock#_barWithInt", collector =
        collector, freeze = freeze)

    public val _barWithInts: KMockContract.SyncFunProxy<Any, (IntArray) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AliasPlatformMock#_barWithInts", collector
        = collector, freeze = freeze)

    public val _buzzWithString: KMockContract.AsyncFunProxy<L, suspend (String) -> L> =
        ProxyFactory.createAsyncFunProxy("mock.template.spy.AliasPlatformMock#_buzzWithString",
            collector = collector, freeze = freeze)

    public val _buzzWithStrings: KMockContract.AsyncFunProxy<L, suspend (Array<out String>) -> L> =
        ProxyFactory.createAsyncFunProxy("mock.template.spy.AliasPlatformMock#_buzzWithStrings",
            collector = collector, freeze = freeze)

    public val _izz: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AliasPlatformMock#_izz", collector =
        collector, freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> String> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AliasPlatformMock#_toString", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (Any?) -> Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AliasPlatformMock#_equals", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AliasPlatformMock#_hashCode", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    public override fun <T> foo(payload: T): Unit = _fooWithZTAny.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.foo(payload) }
    }

    public override fun <T> foo(vararg payload: T): Unit = _fooWithZTAnys.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
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

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : CharSequence, T : Comparable<List<Array<T>>> =
        _izz.invoke() {
            useSpyIf(__spyOn) { __spyOn!!.izz<T>() }
        } as T

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
            mockKlass = AliasPlatformMock::class
        )
    }

    public override fun hashCode(): Int = _hashCode.invoke() {
        useRelaxerIf(true) { super.hashCode() }
        useSpyIf(__spyOn) { __spyOn!!.hashCode() }
    }

    public fun _clearMock(): Unit {
        _template.clear()
        _ozz.clear()
        _fooWithZTAny.clear()
        _fooWithZTAnys.clear()
        _barWithInt.clear()
        _barWithInts.clear()
        _buzzWithString.clear()
        _buzzWithStrings.clear()
        _izz.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
