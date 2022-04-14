package mock.template.relaxed

import kotlin.Any
import kotlin.Boolean
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import mock.template.relaxed.relaxed
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonMock(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Common {
    public override val buzz: String
        get() = _buzz.onGet {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId) }
        }

    public val _buzz: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.relaxed.CommonMock#_buzz", collector =
        verifier, freeze = freeze)

    public val _foo: KMockContract.SyncFunProxy<String, (kotlin.Any) -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.CommonMock#_foo", collector = verifier,
            freeze = freeze)

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

    public override fun foo(payload: Any): String = _foo.invoke(payload) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId) }
    }

    public override fun oo(vararg payload: Any): String = _oo.invoke(payload) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId) }
    }

    public override suspend fun bar(payload: Any): String = _bar.invoke(payload) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId) }
    }

    public override suspend fun ar(vararg payload: Any): String = _ar.invoke(payload) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId) }
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
