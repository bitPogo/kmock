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

internal class PlatformMock<K : Any, L>(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Platform<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        PropertyProxy("mock.template.generic.PlatformMock#_template", spyOnGet = null, spyOnSet =
        null, collector = verifier, freeze = freeze, relaxer = null)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        SyncFunProxy("mock.template.generic.PlatformMock#_fooWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.PlatformMock#_fooWithAny", spyOn = null, collector =
        verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { { relaxVoidFunction() } } else
        { null }, relaxer = null, buildInRelaxer = null)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        SyncFunProxy("mock.template.generic.PlatformMock#_blaWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _blaWithInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.PlatformMock#_blaWithInt", spyOn = null, collector =
        verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { { relaxVoidFunction() } } else
        { null }, relaxer = null, buildInRelaxer = null)

    public val _barWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.PlatformMock#_barWithCollectionsList",
        spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
            relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        SyncFunProxy("mock.template.generic.PlatformMock#_barWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _blubbWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.PlatformMock#_blubbWithCollectionsList",
        spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
            relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        SyncFunProxy("mock.template.generic.PlatformMock#_blubbWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _bussWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.PlatformMock#_bussWithCollectionsList",
        spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
            relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        SyncFunProxy("mock.template.generic.PlatformMock#_bussWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _bossWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.PlatformMock#_bossWithCollectionsList",
        spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
            relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        SyncFunProxy("mock.template.generic.PlatformMock#_bossWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _buzzWithT:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.PlatformMock#_buzzWithT", spyOn = null,
        collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
            relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        SyncFunProxy("mock.template.generic.PlatformMock#_buzzWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _ozzWithL: KMockContract.SyncFunProxy<Unit, (L) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.PlatformMock#_ozzWithL", spyOn = null, collector =
        verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { { relaxVoidFunction() } } else
        { null }, relaxer = null, buildInRelaxer = null)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        SyncFunProxy("mock.template.generic.PlatformMock#_ozzWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _brassWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.PlatformMock#_brassWithComparable", spyOn =
    null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
        relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>> =
        SyncFunProxy("mock.template.generic.PlatformMock#_brassWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _blissWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.PlatformMock#_blissWithComparable", spyOn =
    null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
        relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?> =
        SyncFunProxy("mock.template.generic.PlatformMock#_blissWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _lossWithCollectionsMap:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String, kotlin.String>) ->
        kotlin.Unit> = SyncFunProxy("mock.template.generic.PlatformMock#_lossWithCollectionsMap",
        spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
            relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        SyncFunProxy("mock.template.generic.PlatformMock#_lossWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _uzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.PlatformMock#_uzzWithMockTemplateGenericSomeGeneric",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        SyncFunProxy("mock.template.generic.PlatformMock#_uzzWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _lzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.PlatformMock#_lzzWithMockTemplateGenericSomeGeneric",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        SyncFunProxy("mock.template.generic.PlatformMock#_lzzWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _tzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.PlatformMock#_tzzWithMockTemplateGenericSomeGeneric",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        SyncFunProxy("mock.template.generic.PlatformMock#_tzzWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _rzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.PlatformMock#_rzzWithMockTemplateGenericSomeGeneric",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        SyncFunProxy("mock.template.generic.PlatformMock#_rzzWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _izzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.PlatformMock#_izzWithMockTemplateGenericSomeGeneric",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        SyncFunProxy("mock.template.generic.PlatformMock#_izzWithVoid", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _ossWithAnyAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> = SyncFunProxy("mock.template.generic.PlatformMock#_ossWithAnyAny", spyOn = null,
        collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
            relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _ossWithAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        SyncFunProxy("mock.template.generic.PlatformMock#_ossWithAny", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

    public val _kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any, kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.PlatformMock#_kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _kssWithMockTemplateGenericSomeGeneric: KMockContract.SyncFunProxy<Any, (kotlin.Any) ->
    kotlin.Any> =
        SyncFunProxy("mock.template.generic.PlatformMock#_kssWithMockTemplateGenericSomeGeneric",
            spyOn = null, collector = verifier, freeze = freeze, relaxer = null)

    public val _issWithAnyMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any) -> kotlin.Unit> =
        SyncFunProxy("mock.template.generic.PlatformMock#_issWithAnyMockTemplateGenericSomeGeneric",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _issWithAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        SyncFunProxy("mock.template.generic.PlatformMock#_issWithAny", spyOn = null, collector =
        verifier, freeze = freeze, relaxer = null)

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
