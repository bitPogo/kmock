package mock.template.methodreceiver

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import mock.template.methodreceiver.relaxed
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class RelaxedMock<L>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Relaxed<L>? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Relaxed<L> {
    public val _equalsReceiver: KMockContract.SyncFunProxy<Int, (Something) -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_equalsReceiver",
            collector = collector, freeze = freeze)

    public val _doSomethingReceiver: KMockContract.SyncFunProxy<Int, (Something) -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_doSomethingReceiver",
            collector = collector, freeze = freeze)

    public val _doSomethingElseReceiverWithSomethingElse:
        KMockContract.SyncFunProxy<List<Any>, (SomethingElse<Any>) -> List<Any>> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_doSomethingElseReceiverWithSomethingElse",
            collector = collector, freeze = freeze)

    public val _doSomethingElseReceiverWithZTAny: KMockContract.SyncFunProxy<L, (Any?) -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_doSomethingElseReceiverWithZTAny",
            collector = collector, freeze = freeze)

    public val _doSomethingElse: KMockContract.SyncFunProxy<Unit, (SomethingElse<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_doSomethingElse",
            collector = collector, freeze = freeze)

    public val _mutaborReceiver: KMockContract.SyncFunProxy<Int, (Relaxed<*>) -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_mutaborReceiver",
            collector = collector, freeze = freeze)

    public val _mutabor: KMockContract.SyncFunProxy<Unit, (Relaxed<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_mutabor", collector
        = collector, freeze = freeze)

    public val _doNothingReceiverWithTSomethingTComparable:
        KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_doNothingReceiverWithTSomethingTComparable",
            collector = collector, freeze = freeze)

    public val _doNothingReceiverWithTAny: KMockContract.SyncFunProxy<Any, (Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_doNothingReceiverWithTAny",
            collector = collector, freeze = freeze)

    public val _doNothingElseReceiver: KMockContract.SyncFunProxy<Unit, (Any, Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_doNothingElseReceiver",
            collector = collector, freeze = freeze)

    public val _doNothingElse: KMockContract.SyncFunProxy<Any, (Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_doNothingElse",
            collector = collector, freeze = freeze)

    public val _doInsideReceiver:
        KMockContract.SyncFunProxy<Int, (AnythingElse.SomethingInside) -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_doInsideReceiver",
            collector = collector, freeze = freeze)

    public val _iDo: KMockContract.SyncFunProxy<Unit, () -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.RelaxedMock#_iDo", collector =
        collector, freeze = freeze)

    public override fun Something.equals(): Int = _equalsReceiver.invoke(this@equals,) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
    }

    public override fun Something.doSomething(): Int = _doSomethingReceiver.invoke(this@doSomething,)
    {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
    }

    public override fun SomethingElse<Any>.doSomethingElse(): List<Any> =
        _doSomethingElseReceiverWithSomethingElse.invoke(this@doSomethingElse,) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> T.doSomethingElse(): L =
        _doSomethingElseReceiverWithZTAny.invoke(this@doSomethingElse,) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,
                type0 = kotlin.Any::class,) as L }
        }

    public override fun doSomethingElse(x: SomethingElse<Any>): Unit = _doSomethingElse.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun Relaxed<*>.mutabor(): Int = _mutaborReceiver.invoke(this@mutabor,) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
    }

    public override fun mutabor(x: Relaxed<*>): Unit = _mutabor.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T> =
        _doNothingReceiverWithTSomethingTComparable.invoke(this@doNothing,) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Any> T.doNothing(): T =
        _doNothingReceiverWithTAny.invoke(this@doNothing,) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,
                type0 = kotlin.Any::class,) as T }
        } as T

    public override fun <T, R : Any> T.doNothingElse(a: R): Unit where T : Something, T :
    Comparable<T> = _doNothingElseReceiver.invoke(this@doNothingElse,a) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun doNothingElse(a: Any): Any = _doNothingElse.invoke(a) {
        useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
    }

    public override fun AnythingElse.SomethingInside.doInside(): Int =
        _doInsideReceiver.invoke(this@doInside,) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
        }

    public override fun iDo(): Unit = _iDo.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock() {
        _equalsReceiver.clear()
        _doSomethingReceiver.clear()
        _doSomethingElseReceiverWithSomethingElse.clear()
        _doSomethingElseReceiverWithZTAny.clear()
        _doSomethingElse.clear()
        _mutaborReceiver.clear()
        _mutabor.clear()
        _doNothingReceiverWithTSomethingTComparable.clear()
        _doNothingReceiverWithTAny.clear()
        _doNothingElseReceiver.clear()
        _doNothingElse.clear()
        _doInsideReceiver.clear()
        _iDo.clear()
    }
}
