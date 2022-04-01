package mock.template.alias

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class AliasSharedMock(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Shared? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Shared {
    public val _foo: KMockContract.AsyncFunProxy<Any, suspend (kotlin.Int, kotlin.Any) -> kotlin.Any>
        = ProxyFactory.createAsyncFunProxy("mock.template.alias.AliasSharedMock#_foo", collector =
    verifier, freeze = freeze)

    public val _bar: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.alias.AliasSharedMock#_bar", collector =
        verifier, freeze = freeze)

    public override suspend fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public fun _clearMock(): Unit {
        _foo.clear()
        _bar.clear()
    }
}
