package mock.template.mixedannotation

import kotlin.Boolean
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Common {
    public val _doSomething: KMockContract.SyncFunProxy<Unit, () -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.mixedannotation.CommonMock#_doSomething",
            collector = collector, freeze = freeze)

    public override fun doSomething(): Unit = _doSomething.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock() {
        _doSomething.clear()
    }
}
