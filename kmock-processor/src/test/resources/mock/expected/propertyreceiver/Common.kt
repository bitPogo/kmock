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
    public override var Something.thing: Int
        get() = _thingGetter.invoke(this@thing)
        set(`value`) {
            _thingSetter.invoke(this@thing, value)
        }

    public val _thingGetter:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.Something) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_thingGetter",
            collector = verifier, freeze = freeze)

    public val _thingSetter:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.Something) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_thingSetter",
            collector = verifier, freeze = freeze)

    public override val SomethingElse<Any>.things: List<Any>
        get() = _thingsGetter.invoke(this@things)

    public val _thingsGetter:
        KMockContract.SyncFunProxy<List<Any>, (mock.template.propertyreceiver.SomethingElse<kotlin.Any>) ->
        kotlin.collections.List<kotlin.Any>> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_thingsGetter",
            collector = verifier, freeze = freeze)

    public override var Common<*>.extension: Int
        get() = _extensionGetter.invoke(this@extension)
        set(`value`) {
            _extensionSetter.invoke(this@extension, value)
        }

    public val _extensionGetter:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.Common<*>) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_extensionGetter",
            collector = verifier, freeze = freeze)

    public val _extensionSetter:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.Common<*>) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_extensionSetter",
            collector = verifier, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override var <T> T.nothing: T where T : Something, T : Comparable<T>
        get() = _nothingGetterWithTSomethingComparable.invoke(this@nothing) as T
        set(`value`) {
            _nothingSetterWithTSomethingComparable.invoke(this@nothing, value)
        }

    public val _nothingGetterWithTSomethingComparable: KMockContract.SyncFunProxy<Any, (kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_nothingGetterWithTSomethingComparable",
            collector = verifier, freeze = freeze)

    public val _nothingSetterWithTSomethingComparable: KMockContract.SyncFunProxy<Any, (kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_nothingSetterWithTSomethingComparable",
            collector = verifier, freeze = freeze)

    public override var <T : L> T.nothing: L
        get() = _nothingGetterWithTL.invoke(this@nothing)
        set(`value`) {
            _nothingSetterWithTL.invoke(this@nothing, value)
        }

    public val _nothingGetterWithTL: KMockContract.SyncFunProxy<L, (L) -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_nothingGetterWithTL",
            collector = verifier, freeze = freeze)

    public val _nothingSetterWithTL: KMockContract.SyncFunProxy<L, (L) -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_nothingSetterWithTL",
            collector = verifier, freeze = freeze)

    public override var L.otherThing: String
        get() = _otherThingGetter.invoke(this@otherThing)
        set(`value`) {
            _otherThingSetter.invoke(this@otherThing, value)
        }

    public val _otherThingGetter: KMockContract.SyncFunProxy<String, (L) -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_otherThingGetter",
            collector = verifier, freeze = freeze)

    public val _otherThingSetter: KMockContract.SyncFunProxy<String, (L) -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_otherThingSetter",
            collector = verifier, freeze = freeze)

    public override val myThing: String
        get() = _myThing.onGet()

    public val _myThing: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.propertyreceiver.CommonMock#_myThing",
            collector = verifier, freeze = freeze)

    public override val AnythingElse.SomethingInside.inside: Int
        get() = _insideGetter.invoke(this@inside)

    public val _insideGetter:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.AnythingElse.SomethingInside) ->
        kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_insideGetter",
            collector = verifier, freeze = freeze)

    public val _getOtherThing: KMockContract.SyncFunProxy<Unit, () -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.CommonMock#_getOtherThing",
            collector = verifier, freeze = freeze)

    public override fun getOtherThing(): Unit = _getOtherThing.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

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
    }
}