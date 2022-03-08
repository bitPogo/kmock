// COMMON SOURCE
package generatorTest

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class SyncFunctionCommonMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: SyncFunctionCommon? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : SyncFunctionCommon {
    public val _foo: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunProxy("generatorTest.SyncFunctionCommon#_foo", spyOn = if (spyOn != null) { { fuzz,
            ozz ->
            foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public override fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public fun _clearMock(): Unit {
        _foo.clear()
    }
}
