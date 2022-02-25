// COMMON SOURCE
package generatorTest

import kotlin.Any
import kotlin.String
import kotlin.Unit
import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.SyncFunMockery

internal class PropertyCommonMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: PropertyCommon? = null
) : PropertyCommon {
    public override val foo: String
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyMockery<String> =
        PropertyMockery("generatorTest.PropertyCommon#_foo", spyOnGet = if (spyOn != null) {
            spyOn::foo::get } else { null }, collector = verifier, )

    public override var buzz: Any
        get() = _buzz.onGet()
        set(`value`) = _buzz.onSet(value)

    public val _buzz: KMockContract.PropertyMockery<Any> =
        PropertyMockery("generatorTest.PropertyCommon#buzz", spyOnGet = if (spyOn != null) {
            spyOn::buzz::get } else { null }, spyOnSet = if (spyOn != null) { spyOn::buzz::set } else {
            null }, collector = verifier, )

    public fun _clearMock(): Unit {
        _foo.clear()
        _buzz.clear()
    }
}
