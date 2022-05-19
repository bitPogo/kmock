package mock.template.propertyreceiver

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

    public val _thingGetter:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.Something) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_thingGetter",
            collector = verifier, freeze = freeze)

    public val _thingSetter:
        KMockContract.SyncFunProxy<Unit, (mock.template.propertyreceiver.Something) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_thingSetter",
            collector = verifier, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override val SomethingElse<Any>.things: List<Any>
        get() = _thingsGetter.invoke(this@things) {
            useSpyIf(__spyOn) {    spyContext {
                this@things.things
            } as kotlin.collections.List<kotlin.Any> }
        }

    public val _thingsGetter:
        KMockContract.SyncFunProxy<List<Any>, (mock.template.propertyreceiver.SomethingElse<kotlin.Any>) ->
        kotlin.collections.List<kotlin.Any>> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_thingsGetter",
            collector = verifier, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override var Spied<*>.extension: Int
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

    public val _extensionGetter:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.Spied<*>) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_extensionGetter",
            collector = verifier, freeze = freeze)

    public val _extensionSetter:
        KMockContract.SyncFunProxy<Unit, (mock.template.propertyreceiver.Spied<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_extensionSetter",
            collector = verifier, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override var <T> T.nothing: T where T : Something, T : Comparable<T>
        get() = _nothingGetterWithTSomethingComparable.invoke(this@nothing) {
            useSpyIf(__spyOn) {    spyContext {
                this@nothing.nothing
            } as T }
        } as T
        set(`value`) {
            _nothingSetterWithTSomethingComparable.invoke(this@nothing, value) {
                useSpyIf(__spyOn) {    spyContext {
                    this@nothing.nothing = value
                    Unit
                } }
            }
        }

    public val _nothingGetterWithTSomethingComparable: KMockContract.SyncFunProxy<Any, (kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_nothingGetterWithTSomethingComparable",
            collector = verifier, freeze = freeze)

    public val _nothingSetterWithTSomethingComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_nothingSetterWithTSomethingComparable",
            collector = verifier, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override var <T : L> T.nothing: L
        get() = _nothingGetterWithTL.invoke(this@nothing) {
            useSpyIf(__spyOn) {    spyContext {
                this@nothing.nothing
            } as L }
        }
        set(`value`) {
            _nothingSetterWithTL.invoke(this@nothing, value) {
                useSpyIf(__spyOn) {    spyContext {
                    this@nothing.nothing = value
                    Unit
                } }
            }
        }

    public val _nothingGetterWithTL: KMockContract.SyncFunProxy<L, (L) -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_nothingGetterWithTL",
            collector = verifier, freeze = freeze)

    public val _nothingSetterWithTL: KMockContract.SyncFunProxy<Unit, (L) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_nothingSetterWithTL",
            collector = verifier, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override var L.otherThing: String
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

    public val _otherThingGetter: KMockContract.SyncFunProxy<String, (L) -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_otherThingGetter",
            collector = verifier, freeze = freeze)

    public val _otherThingSetter: KMockContract.SyncFunProxy<Unit, (L) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_otherThingSetter",
            collector = verifier, freeze = freeze)

    public override val myThing: String
        get() = _myThing.onGet {
            useSpyIf(__spyOn) { __spyOn!!.myThing }
        }

    public val _myThing: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.propertyreceiver.SpiedMock#_myThing",
            collector = verifier, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override val AnythingElse.SomethingInside.inside: Int
        get() = _insideGetter.invoke(this@inside) {
            useSpyIf(__spyOn) {    spyContext {
                this@inside.inside
            } as kotlin.Int }
        }

    public val _insideGetter:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.AnythingElse.SomethingInside) ->
        kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_insideGetter",
            collector = verifier, freeze = freeze)

    public val _getOtherThing: KMockContract.SyncFunProxy<Unit, () -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_getOtherThing",
            collector = verifier, freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_toString",
            collector = verifier, freeze = freeze, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_equals", collector
        = verifier, freeze = freeze, ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.SpiedMock#_hashCode",
            collector = verifier, freeze = freeze, ignorableForVerification = true)

    public override fun getOtherThing(): Unit = _getOtherThing.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.getOtherThing() }
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

    public fun spyContext(action: mock.template.propertyreceiver.Spied<L>.() -> Any?) =
        action(__spyOn!!)

    public fun _clearMock(): Unit {
        _thingGetter.clear()
        _thingSetter.clear()
        _thingsGetter.clear()
        _extensionGetter.clear()
        _extensionSetter.clear()
        _nothingGetterWithTSomethingComparable.clear()
        _nothingSetterWithTSomethingComparable.clear()
        _nothingGetterWithTL.clear()
        _nothingSetterWithTL.clear()
        _otherThingGetter.clear()
        _otherThingSetter.clear()
        _myThing.clear()
        _insideGetter.clear()
        _getOtherThing.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
