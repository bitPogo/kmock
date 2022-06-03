package mock.template.typealiaz

import kotlin.Any
import kotlin.Boolean
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SharedMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Shared? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Shared {
    public val _doSomething: KMockContract.SyncFunProxy<Any, (
        Any,
        Alias0,
        Alias1,
    ) -> Any> = ProxyFactory.createSyncFunProxy("mock.template.typealiaz.SharedMock#_doSomething",
        collector = collector, freeze = freeze)

    public override fun doSomething(
        arg0: Any,
        arg1: Alias0,
        arg2: Alias1,
    ): Any = _doSomething.invoke(arg0, arg1, arg2)

    public fun _clearMock(): Unit {
        _doSomething.clear()
    }
}
