package mock.template.methodreceiver

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SpiedMock<L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Spied<L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Spied<L> {
    private val __spyOn: Spied<L>? = spyOn

    public val _equalsReceiver:
        KMockContract.SyncFunProxy<Int, (mock.template.methodreceiver.Something) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_equalsReceiver",
            collector = verifier, freeze = freeze)

    public val _doSomethingReceiver:
        KMockContract.SyncFunProxy<Int, (mock.template.methodreceiver.Something) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_doSomethingReceiver",
            collector = verifier, freeze = freeze)

    public val _doSomethingElseReceiverWithSomethingElse:
        KMockContract.SyncFunProxy<List<Any>, (mock.template.methodreceiver.SomethingElse<kotlin.Any>) ->
        kotlin.collections.List<kotlin.Any>> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_doSomethingElseReceiverWithSomethingElse",
            collector = verifier, freeze = freeze)

    public val _doSomethingElseReceiverWithZTAny: KMockContract.SyncFunProxy<L, (kotlin.Any?) -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_doSomethingElseReceiverWithZTAny",
            collector = verifier, freeze = freeze)

    public val _doSomethingElse:
        KMockContract.SyncFunProxy<Unit, (mock.template.methodreceiver.SomethingElse<kotlin.Any>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_doSomethingElse",
            collector = verifier, freeze = freeze)

    public val _mutaborReceiver:
        KMockContract.SyncFunProxy<Int, (mock.template.methodreceiver.Spied<*>) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_mutaborReceiver",
            collector = verifier, freeze = freeze)

    public val _mutabor: KMockContract.SyncFunProxy<Unit, (mock.template.methodreceiver.Spied<*>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_mutabor", collector =
        verifier, freeze = freeze)

    public val _doNothingReceiverWithTSomethingComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_doNothingReceiverWithTSomethingComparable",
            collector = verifier, freeze = freeze)

    public val _doNothingReceiverWithTAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_doNothingReceiverWithTAny",
            collector = verifier, freeze = freeze)

    public val _doNothingElseReceiver: KMockContract.SyncFunProxy<Unit, (kotlin.Any, kotlin.Any) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_doNothingElseReceiver",
            collector = verifier, freeze = freeze)

    public val _doNothingElse: KMockContract.SyncFunProxy<Any, (kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_doNothingElse",
            collector = verifier, freeze = freeze)

    public val _doInsideReceiver:
        KMockContract.SyncFunProxy<Int, (mock.template.methodreceiver.AnythingElse.SomethingInside) ->
        kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_doInsideReceiver",
            collector = verifier, freeze = freeze)

    public val _iDo: KMockContract.SyncFunProxy<Unit, () -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_iDo", collector =
        verifier, freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_toString", collector
        = verifier, freeze = freeze, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_equals", collector =
        verifier, freeze = freeze, ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.methodreceiver.SpiedMock#_hashCode", collector
        = verifier, freeze = freeze, ignorableForVerification = true)

    @Suppress("UNCHECKED_CAST")
    public override fun Something.equals(): Int = _equalsReceiver.invoke(this@equals,) {
        useSpyIf(__spyOn) {    spyContext {
            this@equals.equals()
        } as kotlin.Int }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun Something.doSomething(): Int = _doSomethingReceiver.invoke(this@doSomething,)
    {
        useSpyIf(__spyOn) {    spyContext {
            this@doSomething.doSomething()
        } as kotlin.Int }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun SomethingElse<Any>.doSomethingElse(): List<Any> =
        _doSomethingElseReceiverWithSomethingElse.invoke(this@doSomethingElse,) {
            useSpyIf(__spyOn) {    spyContext {
                this@doSomethingElse.doSomethingElse()
            } as kotlin.collections.List<kotlin.Any> }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> T.doSomethingElse(): L =
        _doSomethingElseReceiverWithZTAny.invoke(this@doSomethingElse,) {
            useSpyIf(__spyOn) {    spyContext {
                this@doSomethingElse.doSomethingElse()
            } as L }
        }

    public override fun doSomethingElse(x: SomethingElse<Any>): Unit = _doSomethingElse.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.doSomethingElse(x) }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun Spied<*>.mutabor(): Int = _mutaborReceiver.invoke(this@mutabor,) {
        useSpyIf(__spyOn) {    spyContext {
            this@mutabor.mutabor()
        } as kotlin.Int }
    }

    public override fun mutabor(x: Spied<*>): Unit = _mutabor.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.mutabor(x) }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T> =
        _doNothingReceiverWithTSomethingComparable.invoke(this@doNothing,) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) {    spyContext {
                this@doNothing.doNothing<T>()
            } as kotlin.Unit }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Any> T.doNothing(): Unit =
        _doNothingReceiverWithTAny.invoke(this@doNothing,) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) {    spyContext {
                this@doNothing.doNothing()
            } as kotlin.Unit }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T, R : Any> T.doNothingElse(a: R): Unit where T : Something, T :
    Comparable<T> = _doNothingElseReceiver.invoke(this@doNothingElse,a) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) {    spyContext {
            this@doNothingElse.doNothingElse<T, R>(a)
        } as kotlin.Unit }
    }

    public override fun doNothingElse(a: Any): Any = _doNothingElse.invoke(a) {
        useSpyIf(__spyOn) { __spyOn!!.doNothingElse(a) }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun AnythingElse.SomethingInside.doInside(): Int =
        _doInsideReceiver.invoke(this@doInside,) {
            useSpyIf(__spyOn) {    spyContext {
                this@doInside.doInside()
            } as kotlin.Int }
        }

    public override fun iDo(): Unit = _iDo.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.iDo() }
    }

    public override fun toString(): String = _toString.invoke() {
        useRelaxerIf(true) { super.toString() }
        useSpyIf(__spyOn) { __spyOn!!.toString() }
    }

    public override fun equals(other: Any?): Boolean = _equals.invoke(other) {
        useRelaxerIf(true) { super.equals(other) }
        useSpyOnEqualsIf(
            spyTarget = __spyOn,
            other = other,
            spyOn = { super.equals(other) },
            mockKlass = SpiedMock::class
        )
    }

    public override fun hashCode(): Int = _hashCode.invoke() {
        useRelaxerIf(true) { super.hashCode() }
        useSpyIf(__spyOn) { __spyOn!!.hashCode() }
    }

    public fun spyContext(action: mock.template.methodreceiver.Spied<L>.() -> Any?) =
        action(__spyOn!!)

    public fun _clearMock(): Unit {
        _equalsReceiver.clear()
        _doSomethingReceiver.clear()
        _doSomethingElseReceiverWithSomethingElse.clear()
        _doSomethingElseReceiverWithZTAny.clear()
        _doSomethingElse.clear()
        _mutaborReceiver.clear()
        _mutabor.clear()
        _doNothingReceiverWithTSomethingComparable.clear()
        _doNothingReceiverWithTAny.clear()
        _doNothingElseReceiver.clear()
        _doNothingElse.clear()
        _doInsideReceiver.clear()
        _iDo.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
