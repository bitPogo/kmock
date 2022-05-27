package mock.template.typealiaz

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class ReceiverMock<L : Alias11>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Receiver<L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Receiver<L> {
    @Suppress("UNCHECKED_CAST")
    public override var <T : Alias3<T>> T.member: T
        get() = _memberGetter.invoke(this@member) as T
        set(`value`) {
            _memberSetter.invoke(this@member, value)
        }

    public val _memberGetter:
        KMockContract.SyncFunProxy<mock.template.typealiaz.Generic<kotlin.Any?>, (mock.template.typealiaz.Generic<kotlin.Any?>) ->
        mock.template.typealiaz.Generic<kotlin.Any?>> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.ReceiverMock#_memberGetter",
            collector = collector, freeze = freeze)

    public val _memberSetter:
        KMockContract.SyncFunProxy<Unit, (mock.template.typealiaz.Generic<kotlin.Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.ReceiverMock#_memberSetter",
            collector = collector, freeze = freeze)

    public val _doSomethingReceiver: KMockContract.SyncFunProxy<Any, (mock.template.typealiaz.Alias11,
        mock.template.typealiaz.Alias3<kotlin.Any>, mock.template.typealiaz.Alias2,
        mock.template.typealiaz.Alias11) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.ReceiverMock#_doSomethingReceiver",
            collector = collector, freeze = freeze)

    public val _doAnythingElseReceiver:
        KMockContract.SyncFunProxy<Unit, (mock.template.typealiaz.Alias3<kotlin.Comparable<kotlin.Any?>>,
            mock.template.typealiaz.Alias11, mock.template.typealiaz.Alias2) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.ReceiverMock#_doAnythingElseReceiver",
            collector = collector, freeze = freeze)

    public val _doAnythingElse:
        KMockContract.SyncFunProxy<Unit, (mock.template.typealiaz.Alias3<mock.template.typealiaz.Alias3<mock.template.typealiaz.Alias11>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.ReceiverMock#_doAnythingElse",
            collector = collector, freeze = freeze)

    public val _doOtherThingReceiver:
        KMockContract.SyncFunProxy<Unit, (mock.template.typealiaz.Generic<kotlin.Function1<kotlin.Any,
            kotlin.Any>>,
            mock.template.typealiaz.Alias3<mock.template.typealiaz.Generic<kotlin.Function1<kotlin.Any,
                kotlin.Any>>>, kotlin.Comparable<kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.ReceiverMock#_doOtherThingReceiver",
            collector = collector, freeze = freeze)

    public val _doSomethingElseReceiverWithLAlias2TAlias11LAlias2:
        KMockContract.SyncFunProxy<Unit, (kotlin.Function1<kotlin.Any, kotlin.Any>,
            kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.ReceiverMock#_doSomethingElseReceiverWithLAlias2TAlias11LAlias2",
            collector = collector, freeze = freeze)

    public val _doSomethingElseReceiverWithTAlias3TAlias3:
        KMockContract.SyncFunProxy<Unit, (mock.template.typealiaz.Generic<kotlin.Any?>,
            mock.template.typealiaz.Generic<kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.ReceiverMock#_doSomethingElseReceiverWithTAlias3TAlias3",
            collector = collector, freeze = freeze)

    public val _run: KMockContract.SyncFunProxy<Alias11, (L) -> mock.template.typealiaz.Alias11> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.ReceiverMock#_run", collector =
        collector, freeze = freeze)

    public override fun Alias11.doSomething(
        arg0: Alias3<Any>,
        arg1: Alias2,
        arg2: Alias11,
    ): Any = _doSomethingReceiver.invoke(this@doSomething,arg0, arg1, arg2)

    public override fun <T : Comparable<T>> Alias3<T>.doAnythingElse(arg1: Alias11, arg2: Alias2):
        Unit = _doAnythingElseReceiver.invoke(this@doAnythingElse,arg1, arg2) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun doAnythingElse(arg1: Alias3<Alias3<Alias11>>): Unit =
        _doAnythingElse.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias3<Alias2>, X : Comparable<X>> T.doOtherThing(arg1: Alias3<T>,
        arg0: X): Unit = _doOtherThingReceiver.invoke(this@doOtherThing,arg1, arg0) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias11, L : Alias2> L.doSomethingElse(arg1: T, arg2: L): Unit =
        _doSomethingElseReceiverWithLAlias2TAlias11LAlias2.invoke(this@doSomethingElse,arg1, arg2) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias3<K>, K> T.doSomethingElse(arg1: T): Unit =
        _doSomethingElseReceiverWithTAlias3TAlias3.invoke(this@doSomethingElse,arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun run(arg: L): Alias11 = _run.invoke(arg)

    public fun _clearMock(): Unit {
        _memberGetter.clear()
        _memberSetter.clear()
        _doSomethingReceiver.clear()
        _doAnythingElseReceiver.clear()
        _doAnythingElse.clear()
        _doOtherThingReceiver.clear()
        _doSomethingElseReceiverWithLAlias2TAlias11LAlias2.clear()
        _doSomethingElseReceiverWithTAlias3TAlias3.clear()
        _run.clear()
    }
}
