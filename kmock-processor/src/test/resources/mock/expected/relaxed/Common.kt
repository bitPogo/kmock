package mock.template.relaxed

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import mock.template.relaxed.relaxed
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Common<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.onGet {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,
                type0 = kotlin.Any::class,
                type1 = kotlin.Comparable::class,) }
        }
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.relaxed.CommonMock#_template", collector =
        verifier, freeze = freeze)

    public override val buzz: String
        get() = _buzz.onGet {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
        }

    public val _buzz: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.relaxed.CommonMock#_buzz", collector =
        verifier, freeze = freeze)

    public val _fooWithAny: KMockContract.SyncFunProxy<String, (kotlin.Any) -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.CommonMock#_fooWithAny", collector =
        verifier, freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.CommonMock#_fooWithVoid", collector =
        verifier, freeze = freeze)

    public val _fooWithString: KMockContract.SyncFunProxy<L, (kotlin.String) -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.CommonMock#_fooWithString", collector =
        verifier, freeze = freeze)

    public val _fooBarWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.CommonMock#_fooBarWithAny", collector =
        verifier, freeze = freeze)

    public val _fooBarWithVoid: KMockContract.SyncFunProxy<K?, () -> K?> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.CommonMock#_fooBarWithVoid", collector
        = verifier, freeze = freeze)

    public val _oo: KMockContract.SyncFunProxy<String, (Array<out kotlin.Any>) -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.CommonMock#_oo", collector = verifier,
            freeze = freeze)

    public val _bar: KMockContract.AsyncFunProxy<String, suspend (kotlin.Any) -> kotlin.String> =
        ProxyFactory.createAsyncFunProxy("mock.template.relaxed.CommonMock#_bar", collector =
        verifier, freeze = freeze)

    public val _ar: KMockContract.AsyncFunProxy<String, suspend (Array<out kotlin.Any>) ->
    kotlin.String> = ProxyFactory.createAsyncFunProxy("mock.template.relaxed.CommonMock#_ar",
        collector = verifier, freeze = freeze)

    public val _buzzWithVoid: KMockContract.SyncFunProxy<Unit, () -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.CommonMock#_buzzWithVoid", collector =
        verifier, freeze = freeze)

    public override fun foo(payload: Any): String = _fooWithAny.invoke(payload) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,
            type0 = kotlin.Any::class,) }
    } as T

    public override fun foo(payload: String): L = _fooWithString.invoke(payload) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,
            type0 = kotlin.Any::class,
            type1 = kotlin.Comparable::class,) }
    }

    public override fun <T : Any> fooBar(payload: T): Unit = _fooBarWithAny.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : K?> fooBar(): T = _fooBarWithVoid.invoke() {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,
            type0 = kotlin.Any::class,) }
    } as T

    public override fun oo(vararg payload: Any): String = _oo.invoke(payload) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
    }

    public override suspend fun bar(payload: Any): String = _bar.invoke(payload) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
    }

    public override suspend fun ar(vararg payload: Any): String = _ar.invoke(payload) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
    }

    public override fun buzz(): Unit = _buzzWithVoid.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _template.clear()
        _buzz.clear()
        _fooWithAny.clear()
        _fooWithVoid.clear()
        _fooWithString.clear()
        _fooBarWithAny.clear()
        _fooBarWithVoid.clear()
        _oo.clear()
        _bar.clear()
        _ar.clear()
        _buzzWithVoid.clear()
    }
}
