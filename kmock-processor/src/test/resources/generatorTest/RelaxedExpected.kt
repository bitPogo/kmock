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
    freeze: Boolean = true,
    relaxed: Boolean = false
) : Relaxed {
    public override val buzz: String
        get() = _buzz.onGet()

    public val _buzz: KMockContract.PropertyMockery<String> = if (spyOn == null) {
        PropertyMockery("generatorTest.Relaxed#_buzz", spyOnGet = null, collector = verifier,
            freeze = freeze, relaxer = if(relaxed) { { mockId -> relaxed(mockId) } } else { null })
    } else {
        PropertyMockery("generatorTest.Relaxed#_buzz", spyOnGet = { spyOn.buzz }, collector =
        verifier, freeze = freeze, relaxer = if(relaxed) { { mockId -> relaxed(mockId) } } else {
            null })
    }


    public val _foo: KMockContract.SyncFunMockery<String, (kotlin.Any) -> kotlin.String> =
        SyncFunMockery("generatorTest.Relaxed#_foo", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, freeze = freeze, relaxer = if(relaxed)
        { { mockId -> relaxed(mockId) } } else { null })

    public val _bar: KMockContract.AsyncFunMockery<String, suspend (kotlin.Any) -> kotlin.String> =
        AsyncFunMockery("generatorTest.Relaxed#_bar", spyOn = if (spyOn != null) { { payload ->
            bar(payload) } } else { null }, collector = verifier, freeze = freeze, relaxer = if(relaxed)
        { { mockId -> relaxed(mockId) } } else { null })

    public override fun foo(payload: Any): String = _foo.invoke(payload)

    public override suspend fun bar(payload: Any): String = _bar.invoke(payload)

    public fun _clearMock(): Unit {
        _buzz.clear()
        _foo.clear()
        _bar.clear()
    }
}
