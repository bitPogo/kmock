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
    public override val foo: Any
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyMockery<Any> =
        PropertyMockery("generatorTest.SyncFunctionOverload#_foo", spyOnGet = if (spyOn != null) { {
            spyOn.foo } } else { null }, collector = verifier, )

    public val _fooWithIntAny: KMockContract.SyncFunMockery<Any, (kotlin.Int, kotlin.Any) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithIntAny", spyOn = if
                                                                                                  (spyOn != null) { { fuzz, ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, )

    public val _fooWithAnyInt: KMockContract.SyncFunMockery<Any, (kotlin.Any, kotlin.Int) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithAnyInt", spyOn = if
                                                                                                  (spyOn != null) { { fuzz, ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, )

    public val _fooWithAnyString: KMockContract.SyncFunMockery<Any, (kotlin.Any, kotlin.String) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithAnyString", spyOn =
    if (spyOn != null) { { fuzz, ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, )

    public val _fooWithStringAny: KMockContract.SyncFunMockery<Any, (kotlin.String, kotlin.Any) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithStringAny", spyOn =
    if (spyOn != null) { { fuzz, ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, )

    public val _fooWithStringGeneratorTestAbc: KMockContract.SyncFunMockery<Any, (kotlin.String,
        generatorTest.Abc) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithStringGeneratorTestAbc", spyOn = if
                                                                                                        (spyOn != null) { { fuzz, ozz ->
            foo(fuzz, ozz) } } else { null }, collector = verifier, )

    public val _fooWithFunction1: KMockContract.SyncFunMockery<Any, (kotlin.Function1<kotlin.Any,
        kotlin.Unit>) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithFunction1", spyOn = if (spyOn !=
            null) { { fuzz ->
            foo(fuzz) } } else { null }, collector = verifier, )

    public val _fooWithAny: KMockContract.SyncFunMockery<Unit, (Any?) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithAny", spyOn = if (spyOn != null) {
            { fuzz ->
                foo(fuzz) } } else { null }, collector = verifier, )

    public val _fooWithSyncFunctionOverload:
        KMockContract.SyncFunMockery<Unit, (SyncFunctionOverload) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithSyncFunctionOverload", spyOn = if
                                                                                                      (spyOn != null) { { fuzz ->
            foo(fuzz) } } else { null }, collector = verifier, )

    public val _fooWithLPG: KMockContract.SyncFunMockery<Unit, (LPG) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithLPG", spyOn = if (spyOn != null) {
            { fuzz ->
                foo(fuzz) } } else { null }, collector = verifier, )

    public override fun foo(fuzz: Int, ozz: Any): Any = _fooWithIntAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: Int): Any = _fooWithAnyInt.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: String): Any = _fooWithAnyString.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Any): Any = _fooWithStringAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Abc): Any = _fooWithStringGeneratorTestAbc.invoke(fuzz,
        ozz)

    public override fun foo(fuzz: Function1<Any, Unit>): Any = _fooWithFunction1.invoke(fuzz)

    public override fun <T> foo(fuzz: T): Unit = _fooWithAny.invoke(fuzz)

    public override fun <T : SyncFunctionOverload> foo(fuzz: T): Unit =
        _fooWithSyncFunctionOverload.invoke(fuzz)

    public override fun <T : LPG> foo(fuzz: T): Unit = _fooWithLPG.invoke(fuzz)

    public fun _clearMock(): Unit {
        _foo.clear()
        _fooWithIntAny.clear()
        _fooWithAnyInt.clear()
        _fooWithAnyString.clear()
        _fooWithStringAny.clear()
        _fooWithStringGeneratorTestAbc.clear()
        _fooWithFunction1.clear()
        _fooWithAny.clear()
        _fooWithSyncFunctionOverload.clear()
        _fooWithLPG.clear()
    }
}
