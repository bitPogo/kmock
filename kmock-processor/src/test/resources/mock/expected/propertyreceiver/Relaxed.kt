package mock.template.propertyreceiver

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import mock.template.propertyreceiver.relaxed
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
    public override var Something.thing: Int
        get() = _thingGetter.invoke(this@thing) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
        }
        set(`value`) {
            _thingSetter.invoke(this@thing, value)
        }

    public val _thingGetter: KMockContract.SyncFunProxy<Int, (Something) -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_thingGetter",
            collector = collector, freeze = freeze)

    public val _thingSetter: KMockContract.SyncFunProxy<Unit, (Something) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_thingSetter",
            collector = collector, freeze = freeze)

    public override val SomethingElse<Any>.things: List<Any>
        get() = _thingsGetter.invoke(this@things) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
        }

    public val _thingsGetter: KMockContract.SyncFunProxy<List<Any>, (SomethingElse<Any>) -> List<Any>>
        = ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_thingsGetter",
        collector = collector, freeze = freeze)

    public override var Relaxed<*>.extension: Int
        get() = _extensionGetter.invoke(this@extension) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
        }
        set(`value`) {
            _extensionSetter.invoke(this@extension, value)
        }

    public val _extensionGetter: KMockContract.SyncFunProxy<Int, (Relaxed<*>) -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_extensionGetter",
            collector = collector, freeze = freeze)

    public val _extensionSetter: KMockContract.SyncFunProxy<Unit, (Relaxed<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_extensionSetter",
            collector = collector, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override var <T> T.nothing: T where T : Something, T : Comparable<T>
        get() = _nothingGetterWithTSomethingTComparable.invoke(this@nothing) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,
                type0 = mock.template.propertyreceiver.Something::class,
                type1 = kotlin.Comparable::class,) as T }
        } as T
        set(`value`) {
            _nothingSetterWithTSomethingTComparable.invoke(this@nothing, value)
        }

    public val _nothingGetterWithTSomethingTComparable: KMockContract.SyncFunProxy<Any, (Any) -> Any>
        =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_nothingGetterWithTSomethingTComparable",
            collector = collector, freeze = freeze)

    public val _nothingSetterWithTSomethingTComparable:
        KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_nothingSetterWithTSomethingTComparable",
            collector = collector, freeze = freeze)

    public override var <T : L> T.nothing: L
        get() = _nothingGetterWithTL.invoke(this@nothing) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,
                type0 = kotlin.Any::class,) as L }
        }
        set(`value`) {
            _nothingSetterWithTL.invoke(this@nothing, value)
        }

    public val _nothingGetterWithTL: KMockContract.SyncFunProxy<L, (L) -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_nothingGetterWithTL",
            collector = collector, freeze = freeze)

    public val _nothingSetterWithTL: KMockContract.SyncFunProxy<Unit, (L) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_nothingSetterWithTL",
            collector = collector, freeze = freeze)

    public override var L.otherThing: String
        get() = _otherThingGetterWithL.invoke(this@otherThing) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
        }
        set(`value`) {
            _otherThingSetterWithL.invoke(this@otherThing, value)
        }

    public val _otherThingGetterWithL: KMockContract.SyncFunProxy<String, (L) -> String> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_otherThingGetterWithL",
            collector = collector, freeze = freeze)

    public val _otherThingSetterWithL: KMockContract.SyncFunProxy<Unit, (L) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_otherThingSetterWithL",
            collector = collector, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override var <T : L> T.otherThing: T
        get() = _otherThingGetterWithTL.invoke(this@otherThing) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,
                type0 = kotlin.Any::class,) as T }
        } as T
        set(`value`) {
            _otherThingSetterWithTL.invoke(this@otherThing, value)
        }

    public val _otherThingGetterWithTL: KMockContract.SyncFunProxy<L, (L) -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_otherThingGetterWithTL",
            collector = collector, freeze = freeze)

    public val _otherThingSetterWithTL: KMockContract.SyncFunProxy<Unit, (L) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_otherThingSetterWithTL",
            collector = collector, freeze = freeze)

    public override var L.nextThing: L
        get() = _nextThingGetter.invoke(this@nextThing) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,
                type0 = kotlin.Any::class,) as L }
        }
        set(`value`) {
            _nextThingSetter.invoke(this@nextThing, value)
        }

    public val _nextThingGetter: KMockContract.SyncFunProxy<L, (L) -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_nextThingGetter",
            collector = collector, freeze = freeze)

    public val _nextThingSetter: KMockContract.SyncFunProxy<Unit, (L) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_nextThingSetter",
            collector = collector, freeze = freeze)

    public override val myThing: String
        get() = _myThing.executeOnGet {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
        }

    public val _myThing: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.propertyreceiver.RelaxedMock#_myThing",
            collector = collector, freeze = freeze)

    public override val AnythingElse.SomethingInside.inside: Int
        get() = _insideGetter.invoke(this@inside) {
            useRelaxerIf(relaxed) { proxyId -> relaxed(proxyId,) }
        }

    public val _insideGetter: KMockContract.SyncFunProxy<Int, (AnythingElse.SomethingInside) -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_insideGetter",
            collector = collector, freeze = freeze)

    public val _getOtherThing: KMockContract.SyncFunProxy<Unit, () -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.RelaxedMock#_getOtherThing",
            collector = collector, freeze = freeze)

    public override fun getOtherThing(): Unit = _getOtherThing.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock() {
        _thingGetter.clear()
        _thingSetter.clear()
        _thingsGetter.clear()
        _extensionGetter.clear()
        _extensionSetter.clear()
        _nothingGetterWithTSomethingTComparable.clear()
        _nothingSetterWithTSomethingTComparable.clear()
        _nothingGetterWithTL.clear()
        _nothingSetterWithTL.clear()
        _otherThingGetterWithL.clear()
        _otherThingSetterWithL.clear()
        _otherThingGetterWithTL.clear()
        _otherThingSetterWithTL.clear()
        _nextThingGetter.clear()
        _nextThingSetter.clear()
        _myThing.clear()
        _insideGetter.clear()
        _getOtherThing.clear()
    }
}
