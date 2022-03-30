package mock.template.spy

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.AsyncFunProxy
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.SyncFunProxy
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class PlatformMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Platform<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> = if (spyOn == null) {
        ProxyFactory.createPropertyProxy("mock.template.spy.PlatformMock#_template", spyOnGet =
        null, spyOnSet = null, collector = verifier, freeze = freeze, relaxer = null)} else {
        ProxyFactory.createPropertyProxy("mock.template.spy.PlatformMock#_template", spyOnGet = {
            __spyOn!!.template }, spyOnSet = { __spyOn!!.template = it; Unit }, collector =
        verifier, freeze = freeze, relaxer = null)}


    public override val ozz: Int
        get() = _ozz.onGet()

    public val _ozz: KMockContract.PropertyProxy<Int> = if (spyOn == null) {
        ProxyFactory.createPropertyProxy("mock.template.spy.PlatformMock#_ozz", spyOnGet = null,
            collector = verifier, freeze = freeze, relaxer = null)} else {
        ProxyFactory.createPropertyProxy("mock.template.spy.PlatformMock#_ozz", spyOnGet = {
            __spyOn!!.ozz }, collector = verifier, freeze = freeze, relaxer = null)}


    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_fooWithAny", spyOn = if
                                                                                                  (spyOn != null) { { payload ->
            __spyOn!!.foo(payload) } } else { null }, collector = verifier, freeze = freeze,
            unitFunRelaxer = if (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer =
            null, buildInRelaxer = null)

    public val _fooWithAnys: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) -> kotlin.Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_fooWithAnys", spyOn = if
                                                                                                     (spyOn != null) { { payload ->
        __spyOn!!.foo(*payload) } } else { null }, collector = verifier, freeze = freeze,
        unitFunRelaxer = if (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer =
        null, buildInRelaxer = null)

    public val _barWithInt: KMockContract.SyncFunProxy<Any, (kotlin.Int) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_barWithInt", spyOn = if
                                                                                                  (spyOn != null) { { arg0 ->
            __spyOn!!.bar(arg0) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _barWithInts: KMockContract.SyncFunProxy<Any, (kotlin.IntArray) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_barWithInts", spyOn = if
                                                                                                   (spyOn != null) { { arg0 ->
            __spyOn!!.bar(*arg0) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _buzzWithString: KMockContract.AsyncFunProxy<L, suspend (kotlin.String) -> L> =
        ProxyFactory.createAsyncFunProxy("mock.template.spy.PlatformMock#_buzzWithString", spyOn = if
                                                                                                       (spyOn != null) { { arg0 ->
            __spyOn!!.buzz(arg0) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _buzzWithStrings: KMockContract.AsyncFunProxy<L, suspend (Array<out kotlin.String>) ->
    L> = ProxyFactory.createAsyncFunProxy("mock.template.spy.PlatformMock#_buzzWithStrings", spyOn
    = if (spyOn != null) { { arg0 ->
        __spyOn!!.buzz(*arg0) } } else { null }, collector = verifier, freeze = freeze, relaxer =
    null)

    private val __spyOn: Platform<K, L>? = spyOn

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_toString", spyOn = if (spyOn
            != null) { { __spyOn!!.toString() } } else { null }, collector = verifier, freeze = freeze,
            unitFunRelaxer = null, relaxer = null, buildInRelaxer = { super.toString() },
            ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_equals", spyOn = if (spyOn !=
            null) { { other ->
            __spyOn!!.equals(other) } } else { null }, collector = verifier, freeze = freeze,
            unitFunRelaxer = null, relaxer = null, buildInRelaxer = { other -> super.equals(other) },
            ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.PlatformMock#_hashCode", spyOn = if (spyOn
            != null) { { __spyOn!!.hashCode() } } else { null }, collector = verifier, freeze = freeze,
            unitFunRelaxer = null, relaxer = null, buildInRelaxer = { super.hashCode() },
            ignorableForVerification = true)

    public override fun <T> foo(payload: T): Unit = _fooWithAny.invoke(payload)

    public override fun <T> foo(vararg payload: T): Unit = _fooWithAnys.invoke(payload)

    public override fun bar(arg0: Int): Any = _barWithInt.invoke(arg0)

    public override fun bar(vararg arg0: Int): Any = _barWithInts.invoke(arg0)

    public override suspend fun buzz(arg0: String): L = _buzzWithString.invoke(arg0)

    public override suspend fun buzz(vararg arg0: String): L = _buzzWithStrings.invoke(arg0)

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean {
        return if(other is PlatformMock<*, *> && __spyOn != null) {
            super.equals(other)
        } else {
            _equals.invoke(other)
        }
    }

    public override fun hashCode(): Int = _hashCode.invoke()

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
