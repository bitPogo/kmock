package mock.template.methodreceiver

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class InheritedMock<P>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Inherited<P>? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Inherited<P> {
    public val _equalsReceiver: KMockContract.SyncFunProxy<Int, (Something) -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.InheritedMock#_equalsReceiver",
            collector = collector, freeze = freeze)

    public val _doSomethingReceiver: KMockContract.SyncFunProxy<Int, (Something) -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.InheritedMock#_doSomethingReceiver",
            collector = collector, freeze = freeze)

    public val _doSomethingElseReceiverWithSomethingElse:
        KMockContract.SyncFunProxy<List<Any>, (SomethingElse<Any>) -> List<Any>> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.InheritedMock#_doSomethingElseReceiverWithSomethingElse",
            collector = collector, freeze = freeze)

    public val _doSomethingElseReceiverWithZTAny: KMockContract.SyncFunProxy<P, (Any?) -> P> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.InheritedMock#_doSomethingElseReceiverWithZTAny",
            collector = collector, freeze = freeze)

    public val _doSomethingElse: KMockContract.SyncFunProxy<Unit, (SomethingElse<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.InheritedMock#_doSomethingElse",
            collector = collector, freeze = freeze)

    public val _mutaborReceiver: KMockContract.SyncFunProxy<Int, (Platform<*>) -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.InheritedMock#_mutaborReceiver",
            collector = collector, freeze = freeze)

    public val _mutabor: KMockContract.SyncFunProxy<Unit, (Platform<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.InheritedMock#_mutabor",
            collector = collector, freeze = freeze)

    public val _doNothingReceiverWithTSomethingTComparable:
        KMockContract.AsyncFunProxy<Unit, suspend (Any) -> Unit> =
        ProxyFactory.createAsyncFunProxy("mock.template.methodreceiver.InheritedMock#_doNothingReceiverWithTSomethingTComparable",
            collector = collector, freeze = freeze)

    public val _doNothingReceiverWithTAny: KMockContract.AsyncFunProxy<Unit, suspend (Any) -> Unit> =
        ProxyFactory.createAsyncFunProxy("mock.template.methodreceiver.InheritedMock#_doNothingReceiverWithTAny",
            collector = collector, freeze = freeze)

    public val _doNothingElseReceiver: KMockContract.SyncFunProxy<Unit, (Any, Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.InheritedMock#_doNothingElseReceiver",
            collector = collector, freeze = freeze)

    public val _doNothingElse: KMockContract.SyncFunProxy<Any, (Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.InheritedMock#_doNothingElse",
            collector = collector, freeze = freeze)

    public val _doInsideReceiver:
        KMockContract.SyncFunProxy<Int, (AnythingElse.SomethingInside) -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.InheritedMock#_doInsideReceiver",
            collector = collector, freeze = freeze)

    public val _iDo: KMockContract.SyncFunProxy<Unit, () -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.InheritedMock#_iDo", collector =
        collector, freeze = freeze)

    public override fun Something.equals(): Int = _equalsReceiver.invoke(this@equals,)

    public override fun Something.doSomething(): Int = _doSomethingReceiver.invoke(this@doSomething,)

    public override fun SomethingElse<Any>.doSomethingElse(): List<Any> =
        _doSomethingElseReceiverWithSomethingElse.invoke(this@doSomethingElse,)

    public override fun <T> T.doSomethingElse(): P =
        _doSomethingElseReceiverWithZTAny.invoke(this@doSomethingElse,)

    public override fun doSomethingElse(x: SomethingElse<Any>): Unit = _doSomethingElse.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun Platform<*>.mutabor(): Int = _mutaborReceiver.invoke(this@mutabor,)

    public override fun mutabor(x: Platform<*>): Unit = _mutabor.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override suspend fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T> =
        _doNothingReceiverWithTSomethingTComparable.invoke(this@doNothing,) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override suspend fun <T : Any> T.doNothing(): Unit =
        _doNothingReceiverWithTAny.invoke(this@doNothing,) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T, R : Any> T.doNothingElse(a: R): Unit where T : Something, T :
    Comparable<T> = _doNothingElseReceiver.invoke(this@doNothingElse,a) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun doNothingElse(a: Any): Any = _doNothingElse.invoke(a)

    public override fun AnythingElse.SomethingInside.doInside(): Int =
        _doInsideReceiver.invoke(this@doInside,)

    public override fun iDo(): Unit = _iDo.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
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
