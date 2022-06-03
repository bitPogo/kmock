package mock.template.typealiaz

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformMock<L : Alias23>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform<L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Platform<L> {
    public val _doSomething: KMockContract.SyncFunProxy<Any, (
        Alias77<Any>,
        Alias23,
        Alias21,
    ) -> Any> = ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_doSomething",
        collector = collector, freeze = freeze)

    public val _doAnythingElseWithAlias21Alias23: KMockContract.SyncFunProxy<Unit, (Alias21,
        Alias23) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_doAnythingElseWithAlias21Alias23",
            collector = collector, freeze = freeze)

    public val _doAnythingElseWithAlias77:
        KMockContract.SyncFunProxy<Unit, (Alias77<Alias77<Alias21>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_doAnythingElseWithAlias77",
            collector = collector, freeze = freeze)

    public val _doOtherThing:
        KMockContract.SyncFunProxy<Unit, (Alias77<mock.template.typealiaz.Generic<kotlin.Function1<kotlin.Any,
            kotlin.Unit>>>, kotlin.Comparable<kotlin.Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_doOtherThing",
            collector = collector, freeze = freeze)

    public val _doSomethingElseWithTAlias21LAlias23:
        KMockContract.SyncFunProxy<Unit, (kotlin.Function1<kotlin.Any, kotlin.Unit>,
            kotlin.Function1<kotlin.Any, kotlin.Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_doSomethingElseWithTAlias21LAlias23",
            collector = collector, freeze = freeze)

    public val _doSomethingElseWithTAlias77:
        KMockContract.SyncFunProxy<Unit, (mock.template.typealiaz.Generic<kotlin.Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_doSomethingElseWithTAlias77",
            collector = collector, freeze = freeze)

    public val _run: KMockContract.SyncFunProxy<Alias21, (L) -> Alias21> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_run", collector =
        collector, freeze = freeze)

    public override fun doSomething(
        arg0: Alias77<Any>,
        arg1: Alias23,
        arg2: Alias21,
    ): Any = _doSomething.invoke(arg0, arg1, arg2)

    public override fun doAnythingElse(arg1: Alias21, arg2: Alias23): Unit =
        _doAnythingElseWithAlias21Alias23.invoke(arg1, arg2) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun doAnythingElse(arg1: Alias77<Alias77<Alias21>>): Unit =
        _doAnythingElseWithAlias77.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias77<Alias21>, X : Comparable<X>> doOtherThing(arg1: Alias77<T>,
        arg0: X): Unit = _doOtherThing.invoke(arg1, arg0) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias21, L : Alias23> doSomethingElse(arg1: T, arg2: L): Unit =
        _doSomethingElseWithTAlias21LAlias23.invoke(arg1, arg2) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias77<K>, K> doSomethingElse(arg1: T): Unit =
        _doSomethingElseWithTAlias77.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun run(arg: L): Alias21 = _run.invoke(arg)

    public fun _clearMock(): Unit {
        _doSomething.clear()
        _doAnythingElseWithAlias21Alias23.clear()
        _doAnythingElseWithAlias77.clear()
        _doOtherThing.clear()
        _doSomethingElseWithTAlias21LAlias23.clear()
        _doSomethingElseWithTAlias77.clear()
        _run.clear()
    }
}
