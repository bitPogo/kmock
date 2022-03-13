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
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy
import tech.antibytes.kmock.proxy.relaxVoidFunction

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

    public val _foo: KMockContract.PropertyProxy<Any> = if (spyOn == null) {
        PropertyProxy("generatorTest.SyncFunctionOverload#_foo", spyOnGet = null, collector =
        verifier, freeze = freeze, relaxer = null)
    } else {
        PropertyProxy("generatorTest.SyncFunctionOverload#_foo", spyOnGet = { spyOn.foo },
            collector = verifier, freeze = freeze, relaxer = null)
    }


    public override var hashCode: Int
        get() = _hashCode.onGet()
        set(`value`) = _hashCode.onSet(value)

    public val _hashCode: KMockContract.PropertyProxy<Int> = if (spyOn == null) {
        PropertyProxy("generatorTest.SyncFunctionOverload#_hashCode", spyOnGet = null, spyOnSet =
        null, collector = verifier, freeze = freeze, relaxer = null)
    } else {
        PropertyProxy("generatorTest.SyncFunctionOverload#_hashCode", spyOnGet = { spyOn.hashCode
        }, spyOnSet = { spyOn.hashCode = it; Unit }, collector = verifier, freeze = freeze,
            relaxer = null)
    }


    public val _fooWithIntAny: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any>
        = SyncFunProxy("generatorTest.SyncFunctionOverload#_fooWithIntAny", spyOn = if (spyOn != null)
    { { fuzz, ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _fooWithAnyInt: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.Int) -> kotlin.Any>
        = SyncFunProxy("generatorTest.SyncFunctionOverload#_fooWithAnyInt", spyOn = if (spyOn != null)
    { { fuzz, ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _fooWithAnyString: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.String) ->
    kotlin.Any> = SyncFunProxy("generatorTest.SyncFunctionOverload#_fooWithAnyString", spyOn = if
                                                                                                   (spyOn != null) { { fuzz, ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _fooWithStringAny: KMockContract.SyncFunProxy<Any, (kotlin.String, kotlin.Any) ->
    kotlin.Any> = SyncFunProxy("generatorTest.SyncFunctionOverload#_fooWithStringAny", spyOn = if
                                                                                                   (spyOn != null) { { fuzz, ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _fooWithStringGeneratorTestAbc: KMockContract.SyncFunProxy<Any, (kotlin.String,
        generatorTest.Abc) -> kotlin.Any> =
        SyncFunProxy("generatorTest.SyncFunctionOverload#_fooWithStringGeneratorTestAbc", spyOn = if
                                                                                                      (spyOn != null) { { fuzz, ozz ->
            foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _fooWithFunction1: KMockContract.SyncFunProxy<Any, (kotlin.Function1<kotlin.Any,
        kotlin.Unit>) -> kotlin.Any> =
        SyncFunProxy("generatorTest.SyncFunctionOverload#_fooWithFunction1", spyOn = if (spyOn !=
            null) { { fuzz ->
            foo(fuzz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.SyncFunctionOverload#_fooWithAny", spyOn = if (spyOn != null) { {
                fuzz ->
            foo(fuzz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _fooWithGeneratorTestSyncFunctionOverload:
        KMockContract.SyncFunProxy<Unit, (generatorTest.SyncFunctionOverload) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.SyncFunctionOverload#_fooWithGeneratorTestSyncFunctionOverload",
            spyOn = if (spyOn != null) { { fuzz ->
                foo(fuzz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                         (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
            null)

    public val _fooWithGeneratorTestLPG: KMockContract.SyncFunProxy<Unit, (generatorTest.LPG) ->
    kotlin.Unit> = SyncFunProxy("generatorTest.SyncFunctionOverload#_fooWithGeneratorTestLPG",
        spyOn = if (spyOn != null) { { fuzz ->
            foo(fuzz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        SyncFunProxy("generatorTest.SyncFunctionOverload#_toString", spyOn = if (spyOn != null) { {
            toString() } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = null,
            relaxer = null, buildInRelaxer = { super.toString() })

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        SyncFunProxy("generatorTest.SyncFunctionOverload#_equals", spyOn = if (spyOn != null) { {
                other ->
            equals(other) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = null,
            relaxer = null, buildInRelaxer = { other -> super.equals(other) })

    public val _hashCodeWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        SyncFunProxy("generatorTest.SyncFunctionOverload#_hashCodeWithVoid", spyOn = if (spyOn !=
            null) { { hashCode() } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer
        = null, relaxer = null, buildInRelaxer = { super.hashCode() })

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

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean = _equals.invoke(other)

    public override fun hashCode(): Int = _hashCodeWithVoid.invoke()

    public fun _clearMock(): Unit {
        _foo.clear()
        _hashCode.clear()
        _fooWithIntAny.clear()
        _fooWithAnyInt.clear()
        _fooWithAnyString.clear()
        _fooWithStringAny.clear()
        _fooWithStringGeneratorTestAbc.clear()
        _fooWithFunction1.clear()
        _fooWithAny.clear()
        _fooWithGeneratorTestSyncFunctionOverload.clear()
        _fooWithGeneratorTestLPG.clear()
        _toString.clear()
        _equals.clear()
        _hashCodeWithVoid.clear()
    }
}
