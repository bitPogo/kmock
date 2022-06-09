package mock.template.sync

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Int
import kotlin.IntArray
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Platform {
    public val _foo: KMockContract.SyncFunProxy<Any, (Int, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.sync.PlatformMock#_foo", collector = collector,
            freeze = freeze)

    public val _bar: KMockContract.SyncFunProxy<Any, (Int, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.sync.PlatformMock#_bar", collector = collector,
            freeze = freeze)

    public val _ozz: KMockContract.SyncFunProxy<Any, (IntArray) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.sync.PlatformMock#_ozz", collector = collector,
            freeze = freeze)

    public val _izz: KMockContract.SyncFunProxy<Any, (Array<out Any>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.sync.PlatformMock#_izz", collector = collector,
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
