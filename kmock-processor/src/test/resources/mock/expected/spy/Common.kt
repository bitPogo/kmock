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

internal class CommonMock<K : Any, L>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Common<K, L> where L : Any, L : Comparable<L> {
    private val __spyOn: Common<K, L>? = spyOn

    public override var template: L
        get() = _template.onGet {
            useSpyIf(__spyOn) { __spyOn!!.template }
        }
        set(`value`) = _template.onSet(value) {
            useSpyIf(__spyOn) { __spyOn!!.template = value }
        }

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.spy.CommonMock#_template", collector =
        collector, freeze = freeze)

    public override val ozz: Int
        get() = _ozz.onGet {
            useSpyIf(__spyOn) { __spyOn!!.ozz }
        }

    public val _ozz: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.spy.CommonMock#_ozz", collector = collector,
            freeze = freeze)

    public val _foo: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_foo", collector = collector,
            freeze = freeze)

    public val _oo: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out kotlin.Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_oo", collector = collector,
            freeze = freeze)

    public val _bar: KMockContract.SyncFunProxy<Any, (Int) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_bar", collector = collector,
            freeze = freeze)

    public val _ar: KMockContract.SyncFunProxy<Any, (IntArray) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_ar", collector = collector,
            freeze = freeze)

    public val _buzz: KMockContract.AsyncFunProxy<L, suspend (String) -> L> =
        ProxyFactory.createAsyncFunProxy("mock.template.spy.CommonMock#_buzz", collector = collector,
            freeze = freeze)

    public val _uzz: KMockContract.AsyncFunProxy<L, suspend (kotlin.Array<out kotlin.String>) -> L> =
        ProxyFactory.createAsyncFunProxy("mock.template.spy.CommonMock#_uzz", collector = collector,
            freeze = freeze)

    public val _izz: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_izz", collector = collector,
            freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> String> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_toString", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (Any?) -> Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_equals", collector = collector,
            freeze = freeze, ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_hashCode", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    public override fun <T> foo(payload: T): Unit = _foo.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.foo(payload) }
    }

    public override fun <T> oo(vararg payload: T): Unit = _oo.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.oo(*payload) }
    }

    public override fun bar(arg0: Int): Any = _bar.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.bar(arg0) }
    }

    public override fun ar(vararg arg0: Int): Any = _ar.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.ar(*arg0) }
    }

    public override suspend fun buzz(arg0: String): L = _buzz.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.buzz(arg0) }
    }

    public override suspend fun uzz(vararg arg0: String): L = _uzz.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.uzz(*arg0) }
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
            mockKlass = CommonMock::class
        )
    }

    public override fun hashCode(): Int = _hashCode.invoke() {
        useRelaxerIf(true) { super.hashCode() }
        useSpyIf(__spyOn) { __spyOn!!.hashCode() }
    }

    public fun _clearMock(): Unit {
        _template.clear()
        _ozz.clear()
        _foo.clear()
        _oo.clear()
        _bar.clear()
        _ar.clear()
        _buzz.clear()
        _uzz.clear()
        _izz.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
