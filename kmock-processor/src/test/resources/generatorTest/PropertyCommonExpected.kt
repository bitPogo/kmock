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

internal class PropertyCommonStub(
    verifier: KMockContract.Collector = KMockContract.Collector { _, _ -> Unit }
) : PropertyCommon {
    public override val foo: String
        get() = fooProp.onGet()

    public val fooProp: KMockContract.PropertyMockery<String> =
        PropertyMockery("generatorTest.PropertyCommon#foo", verifier)

    public override var buzz: Any
        get() = buzzProp.onGet()
        set(`value`) = buzzProp.onSet(value)

    public val buzzProp: KMockContract.PropertyMockery<Any> =
        PropertyMockery("generatorTest.PropertyCommon#buzz", verifier)

    public fun clear(): Unit {
        fooProp.clear()
        buzzProp.clear()
    }
}
