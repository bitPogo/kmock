package mock.template.async

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SharedMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Shared? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Shared {
    public val _foo: KMockContract.AsyncFunProxy<Any, suspend (Int, Any) -> Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.async.SharedMock#_foo", collector = collector,
            freeze = freeze)

    public val _bar: KMockContract.AsyncFunProxy<Any, suspend (Int, Any) -> Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.async.SharedMock#_bar", collector = collector,
            freeze = freeze)

    public override suspend fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override suspend fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public fun _clearMock() {
        _foo.clear()
        _bar.clear()
    }
}
