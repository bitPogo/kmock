package mock.template.typealiaz

import kotlin.Any
import kotlin.Boolean
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
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false,
) : Common {
    public val _doSomething: KMockContract.SyncFunProxy<Any, (kotlin.Any,
        mock.template.typealiaz.Alias0, mock.template.typealiaz.Alias1) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.CommonMock#_doSomething", collector =
        verifier, freeze = freeze)

    public override fun doSomething(
        arg0: Any,
        arg1: Alias0,
        arg2: Alias1,
    ): Any = _doSomething.invoke(arg0, arg1, arg2)

    public fun _clearMock(): Unit {
        _doSomething.clear()
    }
}
