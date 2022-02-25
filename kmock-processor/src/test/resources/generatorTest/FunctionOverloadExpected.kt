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
    public val _foo: KMockContract.SyncFunMockery<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#_foo", spyOn = if (spyOn != null) { { fuzz
            ,ozz ->
            foo(fuzz ,ozz) } } else { null }, collector = verifier, )

    public val _fooWithAny: KMockContract.SyncFunMockery<Any, (kotlin.Any, kotlin.Int) -> kotlin.Any>
        = SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithAny", spyOn = if (spyOn != null)
    { { fuzz ,ozz ->
        foo(fuzz ,ozz) } } else { null }, collector = verifier, )

    public val _fooWithString: KMockContract.SyncFunMockery<Any, (kotlin.Any, kotlin.String) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithString", spyOn = if
                                                                                                  (spyOn != null) { { fuzz ,ozz ->
        foo(fuzz ,ozz) } } else { null }, collector = verifier, )

    public val _fooWithStringAny: KMockContract.SyncFunMockery<Any, (kotlin.String, kotlin.Any) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithStringAny", spyOn =
    if (spyOn != null) { { fuzz ,ozz ->
        foo(fuzz ,ozz) } } else { null }, collector = verifier, )

    public val _fooWithGeneratorTestAbc: KMockContract.SyncFunMockery<Any, (kotlin.String,
        generatorTest.Abc) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithGeneratorTestAbc", spyOn = if
                                                                                                  (spyOn != null) { { fuzz ,ozz ->
            foo(fuzz ,ozz) } } else { null }, collector = verifier, )

    public val _fooWithFunction1: KMockContract.SyncFunMockery<Any, (kotlin.Function1<kotlin.Any,
        kotlin.Unit>) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithFunction1", spyOn = if (spyOn !=
            null) { { fuzz ->
            foo(fuzz) } } else { null }, collector = verifier, )

    public override fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: Int): Any = _fooWithAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: String): Any = _fooWithString.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Any): Any = _fooWithStringAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Abc): Any = _fooWithGeneratorTestAbc.invoke(fuzz, ozz)

    public override fun foo(fuzz: Function1<Any, Unit>): Any = _fooWithFunction1.invoke(fuzz)

    public fun _clearMock(): Unit {
        _foo.clear()
        _fooWithAny.clear()
        _fooWithString.clear()
        _fooWithStringAny.clear()
        _fooWithGeneratorTestAbc.clear()
        _fooWithFunction1.clear()
    }
}
