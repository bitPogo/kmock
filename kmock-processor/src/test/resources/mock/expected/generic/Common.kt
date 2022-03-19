// COMMONTEST
package mock.template.generic

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
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

internal class CommonMock<K : Any, L>(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: Common<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Common<K, L> where L : Any, L : Comparable<L> {
    private val __spyOn: Common<K, L>? = spyOn

    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> = if (spyOn == null) {
        PropertyProxy("mock.template.generic.Common#_template", spyOnGet = null, spyOnSet = null,
            collector = verifier, freeze = freeze, relaxer = null)
    } else {
        PropertyProxy("mock.template.generic.Common#_template", spyOnGet = { spyOn.template },
            spyOnSet = { spyOn.template = it; Unit }, collector = verifier, freeze = freeze, relaxer =
            null)
    }


    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        SyncFunProxy("mock.template.generic.Common#_fooWithVoid", spyOn = if (spyOn != null) { { foo()
        } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.Common#_fooWithAny", spyOn = if (spyOn != null) { {
                payload ->
            foo(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        SyncFunProxy("mock.template.generic.Common#_blaWithVoid", spyOn = if (spyOn != null) { { bla()
        } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _blaWithInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.Common#_blaWithInt", spyOn = if (spyOn != null) { {
                payload ->
            bla(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _barWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.Common#_barWithCollectionsList", spyOn = if
                                                                                                        (spyOn != null) { { payload ->
        bar(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                    (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        SyncFunProxy("mock.template.generic.Common#_barWithVoid", spyOn = if (spyOn != null) { { bar()
        } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _blubbWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.Common#_blubbWithCollectionsList", spyOn =
    if (spyOn != null) { { payload ->
        blubb(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                      (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        SyncFunProxy("mock.template.generic.Common#_blubbWithVoid", spyOn = if (spyOn != null) { {
            blubb() } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _bussWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.Common#_bussWithCollectionsList", spyOn =
    if (spyOn != null) { { payload ->
        buss(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        SyncFunProxy("mock.template.generic.Common#_bussWithVoid", spyOn = if (spyOn != null) { {
            buss() } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _bossWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.Common#_bossWithCollectionsList", spyOn =
    if (spyOn != null) { { payload ->
        boss(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        SyncFunProxy("mock.template.generic.Common#_bossWithVoid", spyOn = if (spyOn != null) { {
            boss() } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _buzzWithT:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.Common#_buzzWithT", spyOn = if (spyOn !=
        null) { { payload ->
        buzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        SyncFunProxy("mock.template.generic.Common#_buzzWithVoid", spyOn = if (spyOn != null) { {
            buzz() } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _ozzWithL: KMockContract.SyncFunProxy<Unit, (L) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.Common#_ozzWithL", spyOn = if (spyOn != null) { {
                payload ->
            ozz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        SyncFunProxy("mock.template.generic.Common#_ozzWithVoid", spyOn = if (spyOn != null) { { ozz()
        } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _brassWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.Common#_brassWithComparable", spyOn = if
                                                                                                     (spyOn != null) { { payload ->
        throw IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
    { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>> =
        SyncFunProxy("mock.template.generic.Common#_brassWithVoid", spyOn = if (spyOn != null) { {
            throw IllegalArgumentException(
                "Recursive generics are not supported on function level spies (yet)."
            ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _blissWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.Common#_blissWithComparable", spyOn = if
                                                                                                     (spyOn != null) { { payload ->
        throw IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
    { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?> =
        SyncFunProxy("mock.template.generic.Common#_blissWithVoid", spyOn = if (spyOn != null) { {
            throw IllegalArgumentException(
                "Recursive generics are not supported on function level spies (yet)."
            ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _lossWithCollectionsMap:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String, kotlin.String>) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.Common#_lossWithCollectionsMap", spyOn = if
                                                                                                        (spyOn != null) { { payload ->
        loss(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        SyncFunProxy("mock.template.generic.Common#_lossWithVoid", spyOn = if (spyOn != null) { {
            loss() } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _uzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.Common#_uzzWithMockTemplateGenericSomeGeneric", spyOn = if
                                                                                                        (spyOn != null) { { payload ->
            @Suppress("UNCHECKED_CAST")
            payload as mock.template.generic.SomeGeneric<kotlin.String>
            @Suppress("UNCHECKED_CAST")
            payload as kotlin.collections.List<kotlin.String>
            uzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        SyncFunProxy("mock.template.generic.Common#_uzzWithVoid", spyOn = if (spyOn != null) { { uzz()
        } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _lzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.Common#_lzzWithMockTemplateGenericSomeGeneric", spyOn = if
                                                                                                        (spyOn != null) { { payload ->
            @Suppress("UNCHECKED_CAST")
            payload as mock.template.generic.SomeGeneric<kotlin.String>
            @Suppress("UNCHECKED_CAST")
            payload as kotlin.collections.List<kotlin.String>?
            lzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        SyncFunProxy("mock.template.generic.Common#_lzzWithVoid", spyOn = if (spyOn != null) { { lzz()
        } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _tzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.Common#_tzzWithMockTemplateGenericSomeGeneric", spyOn = if
                                                                                                        (spyOn != null) { { payload ->
            @Suppress("UNCHECKED_CAST")
            payload as mock.template.generic.SomeGeneric<kotlin.String>?
            @Suppress("UNCHECKED_CAST")
            payload as kotlin.collections.List<kotlin.String>?
            tzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        SyncFunProxy("mock.template.generic.Common#_tzzWithVoid", spyOn = if (spyOn != null) { { tzz()
        } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _rzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.Common#_rzzWithMockTemplateGenericSomeGeneric", spyOn = if
                                                                                                        (spyOn != null) { { payload ->
            @Suppress("UNCHECKED_CAST")
            payload as mock.template.generic.SomeGeneric<kotlin.String>
            @Suppress("UNCHECKED_CAST")
            payload as kotlin.collections.Map<kotlin.String, kotlin.String>
            rzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        SyncFunProxy("mock.template.generic.Common#_rzzWithVoid", spyOn = if (spyOn != null) { { rzz()
        } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _izzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.Common#_izzWithMockTemplateGenericSomeGeneric", spyOn = if
                                                                                                        (spyOn != null) { { payload ->
            throw IllegalArgumentException(
                "Recursive generics are not supported on function level spies (yet)."
            ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
        { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        SyncFunProxy("mock.template.generic.Common#_izzWithVoid", spyOn = if (spyOn != null) { { throw
        IllegalArgumentException(
            "Recursive generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _ossWithAnyAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> = SyncFunProxy("mock.template.generic.Common#_ossWithAnyAny", spyOn = if (spyOn
        != null) { { arg0, arg1 ->
        oss(arg0, arg1) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                       (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
    null)

    public val _ossWithAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        SyncFunProxy("mock.template.generic.Common#_ossWithAny", spyOn = if (spyOn != null) { {
                arg0 ->
            oss(arg0) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any, kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.Common#_kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric",
            spyOn = if (spyOn != null) { { arg0, arg1 ->
                throw IllegalArgumentException(
                    "Recursive generics are not supported on function level spies (yet)."
                ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
            { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _kssWithMockTemplateGenericSomeGeneric: KMockContract.SyncFunProxy<Any, (kotlin.Any) ->
    kotlin.Any> =
        SyncFunProxy("mock.template.generic.Common#_kssWithMockTemplateGenericSomeGeneric", spyOn = if
                                                                                                        (spyOn != null) { { arg0 ->
            throw IllegalArgumentException(
                "Recursive generics are not supported on function level spies (yet)."
            ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _issWithAnyMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.Common#_issWithAnyMockTemplateGenericSomeGeneric", spyOn =
        if (spyOn != null) { { arg0, arg1 ->
            @Suppress("UNCHECKED_CAST")
            arg1 as mock.template.generic.SomeGeneric<kotlin.String>
            @Suppress("UNCHECKED_CAST")
            arg1 as kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>
            iss(arg0, arg1) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                           (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)

    public val _issWithAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        SyncFunProxy("mock.template.generic.Common#_issWithAny", spyOn = if (spyOn != null) { {
                arg0 ->
            iss(arg0) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

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
        _uzzWithMockTemplateGenericSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String> =
        _uzzWithVoid.invoke() as T

    public override fun <T> lzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithMockTemplateGenericSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithVoid.invoke() as T

    public override fun <T> tzz(payload: T): Unit where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithMockTemplateGenericSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithVoid.invoke() as T

    public override fun <T> rzz(payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzzWithMockTemplateGenericSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzzWithVoid.invoke() as T

    public override fun <T> izz(payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izzWithMockTemplateGenericSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izzWithVoid.invoke() as T

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithAnyAny.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithAny.invoke(arg0) as R

    public override fun <T : R, R> kss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        _kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kssWithMockTemplateGenericSomeGeneric.invoke(arg0) as R

    public override fun <R, T> iss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithAnyMockTemplateGenericSomeGeneric.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithAny.invoke(arg0) as R

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
        _uzzWithMockTemplateGenericSomeGeneric.clear()
        _uzzWithVoid.clear()
        _lzzWithMockTemplateGenericSomeGeneric.clear()
        _lzzWithVoid.clear()
        _tzzWithMockTemplateGenericSomeGeneric.clear()
        _tzzWithVoid.clear()
        _rzzWithMockTemplateGenericSomeGeneric.clear()
        _rzzWithVoid.clear()
        _izzWithMockTemplateGenericSomeGeneric.clear()
        _izzWithVoid.clear()
        _ossWithAnyAny.clear()
        _ossWithAny.clear()
        _kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric.clear()
        _kssWithMockTemplateGenericSomeGeneric.clear()
        _issWithAnyMockTemplateGenericSomeGeneric.clear()
        _issWithAny.clear()
    }
}
