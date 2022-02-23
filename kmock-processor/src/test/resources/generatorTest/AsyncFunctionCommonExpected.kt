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

internal class AsyncFunctionCommonMock(
    verifier: KMockContract.Collector = KMockContract.Collector { _, _ -> Unit }
) : AsyncFunctionCommon {
    public val fooFun: KMockContract.AsyncFunMockery<Any, suspend (kotlin.Int, kotlin.Any) ->
    kotlin.Any> = AsyncFunMockery("generatorTest.AsyncFunctionCommon#fooFun", verifier)

    public override suspend fun foo(fuzz: Int, ozz: Any): Any = fooFun.invoke(fuzz, ozz)

    public fun clearMock(): Unit {
        fooFun.clear()
    }
}
