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

internal class PlatformMock<L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform<L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Platform<L> {
    public override var Something.thing: Int
        get() = _getThing.invoke(this@thing)
        set(`value`) {
            _setThing.invoke(this@thing, value)
        }

    public val _getThing:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.Something) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_getThing",
            collector = verifier, freeze = freeze)

    public val _setThing:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.Something) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_setThing",
            collector = verifier, freeze = freeze)

    public override val SomethingElse<Any>.things: List<Any>
        get() = _getThings.invoke(this@things)

    public val _getThings:
        KMockContract.SyncFunProxy<List<Any>, (mock.template.propertyreceiver.SomethingElse<kotlin.Any>) ->
        kotlin.collections.List<kotlin.Any>> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_getThings",
            collector = verifier, freeze = freeze)

    public override var Platform<*>.extension: Int
        get() = _getExtension.invoke(this@extension)
        set(`value`) {
            _setExtension.invoke(this@extension, value)
        }

    public val _getExtension:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.Platform<*>) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_getExtension",
            collector = verifier, freeze = freeze)

    public val _setExtension:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.Platform<*>) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_setExtension",
            collector = verifier, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override var <T> T.nothing: T where T : Something, T : Comparable<T>
        get() = _getNothingWithTSomethingComparable.invoke(this@nothing) as T
        set(`value`) {
            _setNothingWithTSomethingComparable.invoke(this@nothing, value)
        }

    public val _getNothingWithTSomethingComparable: KMockContract.SyncFunProxy<Any, (kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_getNothingWithTSomethingComparable",
            collector = verifier, freeze = freeze)

    public val _setNothingWithTSomethingComparable: KMockContract.SyncFunProxy<Any, (kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_setNothingWithTSomethingComparable",
            collector = verifier, freeze = freeze)

    public override var <T : L> T.nothing: L
        get() = _getNothingWithTL.invoke(this@nothing)
        set(`value`) {
            _setNothingWithTL.invoke(this@nothing, value)
        }

    public val _getNothingWithTL: KMockContract.SyncFunProxy<L, (L) -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_getNothingWithTL",
            collector = verifier, freeze = freeze)

    public val _setNothingWithTL: KMockContract.SyncFunProxy<L, (L) -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_setNothingWithTL",
            collector = verifier, freeze = freeze)

    public override var L.otherThing: String
        get() = _getOtherThingWithLAny.invoke(this@otherThing)
        set(`value`) {
            _setOtherThing.invoke(this@otherThing, value)
        }

    public val _getOtherThingWithLAny: KMockContract.SyncFunProxy<String, (kotlin.Any?) ->
    kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_getOtherThingWithLAny",
            collector = verifier, freeze = freeze)

    public val _setOtherThing: KMockContract.SyncFunProxy<String, (kotlin.Any?) -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_setOtherThing",
            collector = verifier, freeze = freeze)

    public override val myThing: String
        get() = _myThing.onGet()

    public val _myThing: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.propertyreceiver.PlatformMock#_myThing",
            collector = verifier, freeze = freeze)

    public override val AnythingElse.SomethingInside.inside: Int
        get() = _getInside.invoke(this@inside)

    public val _getInside:
        KMockContract.SyncFunProxy<Int, (mock.template.propertyreceiver.AnythingElse.SomethingInside) ->
        kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_getInside",
            collector = verifier, freeze = freeze)

    public val _getOtherThingWithVoid: KMockContract.SyncFunProxy<Unit, () -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.propertyreceiver.PlatformMock#_getOtherThingWithVoid",
            collector = verifier, freeze = freeze)

    public override fun getOtherThing(): Unit = _getOtherThingWithVoid.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _getThing.clear()
        _setThing.clear()
        _getThings.clear()
        _getExtension.clear()
        _setExtension.clear()
        _getNothingWithTSomethingComparable.clear()
        _setNothingWithTSomethingComparable.clear()
        _getNothingWithTL.clear()
        _setNothingWithTL.clear()
        _getOtherThingWithLAny.clear()
        _setOtherThing.clear()
        _myThing.clear()
        _getInside.clear()
        _getOtherThingWithVoid.clear()
    }
}
