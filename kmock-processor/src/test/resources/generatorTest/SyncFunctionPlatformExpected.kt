package generatorTest

import kotlin.Any
import kotlin.Int
import kotlin.Unit
import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.SyncFunMockery

internal class SyncFunctionPlatformMock(
    verifier: KMockContract.Collector = KMockContract.Collector { _, _ -> Unit }
) : SyncFunctionPlatform {
    public val fooFun: KMockContract.SyncFunMockery<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionPlatform#fooFun", verifier)

    public override fun foo(fuzz: Int, ozz: Any): Any = fooFun.invoke(fuzz, ozz)

    public fun clearMock(): Unit {
        fooFun.clear()
    }
}
