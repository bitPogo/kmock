package mock.template.relaxed

import kotlin.Any
import kotlin.Boolean
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import mock.template.relaxed.relaxed
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.AsyncFunProxy
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.SyncFunProxy
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class CommonMock(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Common {
    public override val buzz: String
        get() = _buzz.onGet()

    public val _buzz: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.relaxed.CommonMock#_buzz", spyOnGet = null,
            collector = verifier, freeze = freeze, relaxer = if (relaxed) { { mockId -> relaxed(mockId) }
            } else { null })

    public val _foo: KMockContract.SyncFunProxy<String, (kotlin.Any) -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.CommonMock#_foo", spyOn = null,
            collector = verifier, freeze = freeze, relaxer = if (relaxed) { { mockId -> relaxed(mockId) }
            } else { null })

    public val _bar: KMockContract.AsyncFunProxy<String, suspend (kotlin.Any) -> kotlin.String> =
        ProxyFactory.createAsyncFunProxy("mock.template.relaxed.CommonMock#_bar", spyOn = null,
            collector = verifier, freeze = freeze, relaxer = if (relaxed) { { mockId -> relaxed(mockId) }
            } else { null })

    public val _buzzWithVoid: KMockContract.SyncFunProxy<Unit, () -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.relaxed.CommonMock#_buzzWithVoid", spyOn =
        null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
            relaxVoidFunction() } } else { null }, relaxer = if (relaxed) { { mockId -> relaxed(mockId) }
        } else { null }, buildInRelaxer = null)

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
