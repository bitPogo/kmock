package mock.template.compatibility

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Char
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.sequences.Sequence
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Platform<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.compatibility.PlatformMock#_template",
            collector = verifier, freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_fooWithVoid",
            collector = verifier, freeze = freeze)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_fooWithAny",
            collector = verifier, freeze = freeze)

    public val _fooWithAnys: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) -> kotlin.Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_fooWithAnys",
        collector = verifier, freeze = freeze)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blaWithVoid",
            collector = verifier, freeze = freeze)

    public val _blaWithInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blaWithInt",
            collector = verifier, freeze = freeze)

    public val _blaWithInts: KMockContract.SyncFunProxy<Unit, (kotlin.IntArray) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blaWithInts",
            collector = verifier, freeze = freeze)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_barWithVoid",
            collector = verifier, freeze = freeze)

    public val _barWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_barWithCollectionsList",
            collector = verifier, freeze = freeze)

    public val _barWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_barWithCollectionsLists",
            collector = verifier, freeze = freeze)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blubbWithVoid",
            collector = verifier, freeze = freeze)

    public val _blubbWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blubbWithCollectionsList",
            collector = verifier, freeze = freeze)

    public val _blubbWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blubbWithCollectionsLists",
            collector = verifier, freeze = freeze)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_bussWithVoid",
            collector = verifier, freeze = freeze)

    public val _bussWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_bussWithCollectionsList",
            collector = verifier, freeze = freeze)

    public val _bussWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_bussWithCollectionsLists",
            collector = verifier, freeze = freeze)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_bossWithVoid",
            collector = verifier, freeze = freeze)

    public val _bossWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_bossWithCollectionsList",
            collector = verifier, freeze = freeze)

    public val _bossWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_bossWithCollectionsLists",
            collector = verifier, freeze = freeze)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_buzzWithVoid",
            collector = verifier, freeze = freeze)

    public val _buzzWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_buzzWithCollectionsList",
            collector = verifier, freeze = freeze)

    public val _buzzWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_buzzWithCollectionsLists",
            collector = verifier, freeze = freeze)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_ozzWithVoid",
            collector = verifier, freeze = freeze)

    public val _ozzWithL: KMockContract.SyncFunProxy<Unit, (L) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_ozzWithL",
            collector = verifier, freeze = freeze)

    public val _ozzWithLs: KMockContract.SyncFunProxy<Unit, (Array<out L>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_ozzWithLs",
            collector = verifier, freeze = freeze)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_brassWithVoid",
            collector = verifier, freeze = freeze)

    public val _brassWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_brassWithComparable",
            collector = verifier, freeze = freeze)

    public val _brassWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_brassWithComparables",
            collector = verifier, freeze = freeze)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blissWithVoid",
            collector = verifier, freeze = freeze)

    public val _blissWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blissWithComparable",
            collector = verifier, freeze = freeze)

    public val _blissWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blissWithComparables",
            collector = verifier, freeze = freeze)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_lossWithVoid",
            collector = verifier, freeze = freeze)

    public val _lossWithCollectionsMap:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String, kotlin.String>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_lossWithCollectionsMap",
            collector = verifier, freeze = freeze)

    public val _lossWithCollectionsMaps: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_lossWithCollectionsMaps",
            collector = verifier, freeze = freeze)

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_uzzWithVoid",
            collector = verifier, freeze = freeze)

    public val _uzzWithMockTemplateCompatibilitySomeGenericCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_uzzWithMockTemplateCompatibilitySomeGenericCollectionsList",
            collector = verifier, freeze = freeze)

    public val _uzzWithMockTemplateCompatibilitySomeGenericCollectionsLists:
        KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_uzzWithMockTemplateCompatibilitySomeGenericCollectionsLists",
            collector = verifier, freeze = freeze)

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_lzzWithVoid",
            collector = verifier, freeze = freeze)

    public val _lzzWithMockTemplateCompatibilitySomeGenericCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_lzzWithMockTemplateCompatibilitySomeGenericCollectionsList",
            collector = verifier, freeze = freeze)

    public val _lzzWithMockTemplateCompatibilitySomeGenericCollectionsLists:
        KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_lzzWithMockTemplateCompatibilitySomeGenericCollectionsLists",
            collector = verifier, freeze = freeze)

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_tzzWithVoid",
            collector = verifier, freeze = freeze)

    public val _tzzWithMockTemplateCompatibilitySomeGenericCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_tzzWithMockTemplateCompatibilitySomeGenericCollectionsList",
            collector = verifier, freeze = freeze)

    public val _tzzWithMockTemplateCompatibilitySomeGenericCollectionsLists:
        KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_tzzWithMockTemplateCompatibilitySomeGenericCollectionsLists",
            collector = verifier, freeze = freeze)

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_rzzWithVoid",
            collector = verifier, freeze = freeze)

    public val _rzzWithMockTemplateCompatibilitySomeGenericCollectionsMap:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_rzzWithMockTemplateCompatibilitySomeGenericCollectionsMap",
            collector = verifier, freeze = freeze)

    public val _rzzWithMockTemplateCompatibilitySomeGenericCollectionsMaps:
        KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_rzzWithMockTemplateCompatibilitySomeGenericCollectionsMaps",
            collector = verifier, freeze = freeze)

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_izzWithVoid",
            collector = verifier, freeze = freeze)

    public val _izzWithMockTemplateCompatibilitySomeGenericComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_izzWithMockTemplateCompatibilitySomeGenericComparable",
            collector = verifier, freeze = freeze)

    public val _izzWithMockTemplateCompatibilitySomeGenericComparables:
        KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_izzWithMockTemplateCompatibilitySomeGenericComparables",
            collector = verifier, freeze = freeze)

    public val _ossWithAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_ossWithAny",
            collector = verifier, freeze = freeze)

    public val _ossWithAnyAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_ossWithAnyAny",
            collector = verifier, freeze = freeze)

    public val _ossWithAnyAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, Array<out
    kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_ossWithAnyAnys",
            collector = verifier, freeze = freeze)

    public val _kssWithMockTemplateCompatibilitySomeGenericComparable:
        KMockContract.SyncFunProxy<Any, (kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_kssWithMockTemplateCompatibilitySomeGenericComparable",
            collector = verifier, freeze = freeze)

    public
    val _kssWithMockTemplateCompatibilitySomeGenericComparableMockTemplateCompatibilitySomeGenericComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_kssWithMockTemplateCompatibilitySomeGenericComparableMockTemplateCompatibilitySomeGenericComparable",
            collector = verifier, freeze = freeze)

    public val _issWithAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_issWithAny",
            collector = verifier, freeze = freeze)

    public val _issWithAnyMockTemplateCompatibilitySomeGenericComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_issWithAnyMockTemplateCompatibilitySomeGenericComparable",
            collector = verifier, freeze = freeze)

    public val _pssWithMockTemplateCompatibilitySomeGeneric:
        KMockContract.SyncFunProxy<mock.template.compatibility.SomeGeneric<kotlin.String>, (mock.template.compatibility.SomeGeneric<kotlin.String>) ->
        mock.template.compatibility.SomeGeneric<kotlin.String>> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_pssWithMockTemplateCompatibilitySomeGeneric",
            collector = verifier, freeze = freeze)

    public val _pssWithMockTemplateCompatibilitySomeGenericMockTemplateCompatibilitySomeGeneric:
        KMockContract.SyncFunProxy<Unit, (mock.template.compatibility.SomeGeneric<kotlin.String>,
            mock.template.compatibility.SomeGeneric<kotlin.String>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_pssWithMockTemplateCompatibilitySomeGenericMockTemplateCompatibilitySomeGeneric",
            collector = verifier, freeze = freeze)

    public val _xssWithAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_xssWithAny",
            collector = verifier, freeze = freeze)

    public val _xssWithAnySequencesSequenceCharSequence:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_xssWithAnySequencesSequenceCharSequence",
            collector = verifier, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithAny.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> foo(vararg payload: T): Unit = _fooWithAnys.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Int> bla(): T = _blaWithVoid.invoke() as T

    public override fun <T : Int> bla(payload: T): Unit = _blaWithInt.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Int> bla(vararg payload: T): Unit = _blaWithInts.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String>>> bar(): T = _barWithVoid.invoke() as T

    public override fun <T : List<Array<String>>> bar(payload: T): Unit =
        _barWithCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<String>>> bar(vararg payload: T): Unit =
        _barWithCollectionsLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String?>>> blubb(): T = _blubbWithVoid.invoke() as T

    public override fun <T : List<Array<String?>>> blubb(payload: T): Unit =
        _blubbWithCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<String?>>> blubb(vararg payload: T): Unit =
        _blubbWithCollectionsLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>?> buss(): T = _bussWithVoid.invoke() as T

    public override fun <T : List<Array<Int>>?> buss(payload: T): Unit =
        _bussWithCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<Int>>?> buss(vararg payload: T): Unit =
        _bussWithCollectionsLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>?>> boss(): T = _bossWithVoid.invoke() as T

    public override fun <T : List<Array<Int>?>> boss(payload: T): Unit =
        _bossWithCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<Int>?>> boss(vararg payload: T): Unit =
        _bossWithCollectionsLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>> buzz(): T? = _buzzWithVoid.invoke() as T?

    public override fun <T : List<Array<Int>>> buzz(payload: T?): Unit =
        _buzzWithCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<Int>>> buzz(vararg payload: T?): Unit =
        _buzzWithCollectionsLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : L> ozz(): T = _ozzWithVoid.invoke() as T

    public override fun <T : L> ozz(payload: T): Unit = _ozzWithL.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : L> ozz(vararg payload: T): Unit = _ozzWithLs.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>> brass(): T = _brassWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>> brass(payload: T): Unit =
        _brassWithComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<List<Array<T>>>> brass(vararg payload: T): Unit =
        _brassWithComparables.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>?> bliss(): T = _blissWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>?> bliss(payload: T): Unit =
        _blissWithComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<List<Array<T>>>?> bliss(vararg payload: T): Unit =
        _blissWithComparables.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Map<String, String>> loss(): T = _lossWithVoid.invoke() as T

    public override fun <T : Map<String, String>> loss(payload: T): Unit =
        _lossWithCollectionsMap.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Map<String, String>> loss(vararg payload: T): Unit =
        _lossWithCollectionsMaps.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String> =
        _uzzWithVoid.invoke() as T

    public override fun <T> uzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String> =
        _uzzWithMockTemplateCompatibilitySomeGenericCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> uzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    List<String> = _uzzWithMockTemplateCompatibilitySomeGenericCollectionsLists.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithVoid.invoke() as T

    public override fun <T> lzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithMockTemplateCompatibilitySomeGenericCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> lzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    List<String>? = _lzzWithMockTemplateCompatibilitySomeGenericCollectionsLists.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithVoid.invoke() as T

    public override fun <T> tzz(payload: T): Unit where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithMockTemplateCompatibilitySomeGenericCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> tzz(vararg payload: T): Unit where T : SomeGeneric<String>?, T :
    List<String>? = _tzzWithMockTemplateCompatibilitySomeGenericCollectionsLists.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzzWithVoid.invoke() as T

    public override fun <T> rzz(payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> =
        _rzzWithMockTemplateCompatibilitySomeGenericCollectionsMap.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> rzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> =
        _rzzWithMockTemplateCompatibilitySomeGenericCollectionsMaps.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izzWithVoid.invoke() as T

    public override fun <T> izz(payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> =
        _izzWithMockTemplateCompatibilitySomeGenericComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> izz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> =
        _izzWithMockTemplateCompatibilitySomeGenericComparables.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithAny.invoke(arg0) as R

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithAnyAny.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : R, R> oss(arg0: R, vararg arg1: T): Unit = _ossWithAnyAnys.invoke(arg0,
        arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        _kssWithMockTemplateCompatibilitySomeGenericComparable.invoke(arg0) as R

    public override fun <T : R, R> kss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        _kssWithMockTemplateCompatibilitySomeGenericComparableMockTemplateCompatibilitySomeGenericComparable.invoke(arg0,
            arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithAny.invoke(arg0) as R

    public override fun <R, T> iss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> =
        _issWithAnyMockTemplateCompatibilitySomeGenericComparable.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <R : T, T : X, X : SomeGeneric<String>> pss(arg0: T): R =
        _pssWithMockTemplateCompatibilitySomeGeneric.invoke(arg0) as R

    public override fun <R : T, T : X, X : SomeGeneric<String>> pss(arg0: T, arg1: R): Unit =
        _pssWithMockTemplateCompatibilitySomeGenericMockTemplateCompatibilitySomeGeneric.invoke(arg0,
            arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> xss(arg0: T): R where R : Sequence<Char>, R : CharSequence =
        _xssWithAny.invoke(arg0) as R

    public override fun <R, T> xss(arg0: T, arg1: R): Unit where R : Sequence<Char>, R : CharSequence
        = _xssWithAnySequencesSequenceCharSequence.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _template.clear()
        _fooWithVoid.clear()
        _fooWithAny.clear()
        _fooWithAnys.clear()
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
        _buzzWithCollectionsList.clear()
        _buzzWithCollectionsLists.clear()
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
        _uzzWithMockTemplateCompatibilitySomeGenericCollectionsList.clear()
        _uzzWithMockTemplateCompatibilitySomeGenericCollectionsLists.clear()
        _lzzWithVoid.clear()
        _lzzWithMockTemplateCompatibilitySomeGenericCollectionsList.clear()
        _lzzWithMockTemplateCompatibilitySomeGenericCollectionsLists.clear()
        _tzzWithVoid.clear()
        _tzzWithMockTemplateCompatibilitySomeGenericCollectionsList.clear()
        _tzzWithMockTemplateCompatibilitySomeGenericCollectionsLists.clear()
        _rzzWithVoid.clear()
        _rzzWithMockTemplateCompatibilitySomeGenericCollectionsMap.clear()
        _rzzWithMockTemplateCompatibilitySomeGenericCollectionsMaps.clear()
        _izzWithVoid.clear()
        _izzWithMockTemplateCompatibilitySomeGenericComparable.clear()
        _izzWithMockTemplateCompatibilitySomeGenericComparables.clear()
        _ossWithAny.clear()
        _ossWithAnyAny.clear()
        _ossWithAnyAnys.clear()
        _kssWithMockTemplateCompatibilitySomeGenericComparable.clear()
        _kssWithMockTemplateCompatibilitySomeGenericComparableMockTemplateCompatibilitySomeGenericComparable.clear()
        _issWithAny.clear()
        _issWithAnyMockTemplateCompatibilitySomeGenericComparable.clear()
        _pssWithMockTemplateCompatibilitySomeGeneric.clear()
        _pssWithMockTemplateCompatibilitySomeGenericMockTemplateCompatibilitySomeGeneric.clear()
        _xssWithAny.clear()
        _xssWithAnySequencesSequenceCharSequence.clear()
    }
}
