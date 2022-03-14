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


    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        SyncFunProxy("generatorTest.Generics#_fooWithVoid", spyOn = if (spyOn != null) { { foo() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_fooWithAny", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        SyncFunProxy("generatorTest.Generics#_blaWithVoid", spyOn = if (spyOn != null) { { bla() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _blaWithInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_blaWithInt", spyOn = if (spyOn != null) { { payload ->
            bla(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _barWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_barWithCollectionsList", spyOn = if
                                                                                                  (spyOn != null) { { payload ->
        bar(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                    (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        SyncFunProxy("generatorTest.Generics#_barWithVoid", spyOn = if (spyOn != null) { { bar() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _blubbWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_blubbWithCollectionsList", spyOn = if
                                                                                                    (spyOn != null) { { payload ->
        blubb(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                      (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        SyncFunProxy("generatorTest.Generics#_blubbWithVoid", spyOn = if (spyOn != null) { { blubb() }
        } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _bussWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_bussWithCollectionsList", spyOn = if
                                                                                                   (spyOn != null) { { payload ->
        buss(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        SyncFunProxy("generatorTest.Generics#_bussWithVoid", spyOn = if (spyOn != null) { { buss() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _bossWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_bossWithCollectionsList", spyOn = if
                                                                                                   (spyOn != null) { { payload ->
        boss(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        SyncFunProxy("generatorTest.Generics#_bossWithVoid", spyOn = if (spyOn != null) { { boss() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _buzzWithT:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_buzzWithT", spyOn = if (spyOn != null) {
        { payload ->
            buzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                         (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        SyncFunProxy("generatorTest.Generics#_buzzWithVoid", spyOn = if (spyOn != null) { { buzz() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _ozzWithL: KMockContract.SyncFunProxy<Unit, (L) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_ozzWithL", spyOn = if (spyOn != null) { { payload ->
            ozz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        SyncFunProxy("generatorTest.Generics#_ozzWithVoid", spyOn = if (spyOn != null) { { ozz() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _brassWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_brassWithComparable", spyOn = if (spyOn
        != null) { { payload ->
        throw IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
    { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>> =
        SyncFunProxy("generatorTest.Generics#_brassWithVoid", spyOn = if (spyOn != null) { { throw
        IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _blissWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_blissWithComparable", spyOn = if (spyOn
        != null) { { payload ->
        throw IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
    { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?> =
        SyncFunProxy("generatorTest.Generics#_blissWithVoid", spyOn = if (spyOn != null) { { throw
        IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _lossWithCollectionsMap:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String, kotlin.String>) ->
        kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_lossWithCollectionsMap", spyOn = if
                                                                                                  (spyOn != null) { { payload ->
        loss(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        SyncFunProxy("generatorTest.Generics#_lossWithVoid", spyOn = if (spyOn != null) { { loss() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _uzzWithGeneratorTestSomeGeneric: KMockContract.SyncFunProxy<Unit, (kotlin.Any) ->
    kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_uzzWithGeneratorTestSomeGeneric", spyOn =
    if (spyOn != null) { { payload ->
        @Suppress("UNCHECKED_CAST")
        payload as generatorTest.SomeGeneric<kotlin.String>
        @Suppress("UNCHECKED_CAST")
        payload as kotlin.collections.List<kotlin.String>
        uzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                    (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        SyncFunProxy("generatorTest.Generics#_uzzWithVoid", spyOn = if (spyOn != null) { { uzz() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _lzzWithGeneratorTestSomeGeneric: KMockContract.SyncFunProxy<Unit, (kotlin.Any) ->
    kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_lzzWithGeneratorTestSomeGeneric", spyOn =
    if (spyOn != null) { { payload ->
        @Suppress("UNCHECKED_CAST")
        payload as generatorTest.SomeGeneric<kotlin.String>
        @Suppress("UNCHECKED_CAST")
        payload as kotlin.collections.List<kotlin.String>?
        lzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                    (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        SyncFunProxy("generatorTest.Generics#_lzzWithVoid", spyOn = if (spyOn != null) { { lzz() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _tzzWithGeneratorTestSomeGeneric: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) ->
    kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_tzzWithGeneratorTestSomeGeneric", spyOn =
    if (spyOn != null) { { payload ->
        @Suppress("UNCHECKED_CAST")
        payload as generatorTest.SomeGeneric<kotlin.String>?
        @Suppress("UNCHECKED_CAST")
        payload as kotlin.collections.List<kotlin.String>?
        tzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                    (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        SyncFunProxy("generatorTest.Generics#_tzzWithVoid", spyOn = if (spyOn != null) { { tzz() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _rzzWithGeneratorTestSomeGeneric: KMockContract.SyncFunProxy<Unit, (kotlin.Any) ->
    kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_rzzWithGeneratorTestSomeGeneric", spyOn =
    if (spyOn != null) { { payload ->
        @Suppress("UNCHECKED_CAST")
        payload as generatorTest.SomeGeneric<kotlin.String>
        @Suppress("UNCHECKED_CAST")
        payload as kotlin.collections.Map<kotlin.String, kotlin.String>
        rzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                    (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        SyncFunProxy("generatorTest.Generics#_rzzWithVoid", spyOn = if (spyOn != null) { { rzz() } }
        else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _izzWithGeneratorTestSomeGeneric: KMockContract.SyncFunProxy<Unit, (kotlin.Any) ->
    kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_izzWithGeneratorTestSomeGeneric", spyOn =
    if (spyOn != null) { { payload ->
        throw IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
    { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        SyncFunProxy("generatorTest.Generics#_izzWithVoid", spyOn = if (spyOn != null) { { throw
        IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _ossWithAnyAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_ossWithAnyAny", spyOn = if (spyOn !=
        null) { { arg0, arg1 ->
        oss(arg0, arg1) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                       (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _ossWithAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        SyncFunProxy("generatorTest.Generics#_ossWithAny", spyOn = if (spyOn != null) { { arg0 ->
            oss(arg0) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _kssWithGeneratorTestSomeGenericGeneratorTestSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any, kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_kssWithGeneratorTestSomeGenericGeneratorTestSomeGeneric",
            spyOn = if (spyOn != null) { { arg0, arg1 ->
                throw IllegalArgumentException(
                    "Recursive generics are not supported on function level spies (yet)."
                ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
            { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _kssWithGeneratorTestSomeGeneric: KMockContract.SyncFunProxy<Any, (kotlin.Any) ->
    kotlin.Any> = SyncFunProxy("generatorTest.Generics#_kssWithGeneratorTestSomeGeneric", spyOn =
    if (spyOn != null) { { arg0 ->
        throw IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _issWithAnyGeneratorTestSomeGeneric: KMockContract.SyncFunProxy<Unit, (kotlin.Any?,
        kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_issWithAnyGeneratorTestSomeGeneric", spyOn = if (spyOn
            != null) { { arg0, arg1 ->
            @Suppress("UNCHECKED_CAST")
            arg1 as generatorTest.SomeGeneric<kotlin.String>
            @Suppress("UNCHECKED_CAST")
            arg1 as kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>
            iss(arg0, arg1) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                           (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _issWithAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        SyncFunProxy("generatorTest.Generics#_issWithAny", spyOn = if (spyOn != null) { { arg0 ->
            iss(arg0) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        SyncFunProxy("generatorTest.Generics#_toString", spyOn = if (spyOn != null) { {
            spyOn.toString() } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer =
        null, relaxer = null, buildInRelaxer = { super.toString() }, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        SyncFunProxy("generatorTest.Generics#_equals", spyOn = if (spyOn != null) { { other ->
            spyOn.equals(other) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer =
        null, relaxer = null, buildInRelaxer = { other -> super.equals(other) },
            ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        SyncFunProxy("generatorTest.Generics#_hashCode", spyOn = if (spyOn != null) { {
            spyOn.hashCode() } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer =
        null, relaxer = null, buildInRelaxer = { super.hashCode() }, ignorableForVerification = true)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithAny.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Int> bla(): T = _blaWithVoid.invoke() as T

    public override fun <T : Int> bla(payload: T): Unit = _blaWithInt.invoke(payload)

    public override fun <T : List<Array<String>>> bar(payload: T): Unit =
        _barWithCollectionsList.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String>>> bar(): T = _barWithVoid.invoke() as T

    public override fun <T : List<Array<String?>>> blubb(payload: T): Unit =
        _blubbWithCollectionsList.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String?>>> blubb(): T = _blubbWithVoid.invoke() as T

    public override fun <T : List<Array<Int>>?> buss(payload: T): Unit =
        _bussWithCollectionsList.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>?> buss(): T = _bussWithVoid.invoke() as T

    public override fun <T : List<Array<Int>?>> boss(payload: T): Unit =
        _bossWithCollectionsList.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>?>> boss(): T = _bossWithVoid.invoke() as T

    public override fun <T : List<Array<Int>>> buzz(payload: T?): Unit = _buzzWithT.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>> buzz(): T? = _buzzWithVoid.invoke() as T?

    public override fun <T : L> ozz(payload: T): Unit = _ozzWithL.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : L> ozz(): T = _ozzWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>> brass(payload: T): Unit =
        _brassWithComparable.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>> brass(): T = _brassWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>?> bliss(payload: T): Unit =
        _blissWithComparable.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>?> bliss(): T = _blissWithVoid.invoke() as T

    public override fun <T : Map<String, String>> loss(payload: T): Unit =
        _lossWithCollectionsMap.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Map<String, String>> loss(): T = _lossWithVoid.invoke() as T

    public override fun <T> uzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String> =
        _uzzWithGeneratorTestSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String> =
        _uzzWithVoid.invoke() as T

    public override fun <T> lzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithGeneratorTestSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithVoid.invoke() as T

    public override fun <T> tzz(payload: T): Unit where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithGeneratorTestSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithVoid.invoke() as T

    public override fun <T> rzz(payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzzWithGeneratorTestSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzzWithVoid.invoke() as T

    public override fun <T> izz(payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izzWithGeneratorTestSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izzWithVoid.invoke() as T

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithAnyAny.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithAny.invoke(arg0) as R

    public override fun <T : R, R> kss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        _kssWithGeneratorTestSomeGenericGeneratorTestSomeGeneric.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kssWithGeneratorTestSomeGeneric.invoke(arg0) as R

    public override fun <R, T> iss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithAnyGeneratorTestSomeGeneric.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithAny.invoke(arg0) as R

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean = _equals.invoke(other)

    public override fun hashCode(): Int = _hashCode.invoke()

    public fun _clearMock(): Unit {
        _template.clear()
        _fooWithVoid.clear()
        _fooWithAny.clear()
        _blaWithVoid.clear()
        _blaWithInt.clear()
        _barWithCollectionsList.clear()
        _barWithVoid.clear()
        _blubbWithCollectionsList.clear()
        _blubbWithVoid.clear()
        _bussWithCollectionsList.clear()
        _bussWithVoid.clear()
        _bossWithCollectionsList.clear()
        _bossWithVoid.clear()
        _buzzWithT.clear()
        _buzzWithVoid.clear()
        _ozzWithL.clear()
        _ozzWithVoid.clear()
        _brassWithComparable.clear()
        _brassWithVoid.clear()
        _blissWithComparable.clear()
        _blissWithVoid.clear()
        _lossWithCollectionsMap.clear()
        _lossWithVoid.clear()
        _uzzWithGeneratorTestSomeGeneric.clear()
        _uzzWithVoid.clear()
        _lzzWithGeneratorTestSomeGeneric.clear()
        _lzzWithVoid.clear()
        _tzzWithGeneratorTestSomeGeneric.clear()
        _tzzWithVoid.clear()
        _rzzWithGeneratorTestSomeGeneric.clear()
        _rzzWithVoid.clear()
        _izzWithGeneratorTestSomeGeneric.clear()
        _izzWithVoid.clear()
        _ossWithAnyAny.clear()
        _ossWithAny.clear()
        _kssWithGeneratorTestSomeGenericGeneratorTestSomeGeneric.clear()
        _kssWithGeneratorTestSomeGeneric.clear()
        _issWithAnyGeneratorTestSomeGeneric.clear()
        _issWithAny.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
