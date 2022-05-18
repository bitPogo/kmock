package mock.template.methodreceiver

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonMock<L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common<L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Common<L> {
    public val _equalsReceiver:
        KMockContract.SyncFunProxy<Int, (mock.template.methodreceiver.Something) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.CommonMock#_equalsReceiver",
            collector = verifier, freeze = freeze)

    public val _doSomethingReceiver:
        KMockContract.SyncFunProxy<Int, (mock.template.methodreceiver.Something) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.CommonMock#_doSomethingReceiver",
            collector = verifier, freeze = freeze)

    public val _doSomethingElseReceiver:
        KMockContract.SyncFunProxy<List<Any>, (mock.template.methodreceiver.SomethingElse<kotlin.Any>) ->
        kotlin.collections.List<kotlin.Any>> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.CommonMock#_doSomethingElseReceiver",
            collector = verifier, freeze = freeze)

    public val _doSomethingElse:
        KMockContract.SyncFunProxy<Unit, (mock.template.methodreceiver.SomethingElse<kotlin.Any>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.CommonMock#_doSomethingElse",
            collector = verifier, freeze = freeze)

    public val _mutaborReceiver:
        KMockContract.SyncFunProxy<Int, (mock.template.methodreceiver.Common<*>) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.CommonMock#_mutaborReceiver",
            collector = verifier, freeze = freeze)

    public val _mutabor: KMockContract.SyncFunProxy<Unit, (mock.template.methodreceiver.Common<*>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.CommonMock#_mutabor", collector
        = verifier, freeze = freeze)

    public val _doNothingReceiverWithTSomethingComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.CommonMock#_doNothingReceiverWithTSomethingComparable",
            collector = verifier, freeze = freeze)

    public val _doNothingReceiverWithTAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.CommonMock#_doNothingReceiverWithTAny",
            collector = verifier, freeze = freeze)

    public val _doNothingElseReceiver: KMockContract.SyncFunProxy<Unit, (kotlin.Any, kotlin.Any) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.CommonMock#_doNothingElseReceiver",
            collector = verifier, freeze = freeze)

    public val _doNothingElse: KMockContract.SyncFunProxy<Any, (kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.CommonMock#_doNothingElse",
            collector = verifier, freeze = freeze)

    public val _doInsideReceiver:
        KMockContract.SyncFunProxy<Int, (mock.template.methodreceiver.AnythingElse.SomethingInside) ->
        kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.CommonMock#_doInsideReceiver",
            collector = verifier, freeze = freeze)

    public val _iDo: KMockContract.SyncFunProxy<Unit, () -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.CommonMock#_iDo", collector =
        verifier, freeze = freeze)

    public override fun Something.equals(): Int = _equalsReceiver.invoke(this@equals,)

    public override fun Something.doSomething(): Int = _doSomethingReceiver.invoke(this@doSomething,)

    public override fun SomethingElse<Any>.doSomethingElse(): List<Any> =
        _doSomethingElseReceiver.invoke(this@doSomethingElse,)

    public override fun doSomethingElse(x: SomethingElse<Any>): Unit = _doSomethingElse.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun Common<*>.mutabor(): Int = _mutaborReceiver.invoke(this@mutabor,)

    public override fun mutabor(x: Common<*>): Unit = _mutabor.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T> =
        _doNothingReceiverWithTSomethingComparable.invoke(this@doNothing,)

    public override fun <T : Any> T.doNothing(): Unit =
        _doNothingReceiverWithTAny.invoke(this@doNothing,)

    public override fun <T, R : Any> T.doNothingElse(a: R): Unit where T : Something, T :
    Comparable<T> = _doNothingElseReceiver.invoke(this@doNothingElse,a)

    public override fun doNothingElse(a: Any): Any = _doNothingElse.invoke(a)

    public override fun AnythingElse.SomethingInside.doInside(): Int =
        _doInsideReceiver.invoke(this@doInside,)

    public override fun iDo(): Unit = _iDo.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _equalsReceiver.clear()
        _doSomethingReceiver.clear()
        _doSomethingElseReceiver.clear()
        _doSomethingElse.clear()
        _mutaborReceiver.clear()
        _mutabor.clear()
        _doNothingReceiverWithTSomethingComparable.clear()
        _doNothingReceiverWithTAny.clear()
        _doNothingElseReceiver.clear()
        _doNothingElse.clear()
        _doInsideReceiver.clear()
        _iDo.clear()
    }
}
