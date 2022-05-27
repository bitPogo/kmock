package mock.template.typealiaz

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import kotlin.Unit
import mock.template.typealiaz.smooth
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class RelaxedMock<L : Alias102>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Relaxed<L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Relaxed<L> {
    public val _doSomething:
        KMockContract.SyncFunProxy<Any, (mock.template.typealiaz.Alias107<kotlin.Any>,
            mock.template.typealiaz.Alias102, mock.template.typealiaz.Alias101) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.RelaxedMock#_doSomething", collector
        = collector, freeze = freeze)

    public val _doAnythingElseWithAlias101Alias102:
        KMockContract.SyncFunProxy<Unit, (mock.template.typealiaz.Alias101,
            mock.template.typealiaz.Alias102) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.RelaxedMock#_doAnythingElseWithAlias101Alias102",
            collector = collector, freeze = freeze)

    public val _doAnythingElseWithAlias107:
        KMockContract.SyncFunProxy<Unit, (mock.template.typealiaz.Alias107<mock.template.typealiaz.Alias107<mock.template.typealiaz.Alias101>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.RelaxedMock#_doAnythingElseWithAlias107",
            collector = collector, freeze = freeze)

    public val _doOtherThing:
        KMockContract.SyncFunProxy<mock.template.typealiaz.Generic<kotlin.Function1<kotlin.Any,
            kotlin.Unit>>, (mock.template.typealiaz.Alias107<mock.template.typealiaz.Generic<kotlin.Function1<kotlin.Any,
            kotlin.Unit>>>, kotlin.Comparable<kotlin.Any?>) ->
        mock.template.typealiaz.Generic<kotlin.Function1<kotlin.Any, kotlin.Unit>>> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.RelaxedMock#_doOtherThing", collector
        = collector, freeze = freeze)

    public val _doSomethingElseWithTAlias101LAlias102:
        KMockContract.SyncFunProxy<kotlin.Function1<kotlin.Any,
            kotlin.Any>, (kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any,
            kotlin.Any>) -> kotlin.Function1<kotlin.Any, kotlin.Any>> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.RelaxedMock#_doSomethingElseWithTAlias101LAlias102",
            collector = collector, freeze = freeze)

    public val _doSomethingElseWithTAlias107:
        KMockContract.SyncFunProxy<mock.template.typealiaz.Generic<kotlin.Any?>, (mock.template.typealiaz.Generic<kotlin.Any?>) ->
        mock.template.typealiaz.Generic<kotlin.Any?>> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.RelaxedMock#_doSomethingElseWithTAlias107",
            collector = collector, freeze = freeze)

    public val _run: KMockContract.SyncFunProxy<Alias101, (L) -> mock.template.typealiaz.Alias101> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.RelaxedMock#_run", collector =
        collector, freeze = freeze)

    public override fun doSomething(
        arg0: Alias107<Any>,
        arg1: Alias102,
        arg2: Alias101,
    ): Any = _doSomething.invoke(arg0, arg1, arg2) {
        useRelaxerIf(relaxed) { proxyId -> smooth(proxyId,) }
    }

    public override fun doAnythingElse(arg1: Alias101, arg2: Alias102): Unit =
        _doAnythingElseWithAlias101Alias102.invoke(arg1, arg2) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun doAnythingElse(arg1: Alias107<Alias107<Alias101>>): Unit =
        _doAnythingElseWithAlias107.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Alias107<Alias101>, X : Comparable<X>> doOtherThing(arg1: Alias107<T>,
        arg0: X): T = _doOtherThing.invoke(arg1, arg0) {
        useRelaxerIf(relaxed) { proxyId -> smooth(proxyId,
            type0 = mock.template.typealiaz.Generic::class,) as T }
    } as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Alias101, L : Alias102> doSomethingElse(arg1: T, arg2: L): L =
        _doSomethingElseWithTAlias101LAlias102.invoke(arg1, arg2) {
            useRelaxerIf(relaxed) { proxyId -> smooth(proxyId,
                type0 = kotlin.Function1::class,) as L }
        } as L

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Alias107<K>, K> doSomethingElse(arg1: T): T =
        _doSomethingElseWithTAlias107.invoke(arg1) {
            useRelaxerIf(relaxed) { proxyId -> smooth(proxyId,
                type0 = mock.template.typealiaz.Generic::class,) as T }
        } as T

    public override fun run(arg: L): Alias101 = _run.invoke(arg) {
        useRelaxerIf(relaxed) { proxyId -> smooth(proxyId,) }
    }

    public fun _clearMock(): Unit {
        _doSomething.clear()
        _doAnythingElseWithAlias101Alias102.clear()
        _doAnythingElseWithAlias107.clear()
        _doOtherThing.clear()
        _doSomethingElseWithTAlias101LAlias102.clear()
        _doSomethingElseWithTAlias107.clear()
        _run.clear()
    }
}
