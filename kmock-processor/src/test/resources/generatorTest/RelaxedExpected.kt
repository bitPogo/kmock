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

internal class RelaxedMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: Relaxed? = null,
    relaxed: Boolean = false
) : Relaxed {
    public override val buzz: String
        get() = buzzProp.onGet()

    public val buzzProp: KMockContract.PropertyMockery<String> =
        PropertyMockery("generatorTest.Relaxed#buzzProp", spyOnGet = if (spyOn != null) {
            spyOn::buzz::get } else { null }, collector = verifier, relaxer = if(relaxed) { { mockId ->
            relaxed(mockId) } } else { null })

    public val fooFun: KMockContract.SyncFunMockery<String, (kotlin.Any) -> kotlin.String> =
        SyncFunMockery("generatorTest.Relaxed#fooFun", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, relaxer = if(relaxed) { { mockId ->
            relaxed(mockId) } } else { null })

    public val barFun: KMockContract.AsyncFunMockery<String, suspend (kotlin.Any) -> kotlin.String> =
        AsyncFunMockery("generatorTest.Relaxed#barFun", spyOn = if (spyOn != null) { { payload ->
            bar(payload) } } else { null }, collector = verifier, relaxer = if(relaxed) { { mockId ->
            relaxed(mockId) } } else { null })

    public override fun foo(payload: Any): String = fooFun.invoke(payload)

    public override suspend fun bar(payload: Any): String = barFun.invoke(payload)

    public fun clearMock(): Unit {
        buzzProp.clear()
        fooFun.clear()
        barFun.clear()
    }
}
