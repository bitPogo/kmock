package mock.template.relaxed

import kotlin.Any
import kotlin.Boolean
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import mock.template.relaxed.relaxed
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class PlatformMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Platform {
    public override val buzz: String
        get() = _buzz.onGet()

    public val _buzz: KMockContract.PropertyProxy<String> =
        PropertyProxy("mock.template.relaxed.PlatformMock#_buzz", spyOnGet = null, collector =
        verifier, freeze = freeze, relaxer = if (relaxed) { { mockId -> relaxed(mockId) } } else {
            null })

    public val _foo: KMockContract.SyncFunProxy<String, (kotlin.Any) -> kotlin.String> =
        SyncFunProxy("mock.template.relaxed.PlatformMock#_foo", spyOn = null, collector = verifier,
            freeze = freeze, relaxer = if (relaxed) { { mockId -> relaxed(mockId) } } else { null })

    public val _bar: KMockContract.AsyncFunProxy<String, suspend (kotlin.Any) -> kotlin.String> =
        AsyncFunProxy("mock.template.relaxed.PlatformMock#_bar", spyOn = null, collector = verifier,
            freeze = freeze, relaxer = if (relaxed) { { mockId -> relaxed(mockId) } } else { null })

    public val _buzzWithVoid: KMockContract.SyncFunProxy<Unit, () -> kotlin.Unit> =
        SyncFunProxy("mock.template.relaxed.PlatformMock#_buzzWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { { relaxVoidFunction() } } else
        { null }, relaxer = if (relaxed) { { mockId -> relaxed(mockId) } } else { null },
            buildInRelaxer = null)

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
