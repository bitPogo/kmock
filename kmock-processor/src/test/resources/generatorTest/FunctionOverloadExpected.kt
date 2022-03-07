package generatorTest

import kotlin.Any
import kotlin.Boolean
import kotlin.Function1
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.mock.AsyncFunMockery
import tech.antibytes.kmock.mock.PropertyMockery
import tech.antibytes.kmock.mock.SyncFunMockery
import tech.antibytes.kmock.mock.relaxVoidFunction

internal class SyncFunctionOverloadMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: SyncFunctionOverload? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : SyncFunctionOverload {
    public override val foo: Any
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyMockery<Any> = if (spyOn == null) {
        PropertyMockery("generatorTest.SyncFunctionOverload#_foo", spyOnGet = null, collector =
        verifier, freeze = freeze, relaxer = null)
    } else {
        PropertyMockery("generatorTest.SyncFunctionOverload#_foo", spyOnGet = { spyOn.foo },
            collector = verifier, freeze = freeze, relaxer = null)
    }


    public val _fooWithIntAny: KMockContract.SyncFunMockery<Any, (kotlin.Int, kotlin.Any) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithIntAny", spyOn = if
                                                                                                  (spyOn != null) { { fuzz, ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                      (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _fooWithAnyInt: KMockContract.SyncFunMockery<Any, (kotlin.Any, kotlin.Int) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithAnyInt", spyOn = if
                                                                                                  (spyOn != null) { { fuzz, ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                      (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _fooWithAnyString: KMockContract.SyncFunMockery<Any, (kotlin.Any, kotlin.String) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithAnyString", spyOn =
    if (spyOn != null) { { fuzz, ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                      (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _fooWithStringAny: KMockContract.SyncFunMockery<Any, (kotlin.String, kotlin.Any) ->
    kotlin.Any> = SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithStringAny", spyOn =
    if (spyOn != null) { { fuzz, ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                      (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _fooWithStringGeneratorTestAbc: KMockContract.SyncFunMockery<Any, (kotlin.String,
        generatorTest.Abc) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithStringGeneratorTestAbc", spyOn = if
                                                                                                        (spyOn != null) { { fuzz, ozz ->
            foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                          (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _fooWithFunction1: KMockContract.SyncFunMockery<Any, (kotlin.Function1<kotlin.Any,
        kotlin.Unit>) -> kotlin.Any> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithFunction1", spyOn = if (spyOn !=
            null) { { fuzz ->
            foo(fuzz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _fooWithAny: KMockContract.SyncFunMockery<Unit, (Any?) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithAny", spyOn = if (spyOn != null) {
            { fuzz ->
                foo(fuzz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                         (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _fooWithGeneratorTestSyncFunctionOverload:
        KMockContract.SyncFunMockery<Unit, (generatorTest.SyncFunctionOverload) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithGeneratorTestSyncFunctionOverload",
            spyOn = if (spyOn != null) { { fuzz ->
                foo(fuzz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                         (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _fooWithGeneratorTestLPG: KMockContract.SyncFunMockery<Unit, (generatorTest.LPG) ->
    kotlin.Unit> = SyncFunMockery("generatorTest.SyncFunctionOverload#_fooWithGeneratorTestLPG",
        spyOn = if (spyOn != null) { { fuzz ->
            foo(fuzz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public override fun foo(fuzz: Int, ozz: Any): Any = _fooWithIntAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: Int): Any = _fooWithAnyInt.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: String): Any = _fooWithAnyString.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Any): Any = _fooWithStringAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Abc): Any = _fooWithStringGeneratorTestAbc.invoke(fuzz,
        ozz)

    public override fun foo(fuzz: Function1<Any, Unit>): Any = _fooWithFunction1.invoke(fuzz)

    public override fun <T> foo(fuzz: T): Unit = _fooWithAny.invoke(fuzz)

    public override fun <T : SyncFunctionOverload> foo(fuzz: T): Unit =
        _fooWithGeneratorTestSyncFunctionOverload.invoke(fuzz)

    public override fun <T : LPG> foo(fuzz: T): Unit = _fooWithGeneratorTestLPG.invoke(fuzz)

    public fun _clearMock(): Unit {
        _foo.clear()
        _fooWithIntAny.clear()
        _fooWithAnyInt.clear()
        _fooWithAnyString.clear()
        _fooWithStringAny.clear()
        _fooWithStringGeneratorTestAbc.clear()
        _fooWithFunction1.clear()
        _fooWithAny.clear()
        _fooWithGeneratorTestSyncFunctionOverload.clear()
        _fooWithGeneratorTestLPG.clear()
    }
}
