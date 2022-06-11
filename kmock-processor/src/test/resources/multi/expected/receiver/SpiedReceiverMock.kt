package multi

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import multi.template.`receiver`.AnythingElse
import multi.template.`receiver`.Methods
import multi.template.`receiver`.Properties
import multi.template.`receiver`.Something
import multi.template.`receiver`.SomethingElse
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class ReceiverMultiMock<KMockTypeParameter0, KMockTypeParameter1, MultiMock>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: MultiMock? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Properties<KMockTypeParameter0>, Methods<KMockTypeParameter1> where MultiMock :
                                                                        Properties<KMockTypeParameter0>, MultiMock : Methods<KMockTypeParameter1> {
    private val __spyOn: MultiMock? = spyOn

    @Suppress("UNCHECKED_CAST")
    public override var Something.thing: Int
        get() = _thingGetter.invoke(this@thing) {
            useSpyIf(__spyOn) {    spyContext {
                this@thing.thing
            } as kotlin.Int }
        }
        set(`value`) {
            _thingSetter.invoke(this@thing, value) {
                useSpyIf(__spyOn) {    spyContext {
                    this@thing.thing = value
                    Unit
                } }
            }
        }

    public val _thingGetter: KMockContract.SyncFunProxy<Int, (Something) -> Int> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_thingGetter", collector = collector,
            freeze = freeze)

    public val _thingSetter: KMockContract.SyncFunProxy<Unit, (Something) -> Unit> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_thingSetter", collector = collector,
            freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override val SomethingElse<Any>.things: List<Any>
        get() = _thingsGetter.invoke(this@things) {
            useSpyIf(__spyOn) {    spyContext {
                this@things.things
            } as kotlin.collections.List<kotlin.Any> }
        }

    public val _thingsGetter: KMockContract.SyncFunProxy<List<Any>, (SomethingElse<Any>) -> List<Any>>
        = ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_thingsGetter", collector =
    collector, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override var Properties<*>.extension: Int
        get() = _extensionGetter.invoke(this@extension) {
            useSpyIf(__spyOn) {    spyContext {
                this@extension.extension
            } as kotlin.Int }
        }
        set(`value`) {
            _extensionSetter.invoke(this@extension, value) {
                useSpyIf(__spyOn) {    spyContext {
                    this@extension.extension = value
                    Unit
                } }
            }
        }

    public val _extensionGetter: KMockContract.SyncFunProxy<Int, (Properties<*>) -> Int> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_extensionGetter", collector =
        collector, freeze = freeze)

    public val _extensionSetter: KMockContract.SyncFunProxy<Unit, (Properties<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_extensionSetter", collector =
        collector, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override var <T> T.nothing: T where T : Something, T : Comparable<T>
        get() = _nothingGetterWithTSomethingTComparable.invoke(this@nothing) {
            useSpyIf(__spyOn) {    spyContext {
                this@nothing.nothing
            } as T }
        } as T
        set(`value`) {
            _nothingSetterWithTSomethingTComparable.invoke(this@nothing, value) {
                useSpyIf(__spyOn) {    spyContext {
                    this@nothing.nothing = value
                    Unit
                } }
            }
        }

    public val _nothingGetterWithTSomethingTComparable: KMockContract.SyncFunProxy<Any, (Any) -> Any>
        =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_nothingGetterWithTSomethingTComparable",
            collector = collector, freeze = freeze)

    public val _nothingSetterWithTSomethingTComparable:
        KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_nothingSetterWithTSomethingTComparable",
            collector = collector, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override var <T : KMockTypeParameter0> T.nothing: KMockTypeParameter0
        get() = _nothingGetterWithTKMockTypeParameter0.invoke(this@nothing) {
            useSpyIf(__spyOn) {    spyContext {
                this@nothing.nothing
            } as KMockTypeParameter0 }
        }
        set(`value`) {
            _nothingSetterWithTKMockTypeParameter0.invoke(this@nothing, value) {
                useSpyIf(__spyOn) {    spyContext {
                    this@nothing.nothing = value
                    Unit
                } }
            }
        }

    public val _nothingGetterWithTKMockTypeParameter0:
        KMockContract.SyncFunProxy<KMockTypeParameter0, (KMockTypeParameter0) -> KMockTypeParameter0>
        =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_nothingGetterWithTKMockTypeParameter0",
            collector = collector, freeze = freeze)

    public val _nothingSetterWithTKMockTypeParameter0:
        KMockContract.SyncFunProxy<Unit, (KMockTypeParameter0) -> Unit> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_nothingSetterWithTKMockTypeParameter0",
            collector = collector, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override var KMockTypeParameter0.otherThing: String
        get() = _otherThingGetter.invoke(this@otherThing) {
            useSpyIf(__spyOn) {    spyContext {
                this@otherThing.otherThing
            } as kotlin.String }
        }
        set(`value`) {
            _otherThingSetter.invoke(this@otherThing, value) {
                useSpyIf(__spyOn) {    spyContext {
                    this@otherThing.otherThing = value
                    Unit
                } }
            }
        }

    public val _otherThingGetter: KMockContract.SyncFunProxy<String, (KMockTypeParameter0) -> String>
        = ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_otherThingGetter", collector =
    collector, freeze = freeze)

    public val _otherThingSetter: KMockContract.SyncFunProxy<Unit, (KMockTypeParameter0) -> Unit> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_otherThingSetter", collector =
        collector, freeze = freeze)

    public override val myThing: String
        get() = _myThing.executeOnGet {
            useSpyIf(__spyOn) { __spyOn!!.myThing }
        }

    public val _myThing: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("multi.ReceiverMultiMock#_myThing", collector = collector,
            freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override val AnythingElse.SomethingInside.inside: Int
        get() = _insideGetter.invoke(this@inside) {
            useSpyIf(__spyOn) {    spyContext {
                this@inside.inside
            } as kotlin.Int }
        }

    public val _insideGetter: KMockContract.SyncFunProxy<Int, (AnythingElse.SomethingInside) -> Int> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_insideGetter", collector =
        collector, freeze = freeze)

    public val _getOtherThing: KMockContract.SyncFunProxy<Unit, () -> Unit> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_getOtherThing", collector =
        collector, freeze = freeze)

    public val _equalsReceiver: KMockContract.SyncFunProxy<Int, (Something) -> Int> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_equalsReceiver", collector =
        collector, freeze = freeze)

    public val _doSomethingReceiver: KMockContract.SyncFunProxy<Int, (Something) -> Int> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_doSomethingReceiver", collector =
        collector, freeze = freeze)

    public val _doSomethingElseReceiverWithSomethingElse:
        KMockContract.SyncFunProxy<List<Any>, (SomethingElse<Any>) -> List<Any>> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_doSomethingElseReceiverWithSomethingElse",
            collector = collector, freeze = freeze)

    public val _doSomethingElseReceiverWithZTAny:
        KMockContract.SyncFunProxy<KMockTypeParameter1, (Any?) -> KMockTypeParameter1> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_doSomethingElseReceiverWithZTAny",
            collector = collector, freeze = freeze)

    public val _doSomethingElse: KMockContract.SyncFunProxy<Unit, (SomethingElse<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_doSomethingElse", collector =
        collector, freeze = freeze)

    public val _mutaborReceiver: KMockContract.SyncFunProxy<Int, (Methods<*>) -> Int> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_mutaborReceiver", collector =
        collector, freeze = freeze)

    public val _mutabor: KMockContract.SyncFunProxy<Unit, (Methods<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_mutabor", collector = collector,
            freeze = freeze)

    public val _doNothingReceiverWithTSomethingTComparable:
        KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_doNothingReceiverWithTSomethingTComparable",
            collector = collector, freeze = freeze)

    public val _doNothingReceiverWithTAny: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_doNothingReceiverWithTAny",
            collector = collector, freeze = freeze)

    public val _doNothingElseReceiver: KMockContract.SyncFunProxy<Unit, (Any, Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_doNothingElseReceiver", collector =
        collector, freeze = freeze)

    public val _doNothingElse: KMockContract.SyncFunProxy<Any, (Any) -> Any> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_doNothingElse", collector =
        collector, freeze = freeze)

    public val _doInsideReceiver:
        KMockContract.SyncFunProxy<Int, (AnythingElse.SomethingInside) -> Int> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_doInsideReceiver", collector =
        collector, freeze = freeze)

    public val _iDo: KMockContract.SyncFunProxy<Unit, () -> Unit> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_iDo", collector = collector, freeze
        = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> String> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_toString", collector = collector,
            freeze = freeze, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (Any?) -> Boolean> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_equals", collector = collector,
            freeze = freeze, ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> Int> =
        ProxyFactory.createSyncFunProxy("multi.ReceiverMultiMock#_hashCode", collector = collector,
            freeze = freeze, ignorableForVerification = true)

    public override fun getOtherThing(): Unit = _getOtherThing.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.getOtherThing() }
    }

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
    public override fun <T> T.doSomethingElse(): KMockTypeParameter1 =
        _doSomethingElseReceiverWithZTAny.invoke(this@doSomethingElse,) {
            useSpyIf(__spyOn) {    spyContext {
                this@doSomethingElse.doSomethingElse()
            } as KMockTypeParameter1 }
        }

    public override fun doSomethingElse(x: SomethingElse<Any>): Unit = _doSomethingElse.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.doSomethingElse(x) }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun Methods<*>.mutabor(): Int = _mutaborReceiver.invoke(this@mutabor,) {
        useSpyIf(__spyOn) {    spyContext {
            this@mutabor.mutabor()
        } as kotlin.Int }
    }

    public override fun mutabor(x: Methods<*>): Unit = _mutabor.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.mutabor(x) }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T> =
        _doNothingReceiverWithTSomethingTComparable.invoke(this@doNothing,) {
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
            mockKlass = ReceiverMultiMock::class
        )
    }

    public override fun hashCode(): Int = _hashCode.invoke() {
        useRelaxerIf(true) { super.hashCode() }
        useSpyIf(__spyOn) { __spyOn!!.hashCode() }
    }

    public fun spyContext(action: MultiMock.() -> Any?) = action(__spyOn!!)

    public fun _clearMock(): Unit {
        _thingGetter.clear()
        _thingSetter.clear()
        _thingsGetter.clear()
        _extensionGetter.clear()
        _extensionSetter.clear()
        _nothingGetterWithTSomethingTComparable.clear()
        _nothingSetterWithTSomethingTComparable.clear()
        _nothingGetterWithTKMockTypeParameter0.clear()
        _nothingSetterWithTKMockTypeParameter0.clear()
        _otherThingGetter.clear()
        _otherThingSetter.clear()
        _myThing.clear()
        _insideGetter.clear()
        _getOtherThing.clear()
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
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
