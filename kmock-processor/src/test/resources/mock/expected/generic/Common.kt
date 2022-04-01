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
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Common<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.generic.CommonMock#_template", collector =
        verifier, freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_fooWithVoid", collector =
        verifier, freeze = freeze)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_fooWithAny", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blaWithVoid", collector =
        verifier, freeze = freeze)

    public val _blaWithInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blaWithInt", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blaWithInts: KMockContract.SyncFunProxy<Unit, (kotlin.IntArray) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blaWithInts", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_barWithVoid", collector =
        verifier, freeze = freeze)

    public val _barWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_barWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _barWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_barWithCollectionsLists",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blubbWithVoid", collector =
        verifier, freeze = freeze)

    public val _blubbWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blubbWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blubbWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blubbWithCollectionsLists",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bussWithVoid", collector =
        verifier, freeze = freeze)

    public val _bussWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bussWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _bussWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bussWithCollectionsLists",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bossWithVoid", collector =
        verifier, freeze = freeze)

    public val _bossWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bossWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _bossWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bossWithCollectionsLists",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_buzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _buzzWithT:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_buzzWithT",
        collector = verifier, freeze = freeze) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public val _buzzWithTs: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_buzzWithTs", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ozzWithVoid", collector =
        verifier, freeze = freeze)

    public val _ozzWithL: KMockContract.SyncFunProxy<Unit, (L) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ozzWithL", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _ozzWithLs: KMockContract.SyncFunProxy<Unit, (Array<out L>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ozzWithLs", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_brassWithVoid", collector =
        verifier, freeze = freeze)

    public val _brassWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_brassWithComparable",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _brassWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_brassWithComparables",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blissWithVoid", collector =
        verifier, freeze = freeze)

    public val _blissWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blissWithComparable",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blissWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blissWithComparables",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lossWithVoid", collector =
        verifier, freeze = freeze)

    public val _lossWithCollectionsMap:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String, kotlin.String>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lossWithCollectionsMap",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _lossWithCollectionsMaps: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lossWithCollectionsMaps",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_uzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _uzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_uzzWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _uzzWithMockTemplateGenericSomeGenerics: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_uzzWithMockTemplateGenericSomeGenerics",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _lzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lzzWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _lzzWithMockTemplateGenericSomeGenerics: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lzzWithMockTemplateGenericSomeGenerics",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_tzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _tzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_tzzWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _tzzWithMockTemplateGenericSomeGenerics: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_tzzWithMockTemplateGenericSomeGenerics",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_rzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _rzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_rzzWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _rzzWithMockTemplateGenericSomeGenerics: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_rzzWithMockTemplateGenericSomeGenerics",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_izzWithVoid", collector =
        verifier, freeze = freeze)

    public val _izzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_izzWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _izzWithMockTemplateGenericSomeGenerics: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_izzWithMockTemplateGenericSomeGenerics",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _ossWithAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ossWithAny", collector =
        verifier, freeze = freeze)

    public val _ossWithAnyAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ossWithAnyAny", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _ossWithAnyAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, Array<out
    kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ossWithAnyAnys", collector
        = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _kssWithMockTemplateGenericSomeGeneric: KMockContract.SyncFunProxy<Any, (kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_kssWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze)

    public val _kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _issWithAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_issWithAny", collector =
        verifier, freeze = freeze)

    public val _issWithAnyMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_issWithAnyMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithAny.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Int> bla(): T = _blaWithVoid.invoke() as T

    public override fun <T : Int> bla(payload: T): Unit = _blaWithInt.invoke(payload)

    public override fun <T : Int> bla(vararg payload: T): Unit = _blaWithInts.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String>>> bar(): T = _barWithVoid.invoke() as T

    public override fun <T : List<Array<String>>> bar(payload: T): Unit =
        _barWithCollectionsList.invoke(payload)

    public override fun <T : List<Array<String>>> bar(vararg payload: T): Unit =
        _barWithCollectionsLists.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String?>>> blubb(): T = _blubbWithVoid.invoke() as T

    public override fun <T : List<Array<String?>>> blubb(payload: T): Unit =
        _blubbWithCollectionsList.invoke(payload)

    public override fun <T : List<Array<String?>>> blubb(vararg payload: T): Unit =
        _blubbWithCollectionsLists.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>?> buss(): T = _bussWithVoid.invoke() as T

    public override fun <T : List<Array<Int>>?> buss(payload: T): Unit =
        _bussWithCollectionsList.invoke(payload)

    public override fun <T : List<Array<Int>>?> buss(vararg payload: T): Unit =
        _bussWithCollectionsLists.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>?>> boss(): T = _bossWithVoid.invoke() as T

    public override fun <T : List<Array<Int>?>> boss(payload: T): Unit =
        _bossWithCollectionsList.invoke(payload)

    public override fun <T : List<Array<Int>?>> boss(vararg payload: T): Unit =
        _bossWithCollectionsLists.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>> buzz(): T? = _buzzWithVoid.invoke() as T?

    public override fun <T : List<Array<Int>>> buzz(payload: T?): Unit = _buzzWithT.invoke(payload)

    public override fun <T : List<Array<Int>>> buzz(vararg payload: T?): Unit =
        _buzzWithTs.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : L> ozz(): T = _ozzWithVoid.invoke() as T

    public override fun <T : L> ozz(payload: T): Unit = _ozzWithL.invoke(payload)

    public override fun <T : L> ozz(vararg payload: T): Unit = _ozzWithLs.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>> brass(): T = _brassWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>> brass(payload: T): Unit =
        _brassWithComparable.invoke(payload)

    public override fun <T : Comparable<List<Array<T>>>> brass(vararg payload: T): Unit =
        _brassWithComparables.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>?> bliss(): T = _blissWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>?> bliss(payload: T): Unit =
        _blissWithComparable.invoke(payload)

    public override fun <T : Comparable<List<Array<T>>>?> bliss(vararg payload: T): Unit =
        _blissWithComparables.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Map<String, String>> loss(): T = _lossWithVoid.invoke() as T

    public override fun <T : Map<String, String>> loss(payload: T): Unit =
        _lossWithCollectionsMap.invoke(payload)

    public override fun <T : Map<String, String>> loss(vararg payload: T): Unit =
        _lossWithCollectionsMaps.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String> =
        _uzzWithVoid.invoke() as T

    public override fun <T> uzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String> =
        _uzzWithMockTemplateGenericSomeGeneric.invoke(payload)

    public override fun <T> uzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    List<String> = _uzzWithMockTemplateGenericSomeGenerics.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithVoid.invoke() as T

    public override fun <T> lzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithMockTemplateGenericSomeGeneric.invoke(payload)

    public override fun <T> lzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    List<String>? = _lzzWithMockTemplateGenericSomeGenerics.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithVoid.invoke() as T

    public override fun <T> tzz(payload: T): Unit where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithMockTemplateGenericSomeGeneric.invoke(payload)

    public override fun <T> tzz(vararg payload: T): Unit where T : SomeGeneric<String>?, T :
    List<String>? = _tzzWithMockTemplateGenericSomeGenerics.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzzWithVoid.invoke() as T

    public override fun <T> rzz(payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzzWithMockTemplateGenericSomeGeneric.invoke(payload)

    public override fun <T> rzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzzWithMockTemplateGenericSomeGenerics.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izzWithVoid.invoke() as T

    public override fun <T> izz(payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izzWithMockTemplateGenericSomeGeneric.invoke(payload)

    public override fun <T> izz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izzWithMockTemplateGenericSomeGenerics.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithAny.invoke(arg0) as R

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithAnyAny.invoke(arg0, arg1)

    public override fun <T : R, R> oss(arg0: R, vararg arg1: T): Unit = _ossWithAnyAnys.invoke(arg0,
        arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kssWithMockTemplateGenericSomeGeneric.invoke(arg0) as R

    public override fun <T : R, R> kss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        _kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithAny.invoke(arg0) as R

    public override fun <R, T> iss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithAnyMockTemplateGenericSomeGeneric.invoke(arg0, arg1)

    public fun _clearMock(): Unit {
        _template.clear()
        _fooWithVoid.clear()
        _fooWithAny.clear()
        _blaWithVoid.clear()
        _blaWithInt.clear()
        _blaWithInts.clear()
        _barWithVoid.clear()
        _barWithCollectionsList.clear()
        _barWithCollectionsLists.clear()
        _blubbWithVoid.clear()
        _blubbWithCollectionsList.clear()
        _blubbWithCollectionsLists.clear()
        _bussWithVoid.clear()
        _bussWithCollectionsList.clear()
        _bussWithCollectionsLists.clear()
        _bossWithVoid.clear()
        _bossWithCollectionsList.clear()
        _bossWithCollectionsLists.clear()
        _buzzWithVoid.clear()
        _buzzWithT.clear()
        _buzzWithTs.clear()
        _ozzWithVoid.clear()
        _ozzWithL.clear()
        _ozzWithLs.clear()
        _brassWithVoid.clear()
        _brassWithComparable.clear()
        _brassWithComparables.clear()
        _blissWithVoid.clear()
        _blissWithComparable.clear()
        _blissWithComparables.clear()
        _lossWithVoid.clear()
        _lossWithCollectionsMap.clear()
        _lossWithCollectionsMaps.clear()
        _uzzWithVoid.clear()
        _uzzWithMockTemplateGenericSomeGeneric.clear()
        _uzzWithMockTemplateGenericSomeGenerics.clear()
        _lzzWithVoid.clear()
        _lzzWithMockTemplateGenericSomeGeneric.clear()
        _lzzWithMockTemplateGenericSomeGenerics.clear()
        _tzzWithVoid.clear()
        _tzzWithMockTemplateGenericSomeGeneric.clear()
        _tzzWithMockTemplateGenericSomeGenerics.clear()
        _rzzWithVoid.clear()
        _rzzWithMockTemplateGenericSomeGeneric.clear()
        _rzzWithMockTemplateGenericSomeGenerics.clear()
        _izzWithVoid.clear()
        _izzWithMockTemplateGenericSomeGeneric.clear()
        _izzWithMockTemplateGenericSomeGenerics.clear()
        _ossWithAny.clear()
        _ossWithAnyAny.clear()
        _ossWithAnyAnys.clear()
        _kssWithMockTemplateGenericSomeGeneric.clear()
        _kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric.clear()
        _issWithAny.clear()
        _issWithAnyMockTemplateGenericSomeGeneric.clear()
    }
}
