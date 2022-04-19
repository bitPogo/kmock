package mock.template.customshared

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SharedMock(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Shared? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Shared {
    public val _foo: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.customshared.SharedMock#_foo", collector =
        verifier, freeze = freeze)

    public val _bar: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.customshared.SharedMock#_bar", collector =
        verifier, freeze = freeze)

    public override fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public fun _clearMock(): Unit {
        _foo.clear()
        _bar.clear()
    }
}
