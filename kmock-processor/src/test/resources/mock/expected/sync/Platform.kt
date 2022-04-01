package mock.template.sync

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformMock(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Platform {
    public val _foo: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.sync.PlatformMock#_foo", collector = verifier,
            freeze = freeze)

    public val _bar: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.sync.PlatformMock#_bar", collector = verifier,
            freeze = freeze)

    public val _ozz: KMockContract.SyncFunProxy<Any, (kotlin.IntArray) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.sync.PlatformMock#_ozz", collector = verifier,
            freeze = freeze)

    public val _izz: KMockContract.SyncFunProxy<Any, (Array<out kotlin.Any>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.sync.PlatformMock#_izz", collector = verifier,
            freeze = freeze)

    public override fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public override fun ozz(vararg buzz: Int): Any = _ozz.invoke(buzz)

    public override fun izz(vararg buzz: Any): Any = _izz.invoke(buzz)

    public fun _clearMock(): Unit {
        _foo.clear()
        _bar.clear()
        _ozz.clear()
        _izz.clear()
    }
}
