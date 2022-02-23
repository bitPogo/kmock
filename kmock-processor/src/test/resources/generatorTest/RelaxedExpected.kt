package generatorTest

import generatorTest.relaxed
import kotlin.Any
import kotlin.Boolean
import kotlin.String
import kotlin.Unit
import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.SyncFunMockery

internal class RelaxedStub(
    verifier: KMockContract.Collector = KMockContract.Collector { _, _ -> Unit },
    relaxed: Boolean = false
) : Relaxed {
    public override val buzz: String
        get() = buzzProp.onGet()

    public val buzzProp: KMockContract.PropertyMockery<String> =
        PropertyMockery("generatorTest.Relaxed#buzz", verifier, if(relaxed) { { mockId ->
            relaxed(mockId) } } else { null })

    public val fooFun: KMockContract.SyncFunMockery<String, (kotlin.Any) -> kotlin.String> =
        SyncFunMockery("generatorTest.Relaxed#fooFun", verifier, if(relaxed) { { mockId ->
            relaxed(mockId) } } else { null })

    public val barFun: KMockContract.AsyncFunMockery<String, suspend (kotlin.Any) -> kotlin.String> =
        AsyncFunMockery("generatorTest.Relaxed#barFun", verifier, if(relaxed) { { mockId ->
            relaxed(mockId) } } else { null })

    public override fun foo(payload: Any): String = fooFun.invoke(payload)

    public override suspend fun bar(payload: Any): String = barFun.invoke(payload)

    public fun clear(): Unit {
        buzzProp.clear()
        fooFun.clear()
        barFun.clear()
    }
}
