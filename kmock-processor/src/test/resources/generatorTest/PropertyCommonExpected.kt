// COMMONTEST
package generatorTest

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

internal class PropertyCommonMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: PropertyCommon? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : PropertyCommon {
    public override val foo: String
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyProxy<String> = if (spyOn == null) {
        PropertyProxy("generatorTest.PropertyCommon#_foo", spyOnGet = null, collector = verifier,
            freeze = freeze, relaxer = null)
    } else {
        PropertyProxy("generatorTest.PropertyCommon#_foo", spyOnGet = { spyOn.foo }, collector =
        verifier, freeze = freeze, relaxer = null)
    }


    public override val bar: Int
        get() = _bar.onGet()

    public val _bar: KMockContract.PropertyProxy<Int> = if (spyOn == null) {
        PropertyProxy("generatorTest.PropertyCommon#_bar", spyOnGet = null, collector = verifier,
            freeze = freeze, relaxer = null)
    } else {
        PropertyProxy("generatorTest.PropertyCommon#_bar", spyOnGet = { spyOn.bar }, collector =
        verifier, freeze = freeze, relaxer = null)
    }


    public override var buzz: Any
        get() = _buzz.onGet()
        set(`value`) = _buzz.onSet(value)

    public val _buzz: KMockContract.PropertyProxy<Any> = if (spyOn == null) {
        PropertyProxy("generatorTest.PropertyCommon#_buzz", spyOnGet = null, spyOnSet = null,
            collector = verifier, freeze = freeze, relaxer = null)
    } else {
        PropertyProxy("generatorTest.PropertyCommon#_buzz", spyOnGet = { spyOn.buzz }, spyOnSet = {
            spyOn.buzz = it; Unit }, collector = verifier, freeze = freeze, relaxer = null)
    }


    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        SyncFunProxy("generatorTest.PropertyCommon#_toString", spyOn = if (spyOn != null) { {
            toString() } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = null,
            relaxer = null, buildInRelaxer = { super.toString() })

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        SyncFunProxy("generatorTest.PropertyCommon#_equals", spyOn = if (spyOn != null) { { other ->
            equals(other) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = null,
            relaxer = null, buildInRelaxer = { other -> super.equals(other) })

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        SyncFunProxy("generatorTest.PropertyCommon#_hashCode", spyOn = if (spyOn != null) { {
            hashCode() } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = null,
            relaxer = null, buildInRelaxer = { super.hashCode() })

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean = _equals.invoke(other)

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
