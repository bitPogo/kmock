package mock.template.relaxed

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import mock.template.relaxed.relaxed
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Platform {
    public override val buzz: String
        get() = _buzz.executeOnGet {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
        }

    public val _buzz: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.relaxed.PlatformMock#_buzz", collector =
        collector, freeze = freeze)

    public val _foo: KMockContract.SyncFunProxy<String, (Any) -> String> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.PlatformMock#_foo", collector =
        collector, freeze = freeze)

    public val _oo: KMockContract.SyncFunProxy<String, (Array<out Any>) -> String> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.PlatformMock#_oo", collector =
        collector, freeze = freeze)

    public val _bar: KMockContract.AsyncFunProxy<String, suspend (Any) -> String> =
        ProxyFactory.createAsyncFunProxy("mock.template.relaxed.PlatformMock#_bar", collector =
        collector, freeze = freeze)

    public val _ar: KMockContract.AsyncFunProxy<String, suspend (Array<out Any>) -> String> =
        ProxyFactory.createAsyncFunProxy("mock.template.relaxed.PlatformMock#_ar", collector =
        collector, freeze = freeze)

    public val _buzzWithVoid: KMockContract.SyncFunProxy<Unit, () -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.PlatformMock#_buzzWithVoid", collector
        = collector, freeze = freeze)

    public override fun foo(payload: Any): String = _foo.invoke(payload) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
    }

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
        _buzz.clear()
        _foo.clear()
        _oo.clear()
        _bar.clear()
        _ar.clear()
        _buzzWithVoid.clear()
    }
}
