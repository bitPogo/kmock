package mock.template.relaxed

import kotlin.Any
import kotlin.Boolean
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import mock.template.relaxed.relaxed
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SharedMock<T>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Shared<T>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Shared<T> {
    public override val buzz: String
        get() = _buzz.onGet {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
        }

    public val _buzz: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.relaxed.SharedMock#_buzz", collector =
        collector, freeze = freeze)

    public override val uzz: T
        get() = _uzz.onGet {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,
                type0 = kotlin.Any::class,) as T }
        }

    public val _uzz: KMockContract.PropertyProxy<T> =
        ProxyFactory.createPropertyProxy("mock.template.relaxed.SharedMock#_uzz", collector =
        collector, freeze = freeze)

    public val _foo: KMockContract.SyncFunProxy<String, (Any) -> String> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.SharedMock#_foo", collector =
        collector, freeze = freeze)

    public val _bar: KMockContract.AsyncFunProxy<String, suspend (Any) -> String> =
        ProxyFactory.createAsyncFunProxy("mock.template.relaxed.SharedMock#_bar", collector =
        collector, freeze = freeze)

    public val _buzzWithVoid: KMockContract.SyncFunProxy<Unit, () -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.SharedMock#_buzzWithVoid", collector =
        collector, freeze = freeze)

    public override fun foo(payload: Any): String = _foo.invoke(payload) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
    }

    public override suspend fun bar(payload: Any): String = _bar.invoke(payload) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
    }

    public override fun buzz(): Unit = _buzzWithVoid.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _buzz.clear()
        _uzz.clear()
        _foo.clear()
        _bar.clear()
        _buzzWithVoid.clear()
    }
}
