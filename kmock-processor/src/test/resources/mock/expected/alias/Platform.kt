package mock.template.alias

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class AliasPlatformMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: Platform? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Platform {
    public val _foo: KMockContract.AsyncFunProxy<Any, suspend (kotlin.Int, kotlin.Any) -> kotlin.Any>
        = AsyncFunProxy("mock.template.alias.AliasPlatformMock#_foo", spyOn = null, collector =
    verifier, freeze = freeze, relaxer = null)

    public val _bar: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunProxy("mock.template.alias.AliasPlatformMock#_bar", spyOn = null, collector = verifier,
            freeze = freeze, relaxer = null)

    public override suspend fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public fun _clearMock(): Unit {
        _foo.clear()
        _bar.clear()
    }
}
