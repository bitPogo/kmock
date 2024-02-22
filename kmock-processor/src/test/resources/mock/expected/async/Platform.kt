package mock.template.async

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Int
import kotlin.IntArray
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformMock(
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
        ProxyFactory.createAsyncFunProxy("mock.template.async.PlatformMock#_foo", collector =
        collector, freeze = freeze)

    public val _bar: KMockContract.AsyncFunProxy<Any, suspend (Int, Any) -> Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.async.PlatformMock#_bar", collector =
        collector, freeze = freeze)

    public val _ozz: KMockContract.AsyncFunProxy<Any, suspend (IntArray) -> Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.async.PlatformMock#_ozz", collector =
        collector, freeze = freeze)

    public val _izz: KMockContract.AsyncFunProxy<Any, suspend (Array<out Any>) -> Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.async.PlatformMock#_izz", collector =
        collector, freeze = freeze)

    public override suspend fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override suspend fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public override suspend fun ozz(vararg buzz: Int): Any = _ozz.invoke(buzz)

    public override suspend fun izz(vararg buzz: Any): Any = _izz.invoke(buzz)

    public fun _clearMock() {
        _foo.clear()
        _bar.clear()
        _ozz.clear()
        _izz.clear()
    }
}
