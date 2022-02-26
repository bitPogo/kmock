// COMMON SOURCE
package generatorTest

import kotlin.Any
import kotlin.Int
import kotlin.Unit
import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.SyncFunMockery

internal class SyncFunctionCommonMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: SyncFunctionCommon? = null
) : SyncFunctionCommon {
    public val _foo: KMockContract.SyncFunMockery<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionCommon#_foo", spyOn = if (spyOn != null) { { fuzz,
            ozz ->
            foo(fuzz, ozz) } } else { null }, collector = verifier, )

    public override fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public fun _clearMock(): Unit {
        _foo.clear()
    }
}
