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
    collector: KMockContract.Collector = NoopCollector,
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
            collector = collector, freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_fooWithVoid",
            collector = collector, freeze = freeze)

    public val _fooWithZTAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_fooWithZTAny",
            collector = collector, freeze = freeze)

    public val _fooWithZTAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out kotlin.Any?>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_fooWithZTAnys",
            collector = collector, freeze = freeze)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blaWithVoid",
            collector = collector, freeze = freeze)

    public val _blaWithTInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blaWithTInt",
            collector = collector, freeze = freeze)

    public val _blaWithTInts: KMockContract.SyncFunProxy<Unit, (kotlin.IntArray) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blaWithTInts",
            collector = collector, freeze = freeze)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_barWithVoid",
            collector = collector, freeze = freeze)

    public val _barWithTCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_barWithTCollectionsList",
            collector = collector, freeze = freeze)

    public val _barWithTCollectionsLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_barWithTCollectionsLists",
            collector = collector, freeze = freeze)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blubbWithVoid",
            collector = collector, freeze = freeze)

    public val _blubbWithTCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blubbWithTCollectionsList",
            collector = collector, freeze = freeze)

    public val _blubbWithTCollectionsLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blubbWithTCollectionsLists",
            collector = collector, freeze = freeze)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_bussWithVoid",
            collector = collector, freeze = freeze)

    public val _bussWithZTCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_bussWithZTCollectionsList",
            collector = collector, freeze = freeze)

    public val _bussWithZTCollectionsLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_bussWithZTCollectionsLists",
            collector = collector, freeze = freeze)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_bossWithVoid",
            collector = collector, freeze = freeze)

    public val _bossWithTCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_bossWithTCollectionsList",
            collector = collector, freeze = freeze)

    public val _bossWithTCollectionsLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_bossWithTCollectionsLists",
            collector = collector, freeze = freeze)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_buzzWithVoid",
            collector = collector, freeze = freeze)

    public val _buzzWithTCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_buzzWithTCollectionsList",
            collector = collector, freeze = freeze)

    public val _buzzWithTCollectionsLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_buzzWithTCollectionsLists",
            collector = collector, freeze = freeze)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_ozzWithVoid",
            collector = collector, freeze = freeze)

    public val _ozzWithTL: KMockContract.SyncFunProxy<Unit, (L) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_ozzWithTL",
            collector = collector, freeze = freeze)

    public val _ozzWithTLs: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out L>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_ozzWithTLs",
            collector = collector, freeze = freeze)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_brassWithVoid",
            collector = collector, freeze = freeze)

    public val _brassWithTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_brassWithTComparable",
            collector = collector, freeze = freeze)

    public val _brassWithTComparables: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_brassWithTComparables",
            collector = collector, freeze = freeze)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blissWithVoid",
            collector = collector, freeze = freeze)

    public val _blissWithZTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blissWithZTComparable",
            collector = collector, freeze = freeze)

    public val _blissWithZTComparables: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_blissWithZTComparables",
            collector = collector, freeze = freeze)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_lossWithVoid",
            collector = collector, freeze = freeze)

    public val _lossWithTCollectionsMap:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String, kotlin.String>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_lossWithTCollectionsMap",
            collector = collector, freeze = freeze)

    public val _lossWithTCollectionsMaps: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_lossWithTCollectionsMaps",
            collector = collector, freeze = freeze)

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_uzzWithVoid",
            collector = collector, freeze = freeze)

    public val _uzzWithTMockTemplateCompatibilitySomeGenericCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_uzzWithTMockTemplateCompatibilitySomeGenericCollectionsList",
            collector = collector, freeze = freeze)

    public val _uzzWithTMockTemplateCompatibilitySomeGenericCollectionsLists:
        KMockContract.SyncFunProxy<Unit, (kotlin.Array<out kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_uzzWithTMockTemplateCompatibilitySomeGenericCollectionsLists",
            collector = collector, freeze = freeze)

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_lzzWithVoid",
            collector = collector, freeze = freeze)

    public val _lzzWithTMockTemplateCompatibilitySomeGenericCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_lzzWithTMockTemplateCompatibilitySomeGenericCollectionsList",
            collector = collector, freeze = freeze)

    public val _lzzWithTMockTemplateCompatibilitySomeGenericCollectionsLists:
        KMockContract.SyncFunProxy<Unit, (kotlin.Array<out kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_lzzWithTMockTemplateCompatibilitySomeGenericCollectionsLists",
            collector = collector, freeze = freeze)

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_tzzWithVoid",
            collector = collector, freeze = freeze)

    public val _tzzWithZTMockTemplateCompatibilitySomeGenericCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_tzzWithZTMockTemplateCompatibilitySomeGenericCollectionsList",
            collector = collector, freeze = freeze)

    public val _tzzWithZTMockTemplateCompatibilitySomeGenericCollectionsLists:
        KMockContract.SyncFunProxy<Unit, (kotlin.Array<out kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_tzzWithZTMockTemplateCompatibilitySomeGenericCollectionsLists",
            collector = collector, freeze = freeze)

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_rzzWithVoid",
            collector = collector, freeze = freeze)

    public val _rzzWithTMockTemplateCompatibilitySomeGenericCollectionsMap:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_rzzWithTMockTemplateCompatibilitySomeGenericCollectionsMap",
            collector = collector, freeze = freeze)

    public val _rzzWithTMockTemplateCompatibilitySomeGenericCollectionsMaps:
        KMockContract.SyncFunProxy<Unit, (kotlin.Array<out kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_rzzWithTMockTemplateCompatibilitySomeGenericCollectionsMaps",
            collector = collector, freeze = freeze)

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_izzWithVoid",
            collector = collector, freeze = freeze)

    public val _izzWithTMockTemplateCompatibilitySomeGenericComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_izzWithTMockTemplateCompatibilitySomeGenericComparable",
            collector = collector, freeze = freeze)

    public val _izzWithTMockTemplateCompatibilitySomeGenericComparables:
        KMockContract.SyncFunProxy<Unit, (kotlin.Array<out kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_izzWithTMockTemplateCompatibilitySomeGenericComparables",
            collector = collector, freeze = freeze)

    public val _ossWithTAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_ossWithTAny",
            collector = collector, freeze = freeze)

    public val _ossWithTAnyZRAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_ossWithTAnyZRAny",
            collector = collector, freeze = freeze)

    public val _ossWithZRAnyTAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Array<out
    kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_ossWithZRAnyTAnys",
            collector = collector, freeze = freeze)

    public val _kssWithTMockTemplateCompatibilitySomeGenericComparable:
        KMockContract.SyncFunProxy<Any, (kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_kssWithTMockTemplateCompatibilitySomeGenericComparable",
            collector = collector, freeze = freeze)

    public
    val _kssWithTMockTemplateCompatibilitySomeGenericComparableRMockTemplateCompatibilitySomeGenericComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_kssWithTMockTemplateCompatibilitySomeGenericComparableRMockTemplateCompatibilitySomeGenericComparable",
            collector = collector, freeze = freeze)

    public val _issWithZTAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_issWithZTAny",
            collector = collector, freeze = freeze)

    public val _issWithZTAnyRMockTemplateCompatibilitySomeGenericComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_issWithZTAnyRMockTemplateCompatibilitySomeGenericComparable",
            collector = collector, freeze = freeze)

    public val _pssWithTMockTemplateCompatibilitySomeGeneric:
        KMockContract.SyncFunProxy<mock.template.compatibility.SomeGeneric<kotlin.String>, (mock.template.compatibility.SomeGeneric<kotlin.String>) ->
        mock.template.compatibility.SomeGeneric<kotlin.String>> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_pssWithTMockTemplateCompatibilitySomeGeneric",
            collector = collector, freeze = freeze)

    public val _pssWithTMockTemplateCompatibilitySomeGenericRMockTemplateCompatibilitySomeGeneric:
        KMockContract.SyncFunProxy<Unit, (mock.template.compatibility.SomeGeneric<kotlin.String>,
            mock.template.compatibility.SomeGeneric<kotlin.String>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_pssWithTMockTemplateCompatibilitySomeGenericRMockTemplateCompatibilitySomeGeneric",
            collector = collector, freeze = freeze)

    public val _xssWithZTAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_xssWithZTAny",
            collector = collector, freeze = freeze)

    public val _xssWithZTAnyRSequencesSequenceCharSequence:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.PlatformMock#_xssWithZTAnyRSequencesSequenceCharSequence",
            collector = collector, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithZTAny.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> foo(vararg payload: T): Unit = _fooWithZTAnys.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Int> bla(): T = _blaWithVoid.invoke() as T

    public override fun <T : Int> bla(payload: T): Unit = _blaWithTInt.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Int> bla(vararg payload: T): Unit = _blaWithTInts.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String>>> bar(): T = _barWithVoid.invoke() as T

    public override fun <T : List<Array<String>>> bar(payload: T): Unit =
        _barWithTCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<String>>> bar(vararg payload: T): Unit =
        _barWithTCollectionsLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String?>>> blubb(): T = _blubbWithVoid.invoke() as T

    public override fun <T : List<Array<String?>>> blubb(payload: T): Unit =
        _blubbWithTCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<String?>>> blubb(vararg payload: T): Unit =
        _blubbWithTCollectionsLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>?> buss(): T = _bussWithVoid.invoke() as T

    public override fun <T : List<Array<Int>>?> buss(payload: T): Unit =
        _bussWithZTCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<Int>>?> buss(vararg payload: T): Unit =
        _bussWithZTCollectionsLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>?>> boss(): T = _bossWithVoid.invoke() as T

    public override fun <T : List<Array<Int>?>> boss(payload: T): Unit =
        _bossWithTCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<Int>?>> boss(vararg payload: T): Unit =
        _bossWithTCollectionsLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>> buzz(): T? = _buzzWithVoid.invoke() as T?

    public override fun <T : List<Array<Int>>> buzz(payload: T?): Unit =
        _buzzWithTCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<Int>>> buzz(vararg payload: T?): Unit =
        _buzzWithTCollectionsLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : L> ozz(): T = _ozzWithVoid.invoke() as T

    public override fun <T : L> ozz(payload: T): Unit = _ozzWithTL.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : L> ozz(vararg payload: T): Unit = _ozzWithTLs.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>> brass(): T = _brassWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>> brass(payload: T): Unit =
        _brassWithTComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<List<Array<T>>>> brass(vararg payload: T): Unit =
        _brassWithTComparables.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>?> bliss(): T = _blissWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>?> bliss(payload: T): Unit =
        _blissWithZTComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<List<Array<T>>>?> bliss(vararg payload: T): Unit =
        _blissWithZTComparables.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Map<String, String>> loss(): T = _lossWithVoid.invoke() as T

    public override fun <T : Map<String, String>> loss(payload: T): Unit =
        _lossWithTCollectionsMap.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Map<String, String>> loss(vararg payload: T): Unit =
        _lossWithTCollectionsMaps.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String> =
        _uzzWithVoid.invoke() as T

    public override fun <T> uzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String> =
        _uzzWithTMockTemplateCompatibilitySomeGenericCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> uzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    List<String> = _uzzWithTMockTemplateCompatibilitySomeGenericCollectionsLists.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithVoid.invoke() as T

    public override fun <T> lzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithTMockTemplateCompatibilitySomeGenericCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> lzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    List<String>? = _lzzWithTMockTemplateCompatibilitySomeGenericCollectionsLists.invoke(payload)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithVoid.invoke() as T

    public override fun <T> tzz(payload: T): Unit where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithZTMockTemplateCompatibilitySomeGenericCollectionsList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> tzz(vararg payload: T): Unit where T : SomeGeneric<String>?, T :
    List<String>? = _tzzWithZTMockTemplateCompatibilitySomeGenericCollectionsLists.invoke(payload)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzzWithVoid.invoke() as T

    public override fun <T> rzz(payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> =
        _rzzWithTMockTemplateCompatibilitySomeGenericCollectionsMap.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> rzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> =
        _rzzWithTMockTemplateCompatibilitySomeGenericCollectionsMaps.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izzWithVoid.invoke() as T

    public override fun <T> izz(payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> =
        _izzWithTMockTemplateCompatibilitySomeGenericComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> izz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> =
        _izzWithTMockTemplateCompatibilitySomeGenericComparables.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithTAny.invoke(arg0) as R

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithTAnyZRAny.invoke(arg0, arg1)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : R, R> oss(arg0: R, vararg arg1: T): Unit =
        _ossWithZRAnyTAnys.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        _kssWithTMockTemplateCompatibilitySomeGenericComparable.invoke(arg0) as R

    public override fun <T : R, R> kss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        _kssWithTMockTemplateCompatibilitySomeGenericComparableRMockTemplateCompatibilitySomeGenericComparable.invoke(arg0,
            arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithZTAny.invoke(arg0) as R

    public override fun <R, T> iss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> =
        _issWithZTAnyRMockTemplateCompatibilitySomeGenericComparable.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <R : T, T : X, X : SomeGeneric<String>> pss(arg0: T): R =
        _pssWithTMockTemplateCompatibilitySomeGeneric.invoke(arg0) as R

    public override fun <R : T, T : X, X : SomeGeneric<String>> pss(arg0: T, arg1: R): Unit =
        _pssWithTMockTemplateCompatibilitySomeGenericRMockTemplateCompatibilitySomeGeneric.invoke(arg0,
            arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> xss(arg0: T): R where R : Sequence<Char>, R : CharSequence =
        _xssWithZTAny.invoke(arg0) as R

    public override fun <R, T> xss(arg0: T, arg1: R): Unit where R : Sequence<Char>, R : CharSequence
        = _xssWithZTAnyRSequencesSequenceCharSequence.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _template.clear()
        _fooWithVoid.clear()
        _fooWithZTAny.clear()
        _fooWithZTAnys.clear()
        _blaWithVoid.clear()
        _blaWithTInt.clear()
        _blaWithTInts.clear()
        _barWithVoid.clear()
        _barWithTCollectionsList.clear()
        _barWithTCollectionsLists.clear()
        _blubbWithVoid.clear()
        _blubbWithTCollectionsList.clear()
        _blubbWithTCollectionsLists.clear()
        _bussWithVoid.clear()
        _bussWithZTCollectionsList.clear()
        _bussWithZTCollectionsLists.clear()
        _bossWithVoid.clear()
        _bossWithTCollectionsList.clear()
        _bossWithTCollectionsLists.clear()
        _buzzWithVoid.clear()
        _buzzWithTCollectionsList.clear()
        _buzzWithTCollectionsLists.clear()
        _ozzWithVoid.clear()
        _ozzWithTL.clear()
        _ozzWithTLs.clear()
        _brassWithVoid.clear()
        _brassWithTComparable.clear()
        _brassWithTComparables.clear()
        _blissWithVoid.clear()
        _blissWithZTComparable.clear()
        _blissWithZTComparables.clear()
        _lossWithVoid.clear()
        _lossWithTCollectionsMap.clear()
        _lossWithTCollectionsMaps.clear()
        _uzzWithVoid.clear()
        _uzzWithTMockTemplateCompatibilitySomeGenericCollectionsList.clear()
        _uzzWithTMockTemplateCompatibilitySomeGenericCollectionsLists.clear()
        _lzzWithVoid.clear()
        _lzzWithTMockTemplateCompatibilitySomeGenericCollectionsList.clear()
        _lzzWithTMockTemplateCompatibilitySomeGenericCollectionsLists.clear()
        _tzzWithVoid.clear()
        _tzzWithZTMockTemplateCompatibilitySomeGenericCollectionsList.clear()
        _tzzWithZTMockTemplateCompatibilitySomeGenericCollectionsLists.clear()
        _rzzWithVoid.clear()
        _rzzWithTMockTemplateCompatibilitySomeGenericCollectionsMap.clear()
        _rzzWithTMockTemplateCompatibilitySomeGenericCollectionsMaps.clear()
        _izzWithVoid.clear()
        _izzWithTMockTemplateCompatibilitySomeGenericComparable.clear()
        _izzWithTMockTemplateCompatibilitySomeGenericComparables.clear()
        _ossWithTAny.clear()
        _ossWithTAnyZRAny.clear()
        _ossWithZRAnyTAnys.clear()
        _kssWithTMockTemplateCompatibilitySomeGenericComparable.clear()
        _kssWithTMockTemplateCompatibilitySomeGenericComparableRMockTemplateCompatibilitySomeGenericComparable.clear()
        _issWithZTAny.clear()
        _issWithZTAnyRMockTemplateCompatibilitySomeGenericComparable.clear()
        _pssWithTMockTemplateCompatibilitySomeGeneric.clear()
        _pssWithTMockTemplateCompatibilitySomeGenericRMockTemplateCompatibilitySomeGeneric.clear()
        _xssWithZTAny.clear()
        _xssWithZTAnyRSequencesSequenceCharSequence.clear()
    }
}
