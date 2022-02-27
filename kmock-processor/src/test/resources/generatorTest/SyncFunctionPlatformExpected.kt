package generatorTest

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Unit
import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.SyncFunMockery
import tech.antibytes.kmock.relaxVoidFunction

internal class SyncFunctionPlatformMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: SyncFunctionPlatform? = null,
    freeze: Boolean = true,
    relaxUnitFun: Boolean = false
) : SyncFunctionPlatform {
    public val _foo: KMockContract.SyncFunMockery<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionPlatform#_foo", spyOn = if (spyOn != null) { { fuzz,
            ozz ->
            foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                          (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public override fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public fun _clearMock(): Unit {
        _foo.clear()
    }
}
