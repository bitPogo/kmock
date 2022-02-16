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

internal class SyncFunctionCommonStub(
    verifier: KMockContract.Collector = KMockContract.Collector { _, _ -> Unit }
) : SyncFunctionCommon {
    public val fooFun: KMockContract.SyncFunMockery<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionCommon#fooFun", verifier)

    public override fun foo(fuzz: Int, ozz: Any): Any = fooFun.invoke(fuzz, ozz)

    public fun clear(): Unit {
        fooFun.clear()
    }
}
