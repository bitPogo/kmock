package generatorTest

import kotlin.Any
import kotlin.Boolean
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.mock.AsyncFunMockery
import tech.antibytes.kmock.mock.PropertyMockery
import tech.antibytes.kmock.mock.SyncFunMockery
import tech.antibytes.kmock.mock.relaxVoidFunction

internal class PropertyPlatformMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: PropertyPlatform? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false
) : PropertyPlatform {
    public override val foo: String
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyMockery<String> = if (spyOn == null) {
        PropertyMockery("generatorTest.PropertyPlatform#_foo", spyOnGet = null, collector =
        verifier, freeze = freeze, relaxer = null)
    } else {
        PropertyMockery("generatorTest.PropertyPlatform#_foo", spyOnGet = { spyOn.foo }, collector
        = verifier, freeze = freeze, relaxer = null)
    }


    public override var buzz: Any
        get() = _buzz.onGet()
        set(`value`) = _buzz.onSet(value)

    public val _buzz: KMockContract.PropertyMockery<Any> = if (spyOn == null) {
        PropertyMockery("generatorTest.PropertyPlatform#_buzz", spyOnGet = null, spyOnSet = null,
            collector = verifier, freeze = freeze, relaxer = null)
    } else {
        PropertyMockery("generatorTest.PropertyPlatform#_buzz", spyOnGet = { spyOn.buzz }, spyOnSet
        = { spyOn.buzz = it; Unit }, collector = verifier, freeze = freeze, relaxer = null)
    }


    public fun _clearMock(): Unit {
        _foo.clear()
        _buzz.clear()
    }
}
