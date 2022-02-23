package generatorTest

import kotlin.Any
import kotlin.String
import kotlin.Unit
import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.SyncFunMockery

internal class PropertyPlatformMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: PropertyPlatform? = null
) : PropertyPlatform {
    public override val foo: String
        get() = fooProp.onGet()

    public val fooProp: KMockContract.PropertyMockery<String> =
        PropertyMockery("generatorTest.PropertyPlatform#fooProp", spyOnGet = if (spyOn != null) {
            spyOn::foo::get } else { null }, collector = verifier, )

    public override var buzz: Any
        get() = buzzProp.onGet()
        set(`value`) = buzzProp.onSet(value)

    public val buzzProp: KMockContract.PropertyMockery<Any> =
        PropertyMockery("generatorTest.PropertyPlatform#buzzProp", spyOnGet = if (spyOn != null) {
            spyOn::buzz::get } else { null }, spyOnSet = if (spyOn != null) { spyOn::buzz::set } else {
            null }, collector = verifier, )

    public fun clearMock(): Unit {
        fooProp.clear()
        buzzProp.clear()
    }
}
