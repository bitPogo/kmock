// TEST
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

internal class AsyncFunctionSharedMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: AsyncFunctionShared? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : AsyncFunctionShared {
    public val _foo: KMockContract.AsyncFunProxy<Any, suspend (kotlin.Int, kotlin.Any) -> kotlin.Any>
        = AsyncFunProxy("generatorTest.AsyncFunctionShared#_foo", spyOn = if (spyOn != null) { { fuzz,
        ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                      (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public override suspend fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public fun _clearMock(): Unit {
        _foo.clear()
    }
}
