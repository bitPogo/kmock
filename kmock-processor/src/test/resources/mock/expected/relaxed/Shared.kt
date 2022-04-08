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

internal class SharedMock(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Shared? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false,
) : Shared {
    public override val buzz: String
        get() = _buzz.onGet()

    public val _buzz: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.relaxed.SharedMock#_buzz", collector =
        verifier, freeze = freeze) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId) }
        }

    public val _foo: KMockContract.SyncFunProxy<String, (kotlin.Any) -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.SharedMock#_foo", collector = verifier,
            freeze = freeze) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId) }
        }

    public val _bar: KMockContract.AsyncFunProxy<String, suspend (kotlin.Any) -> kotlin.String> =
        ProxyFactory.createAsyncFunProxy("mock.template.relaxed.SharedMock#_bar", collector =
        verifier, freeze = freeze) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId) }
        }

    public val _buzzWithVoid: KMockContract.SyncFunProxy<Unit, () -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.SharedMock#_buzzWithVoid", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun foo(payload: Any): String = _foo.invoke(payload)

    public override suspend fun bar(payload: Any): String = _bar.invoke(payload)

    public override fun buzz(): Unit = _buzzWithVoid.invoke()

    public fun _clearMock(): Unit {
        _buzz.clear()
        _foo.clear()
        _bar.clear()
        _buzzWithVoid.clear()
    }
}
