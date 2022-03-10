package generatorTest

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class GenericsMock<K : Any, L>(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: Generics<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Generics<K, L> where L : CharSequence, L : Comparable<L> {
    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> = if (spyOn == null) {
        PropertyProxy("generatorTest.Generics#_template", spyOnGet = null, spyOnSet = null,
            collector = verifier, freeze = freeze, relaxer = null)
    } else {
        PropertyProxy("generatorTest.Generics#_template", spyOnGet = { spyOn.template }, spyOnSet =
        { spyOn.template = it; Unit }, collector = verifier, freeze = freeze, relaxer = null)
    }


    public val _foo: KMockContract.SyncFunProxy<Any?, () -> Any?> =
        SyncFunProxy("generatorTest.Generics#_foo", spyOn = if (spyOn != null) { { foo() } } else {
            null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (Any?) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_fooWithAny", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _bla: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        SyncFunProxy("generatorTest.Generics#_bla", spyOn = if (spyOn != null) { { bla() } } else {
            null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _blaWithInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_blaWithInt", spyOn = if (spyOn != null) { { payload ->
            bla(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _bar:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_bar", spyOn = if (spyOn != null) { {
            payload ->
        bar(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                    (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        SyncFunProxy("generatorTest.Generics#_barWithVoid", spyOn = if (spyOn != null) { { bar() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _blubb:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_blubb", spyOn = if (spyOn != null) { {
            payload ->
        blubb(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                      (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        SyncFunProxy("generatorTest.Generics#_blubbWithVoid", spyOn = if (spyOn != null) { { blubb() }
        } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _buss:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_buss", spyOn = if (spyOn != null) { {
            payload ->
        buss(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        SyncFunProxy("generatorTest.Generics#_bussWithVoid", spyOn = if (spyOn != null) { { buss() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _boss:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_boss", spyOn = if (spyOn != null) { {
            payload ->
        boss(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        SyncFunProxy("generatorTest.Generics#_bossWithVoid", spyOn = if (spyOn != null) { { boss() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _buzz:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_buzz", spyOn = if (spyOn != null) { {
            payload ->
        buzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        SyncFunProxy("generatorTest.Generics#_buzzWithVoid", spyOn = if (spyOn != null) { { buzz() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _ozz: KMockContract.SyncFunProxy<Unit, (L) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_ozz", spyOn = if (spyOn != null) { { payload ->
            ozz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        SyncFunProxy("generatorTest.Generics#_ozzWithVoid", spyOn = if (spyOn != null) { { ozz() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _brass:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any>>>) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_brass", spyOn = if (spyOn != null) { {
            payload ->
        throw IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
    { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any>>>> =
        SyncFunProxy("generatorTest.Generics#_brassWithVoid", spyOn = if (spyOn != null) { { throw
        IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _bliss:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_bliss", spyOn = if (spyOn != null) { {
            payload ->
        throw IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
    { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?> =
        SyncFunProxy("generatorTest.Generics#_blissWithVoid", spyOn = if (spyOn != null) { { throw
        IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _loss: KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String,
        kotlin.String>) -> kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_loss", spyOn = if
                                                                                                   (spyOn != null) { { payload ->
        loss(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        SyncFunProxy("generatorTest.Generics#_lossWithVoid", spyOn = if (spyOn != null) { { loss() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _uzz: KMockContract.SyncFunProxy<Unit, (Any) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_uzz", spyOn = if (spyOn != null) { { payload ->
            @Suppress("UNCHECKED_CAST")
            payload as generatorTest.SomeGeneric<kotlin.String>
            @Suppress("UNCHECKED_CAST")
            payload as kotlin.collections.List<kotlin.String>
            uzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        SyncFunProxy("generatorTest.Generics#_uzzWithVoid", spyOn = if (spyOn != null) { { uzz() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _lzz: KMockContract.SyncFunProxy<Unit, (Any) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_lzz", spyOn = if (spyOn != null) { { payload ->
            @Suppress("UNCHECKED_CAST")
            payload as generatorTest.SomeGeneric<kotlin.String>
            @Suppress("UNCHECKED_CAST")
            payload as kotlin.collections.List<kotlin.String>?
            lzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        SyncFunProxy("generatorTest.Generics#_lzzWithVoid", spyOn = if (spyOn != null) { { lzz() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _tzz: KMockContract.SyncFunProxy<Unit, (Any?) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_tzz", spyOn = if (spyOn != null) { { payload ->
            @Suppress("UNCHECKED_CAST")
            payload as generatorTest.SomeGeneric<kotlin.String>?
            @Suppress("UNCHECKED_CAST")
            payload as kotlin.collections.List<kotlin.String>?
            tzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> Any?> =
        SyncFunProxy("generatorTest.Generics#_tzzWithVoid", spyOn = if (spyOn != null) { { tzz() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _rzz: KMockContract.SyncFunProxy<Unit, (Any) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_rzz", spyOn = if (spyOn != null) { { payload ->
            @Suppress("UNCHECKED_CAST")
            payload as generatorTest.SomeGeneric<kotlin.String>
            @Suppress("UNCHECKED_CAST")
            payload as kotlin.collections.Map<kotlin.String, kotlin.String>
            rzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        SyncFunProxy("generatorTest.Generics#_rzzWithVoid", spyOn = if (spyOn != null) { { rzz() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _izz: KMockContract.SyncFunProxy<Unit, (Any) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_izz", spyOn = if (spyOn != null) { { payload ->
            throw IllegalArgumentException(
                "Recursive generics are not supported on function level spies (yet)."
            ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
        { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        SyncFunProxy("generatorTest.Generics#_izzWithVoid", spyOn = if (spyOn != null) { { throw
        IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _oss: KMockContract.SyncFunProxy<Unit, (Any?, Any?) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_oss", spyOn = if (spyOn != null) { { arg0, arg1 ->
            oss(arg0, arg1) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                           (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _ossWithAny: KMockContract.SyncFunProxy<Any?, (Any?) -> Any?> =
        SyncFunProxy("generatorTest.Generics#_ossWithAny", spyOn = if (spyOn != null) { { arg0 ->
            oss(arg0) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _kss: KMockContract.SyncFunProxy<Unit, (Any, Any) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_kss", spyOn = if (spyOn != null) { { arg0, arg1 ->
            throw IllegalArgumentException(
                "Recursive generics are not supported on function level spies (yet)."
            ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
        { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _kssWithGeneratorTestSomeGeneric: KMockContract.SyncFunProxy<Any, (Any) -> Any> =
        SyncFunProxy("generatorTest.Generics#_kssWithGeneratorTestSomeGeneric", spyOn = if (spyOn !=
            null) { { arg0 ->
            throw IllegalArgumentException(
                "Recursive generics are not supported on function level spies (yet)."
            ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _foo.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithAny.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Int> bla(): T = _bla.invoke() as T

    public override fun <T : Int> bla(payload: T): Unit = _blaWithInt.invoke(payload)

    public override fun <T : List<Array<String>>> bar(payload: T): Unit = _bar.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String>>> bar(): T = _barWithVoid.invoke() as T

    public override fun <T : List<Array<String?>>> blubb(payload: T): Unit = _blubb.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String?>>> blubb(): T = _blubbWithVoid.invoke() as T

    public override fun <T : List<Array<Int>>?> buss(payload: T): Unit = _buss.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>?> buss(): T = _bussWithVoid.invoke() as T

    public override fun <T : List<Array<Int>?>> boss(payload: T): Unit = _boss.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>?>> boss(): T = _bossWithVoid.invoke() as T

    public override fun <T : List<Array<Int>>> buzz(payload: T?): Unit = _buzz.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>> buzz(): T? = _buzzWithVoid.invoke() as T?

    public override fun <T : L> ozz(payload: T): Unit = _ozz.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : L> ozz(): T = _ozzWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>> brass(payload: T): Unit =
        _brass.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>> brass(): T = _brassWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>?> bliss(payload: T): Unit =
        _bliss.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>?> bliss(): T = _blissWithVoid.invoke() as T

    public override fun <T : Map<String, String>> loss(payload: T): Unit = _loss.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Map<String, String>> loss(): T = _lossWithVoid.invoke() as T

    public override fun <T> uzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String> =
        _uzz.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String> =
        _uzzWithVoid.invoke() as T

    public override fun <T> lzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String>? =
        _lzz.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithVoid.invoke() as T

    public override fun <T> tzz(payload: T): Unit where T : SomeGeneric<String>?, T : List<String>? =
        _tzz.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithVoid.invoke() as T

    public override fun <T> rzz(payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzz.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzzWithVoid.invoke() as T

    public override fun <T> izz(payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izz.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izzWithVoid.invoke() as T

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _oss.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithAny.invoke(arg0) as R

    public override fun <T : R, R> kss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kss.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kssWithGeneratorTestSomeGeneric.invoke(arg0) as R

    public fun _clearMock(): Unit {
        _template.clear()
        _foo.clear()
        _fooWithAny.clear()
        _bla.clear()
        _blaWithInt.clear()
        _bar.clear()
        _barWithVoid.clear()
        _blubb.clear()
        _blubbWithVoid.clear()
        _buss.clear()
        _bussWithVoid.clear()
        _boss.clear()
        _bossWithVoid.clear()
        _buzz.clear()
        _buzzWithVoid.clear()
        _ozz.clear()
        _ozzWithVoid.clear()
        _brass.clear()
        _brassWithVoid.clear()
        _bliss.clear()
        _blissWithVoid.clear()
        _loss.clear()
        _lossWithVoid.clear()
        _uzz.clear()
        _uzzWithVoid.clear()
        _lzz.clear()
        _lzzWithVoid.clear()
        _tzz.clear()
        _tzzWithVoid.clear()
        _rzz.clear()
        _rzzWithVoid.clear()
        _izz.clear()
        _izzWithVoid.clear()
        _oss.clear()
        _ossWithAny.clear()
        _kss.clear()
        _kssWithGeneratorTestSomeGeneric.clear()
    }
}
