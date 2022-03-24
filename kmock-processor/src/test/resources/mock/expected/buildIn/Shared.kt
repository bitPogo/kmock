package mock.template.buildIn

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class SharedMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: Shared? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Shared {
    public override val foo: String
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyProxy<String> =
        PropertyProxy("mock.template.buildIn.SharedMock#_foo", spyOnGet = null, collector = verifier,
            freeze = freeze, relaxer = null)

    public override val bar: Int
        get() = _bar.onGet()

    public val _bar: KMockContract.PropertyProxy<Int> =
        PropertyProxy("mock.template.buildIn.SharedMock#_bar", spyOnGet = null, collector = verifier,
            freeze = freeze, relaxer = null)

    public override var buzz: Any
        get() = _buzz.onGet()
        set(`value`) = _buzz.onSet(value)

    public val _buzz: KMockContract.PropertyProxy<Any> =
        PropertyProxy("mock.template.buildIn.SharedMock#_buzz", spyOnGet = null, spyOnSet = null,
            collector = verifier, freeze = freeze, relaxer = null)

    private val __spyOn: Shared? = spyOn

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        SyncFunProxy("mock.template.buildIn.SharedMock#_toString", spyOn = if (spyOn != null) { {
            __spyOn!!.toString() } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer
        = null, relaxer = null, buildInRelaxer = { super.toString() }, ignorableForVerification =
        true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        SyncFunProxy("mock.template.buildIn.SharedMock#_equals", spyOn = if (spyOn != null) { {
                other ->
            __spyOn!!.equals(other) } } else { null }, collector = verifier, freeze = freeze,
            unitFunRelaxer = null, relaxer = null, buildInRelaxer = { other -> super.equals(other) },
            ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        SyncFunProxy("mock.template.buildIn.SharedMock#_hashCode", spyOn = if (spyOn != null) { {
            __spyOn!!.hashCode() } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer
        = null, relaxer = null, buildInRelaxer = { super.hashCode() }, ignorableForVerification =
        true)

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean {
        return if(other is SharedMock && __spyOn != null) {
            super.equals(other)
        } else {
            _equals.invoke(other)
        }
    }

    public override fun hashCode(): Int = _hashCode.invoke()

    public fun _clearMock(): Unit {
        _foo.clear()
        _bar.clear()
        _buzz.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
