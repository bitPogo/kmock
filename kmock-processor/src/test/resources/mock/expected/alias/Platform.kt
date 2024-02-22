package mock.template.alias

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class AliasPlatformMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Platform {
    public val _foo: KMockContract.AsyncFunProxy<Any, suspend (Int, Any) -> Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.alias.AliasPlatformMock#_foo", collector =
        collector, freeze = freeze)

    public val _bar: KMockContract.SyncFunProxy<Any, (Int, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.alias.AliasPlatformMock#_bar", collector =
        collector, freeze = freeze)

    public override suspend fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public fun _clearMock() {
        _foo.clear()
        _bar.clear()
    }
}
