package mock.template.spy

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import mock.template.spy.relaxed
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.AsyncFunProxy
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.SyncFunProxy
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class RelaxedMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Relaxed<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Relaxed<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> = if (spyOn == null) {
        ProxyFactory.createPropertyProxy("mock.template.spy.RelaxedMock#_template", spyOnGet = null,
            spyOnSet = null, collector = verifier, freeze = freeze, relaxer = if (relaxed) { {
                    mockId -> relaxed(mockId) } } else { null })} else {
        ProxyFactory.createPropertyProxy("mock.template.spy.RelaxedMock#_template", spyOnGet = {
            __spyOn!!.template }, spyOnSet = { __spyOn!!.template = it; Unit }, collector =
        verifier, freeze = freeze, relaxer = if (relaxed) { { mockId -> relaxed(mockId) } } else
        { null })}


    public override val ozz: Int
        get() = _ozz.onGet()

    public val _ozz: KMockContract.PropertyProxy<Int> = if (spyOn == null) {
        ProxyFactory.createPropertyProxy("mock.template.spy.RelaxedMock#_ozz", spyOnGet = null,
            collector = verifier, freeze = freeze, relaxer = if (relaxed) { { mockId ->
                relaxed(mockId) } } else { null })} else {
        ProxyFactory.createPropertyProxy("mock.template.spy.RelaxedMock#_ozz", spyOnGet = {
            __spyOn!!.ozz }, collector = verifier, freeze = freeze, relaxer = if (relaxed) { {
                mockId -> relaxed(mockId) } } else { null })}


    public val _foo: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.RelaxedMock#_foo", spyOn = if (spyOn !=
            null) { { payload ->
            __spyOn!!.foo(payload) } } else { null }, collector = verifier, freeze = freeze,
            unitFunRelaxer = if (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = if
                                                                                                        (relaxed) { { mockId -> relaxed(mockId) } } else { null }, buildInRelaxer = null)

    public val _bar: KMockContract.SyncFunProxy<Any, (kotlin.Int) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.RelaxedMock#_bar", spyOn = if (spyOn !=
            null) { { arg0 ->
            __spyOn!!.bar(arg0) } } else { null }, collector = verifier, freeze = freeze, relaxer = if
                                                                                                        (relaxed) { { mockId -> relaxed(mockId) } } else { null })

    public val _buzz: KMockContract.AsyncFunProxy<L, suspend (kotlin.String) -> L> =
        ProxyFactory.createAsyncFunProxy("mock.template.spy.RelaxedMock#_buzz", spyOn = if (spyOn !=
            null) { { arg0 ->
            __spyOn!!.buzz(arg0) } } else { null }, collector = verifier, freeze = freeze, relaxer = if
                                                                                                         (relaxed) { { mockId -> relaxed(mockId) } } else { null })

    private val __spyOn: Relaxed<K, L>? = spyOn

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.RelaxedMock#_toString", spyOn = if (spyOn
            != null) { { __spyOn!!.toString() } } else { null }, collector = verifier, freeze = freeze,
            unitFunRelaxer = null, relaxer = null, buildInRelaxer = { super.toString() },
            ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.RelaxedMock#_equals", spyOn = if (spyOn !=
            null) { { other ->
            __spyOn!!.equals(other) } } else { null }, collector = verifier, freeze = freeze,
            unitFunRelaxer = null, relaxer = null, buildInRelaxer = { other -> super.equals(other) },
            ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.RelaxedMock#_hashCode", spyOn = if (spyOn
            != null) { { __spyOn!!.hashCode() } } else { null }, collector = verifier, freeze = freeze,
            unitFunRelaxer = null, relaxer = null, buildInRelaxer = { super.hashCode() },
            ignorableForVerification = true)

    public override fun <T> foo(payload: T): Unit = _foo.invoke(payload)

    public override fun bar(arg0: Int): Any = _bar.invoke(arg0)

    public override suspend fun buzz(arg0: String): L = _buzz.invoke(arg0)

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean {
        return if(other is RelaxedMock<*, *> && __spyOn != null) {
            super.equals(other)
        } else {
            _equals.invoke(other)
        }
    }

    public override fun hashCode(): Int = _hashCode.invoke()

    public fun _clearMock(): Unit {
        _template.clear()
        _ozz.clear()
        _foo.clear()
        _bar.clear()
        _buzz.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
