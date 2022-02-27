// COMMON SOURCE
package generatorTest

import kotlin.Any
import kotlin.Boolean
import kotlin.String
import kotlin.Unit
import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.SyncFunMockery
import tech.antibytes.kmock.relaxVoidFunction

internal class PropertyCommonMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: PropertyCommon? = null,
    freeze: Boolean = true,
    relaxUnitFun: Boolean = false
) : PropertyCommon {
    public override val foo: String
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyMockery<String> = if (spyOn == null) {
        PropertyMockery("generatorTest.PropertyCommon#_foo", spyOnGet = null, collector = verifier,
            freeze = freeze, relaxer = null)
    } else {
        PropertyMockery("generatorTest.PropertyCommon#_foo", spyOnGet = { spyOn.foo }, collector =
        verifier, freeze = freeze, relaxer = null)
    }


    public override var buzz: Any
        get() = _buzz.onGet()
        set(`value`) = _buzz.onSet(value)

    public val _buzz: KMockContract.PropertyMockery<Any> = if (spyOn == null) {
        PropertyMockery("generatorTest.PropertyCommon#_buzz", spyOnGet = null, spyOnSet = null,
            collector = verifier, freeze = freeze, relaxer = null)
    } else {
        PropertyMockery("generatorTest.PropertyCommon#_buzz", spyOnGet = { spyOn.buzz }, spyOnSet =
        { spyOn.buzz = it; Unit }, collector = verifier, freeze = freeze, relaxer = null)
    }


    public fun _clearMock(): Unit {
        _foo.clear()
        _buzz.clear()
    }
}
