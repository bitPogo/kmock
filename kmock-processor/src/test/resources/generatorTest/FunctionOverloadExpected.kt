package generatorTest

import kotlin.Any
import kotlin.Function1
import kotlin.Int
import kotlin.String
import kotlin.Unit
import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.SyncFunMockery

internal class SyncFunctionOverloadMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: SyncFunctionOverload? = null
) : SyncFunctionOverload {
    public val fooFun: KMockContract.SyncFunMockery<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#fooFun", spyOn = if (spyOn != null) { {
                fuzz ,ozz ->
            foo(fuzz ,ozz) } } else { null }, collector = verifier, )

    public val fooWithAnyFun: KMockContract.SyncFunMockery<Any, (kotlin.Any, kotlin.Int) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#fooWithAnyFun", spyOn = if
                                                                                                 (spyOn != null) { { fuzz ,ozz ->
        foo(fuzz ,ozz) } } else { null }, collector = verifier, )

    public val fooWithStringFun: KMockContract.SyncFunMockery<Any, (kotlin.Any, kotlin.String) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#fooWithStringFun", spyOn = if
                                                                                                    (spyOn != null) { { fuzz ,ozz ->
        foo(fuzz ,ozz) } } else { null }, collector = verifier, )

    public val fooWithStringAnyFun: KMockContract.SyncFunMockery<Any, (kotlin.String, kotlin.Any) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#fooWithStringAnyFun", spyOn =
    if (spyOn != null) { { fuzz ,ozz ->
        foo(fuzz ,ozz) } } else { null }, collector = verifier, )

    public val fooWithGeneratorTestAbcFun: KMockContract.SyncFunMockery<Any, (kotlin.String,
        generatorTest.Abc) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#fooWithGeneratorTestAbcFun", spyOn = if
                                                                                                    (spyOn != null) { { fuzz ,ozz ->
            foo(fuzz ,ozz) } } else { null }, collector = verifier, )

    public val fooWithFunction1Fun: KMockContract.SyncFunMockery<Any, (kotlin.Function1<kotlin.Any,
        kotlin.Unit>) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#fooWithFunction1Fun", spyOn = if (spyOn !=
            null) { { fuzz ->
            foo(fuzz) } } else { null }, collector = verifier, )

    public override fun foo(fuzz: Int, ozz: Any): Any = fooFun.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: Int): Any = fooWithAnyFun.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: String): Any = fooWithStringFun.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Any): Any = fooWithStringAnyFun.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Abc): Any = fooWithGeneratorTestAbcFun.invoke(fuzz,
        ozz)

    public override fun foo(fuzz: Function1<Any, Unit>): Any = fooWithFunction1Fun.invoke(fuzz)

    public fun clearMock(): Unit {
        fooFun.clear()
        fooWithAnyFun.clear()
        fooWithStringFun.clear()
        fooWithStringAnyFun.clear()
        fooWithGeneratorTestAbcFun.clear()
        fooWithFunction1Fun.clear()
    }
}
