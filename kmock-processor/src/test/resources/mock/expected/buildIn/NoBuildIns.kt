package mock.template.buildIn

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class NoBuildInsMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: NoBuildIns? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : NoBuildIns {
    public val _equalsWithInt: KMockContract.SyncFunProxy<Unit, (Int) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.buildIn.NoBuildInsMock#_equalsWithInt",
            collector = collector, freeze = freeze)

    public val _toStringWithInt: KMockContract.SyncFunProxy<String, (Int) -> String> =
        ProxyFactory.createSyncFunProxy("mock.template.buildIn.NoBuildInsMock#_toStringWithInt",
            collector = collector, freeze = freeze)

    public val _hashCodeWithAny: KMockContract.SyncFunProxy<Int, (Any) -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.buildIn.NoBuildInsMock#_hashCodeWithAny",
            collector = collector, freeze = freeze)

    public override fun equals(other: Int): Unit = _equalsWithInt.invoke(other) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun toString(radix: Int): String = _toStringWithInt.invoke(radix)

    public override fun hashCode(base: Any): Int = _hashCodeWithAny.invoke(base)

    public fun _clearMock(): Unit {
        _equalsWithInt.clear()
        _toStringWithInt.clear()
        _hashCodeWithAny.clear()
    }
}
