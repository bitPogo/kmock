package mock.template.mixedannotation

import kotlin.Boolean
import kotlin.Suppress
import kotlin.Unit
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
    public val _doSomething: KMockContract.SyncFunProxy<Unit, () -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.mixedannotation.PlatformMock#_doSomething",
            collector = collector, freeze = freeze)

    public override fun doSomething(): Unit = _doSomething.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock() {
        _doSomething.clear()
    }
}
