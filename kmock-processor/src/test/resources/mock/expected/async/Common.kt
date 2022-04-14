package mock.template.async

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonMock(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Common {
    public val _foo: KMockContract.AsyncFunProxy<Any, suspend (kotlin.Int, kotlin.Any) -> kotlin.Any>
        = ProxyFactory.createAsyncFunProxy("mock.template.async.CommonMock#_foo", collector =
    verifier, freeze = freeze)

    public val _bar: KMockContract.AsyncFunProxy<Any, suspend (kotlin.Int, kotlin.Any) -> kotlin.Any>
        = ProxyFactory.createAsyncFunProxy("mock.template.async.CommonMock#_bar", collector =
    verifier, freeze = freeze)

    public val _ozz: KMockContract.AsyncFunProxy<Any, suspend (kotlin.IntArray) -> kotlin.Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.async.CommonMock#_ozz", collector = verifier,
            freeze = freeze)

    public val _izz: KMockContract.AsyncFunProxy<Any, suspend (Array<out kotlin.Any>) -> kotlin.Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.async.CommonMock#_izz", collector = verifier,
            freeze = freeze)

    public override suspend fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override suspend fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public override suspend fun ozz(vararg buzz: Int): Any = _ozz.invoke(buzz)

    public override suspend fun izz(vararg buzz: Any): Any = _izz.invoke(buzz)

    public fun _clearMock(): Unit {
        _foo.clear()
        _bar.clear()
        _ozz.clear()
        _izz.clear()
    }
}
