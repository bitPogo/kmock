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

internal class PropertyPlatformMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: PropertyPlatform? = null,
    freeze: Boolean = true
) : PropertyPlatform {
    public override val foo: String
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyMockery<String> =
        PropertyMockery("generatorTest.PropertyPlatform#_foo", spyOnGet = if (spyOn != null) { {
            spyOn.foo } } else { null }, collector = verifier, freeze = freeze, )

    public override var buzz: Any
        get() = _buzz.onGet()
        set(`value`) = _buzz.onSet(value)

    public val _buzz: KMockContract.PropertyMockery<Any> =
        PropertyMockery("generatorTest.PropertyPlatform#buzz", spyOnGet = if (spyOn != null) { {
            spyOn.buzz } } else { null }, spyOnSet = if (spyOn != null) { { spyOn.buzz = it } } else {
            null }, collector = verifier, freeze = freeze, )

    public fun _clearMock(): Unit {
        _foo.clear()
        _buzz.clear()
    }
}
