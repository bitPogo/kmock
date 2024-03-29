package mock.template.sync

import kotlin.Any
import kotlin.Boolean
import kotlin.Enum
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
    public val _foo: KMockContract.SyncFunProxy<Any, (Int, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.sync.SharedMock#_foo", collector = collector,
            freeze = freeze)

    public val _bar: KMockContract.SyncFunProxy<Any, (Int, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.sync.SharedMock#_bar", collector = collector,
            freeze = freeze)

    public val _nol: KMockContract.SyncFunProxy<Any, (Enum<*>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.sync.SharedMock#_nol", collector = collector,
            freeze = freeze)

    public override fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public override fun nol(buzz: Enum<*>): Any = _nol.invoke(buzz)

    public fun _clearMock() {
        _foo.clear()
        _bar.clear()
        _nol.clear()
    }
}
