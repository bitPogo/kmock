package mock.template.generic

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
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false,
) : Platform<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.generic.PlatformMock#_template", collector =
        verifier, freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_fooWithVoid", collector =
        verifier, freeze = freeze)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_fooWithAny", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _fooWithAnys: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) -> kotlin.Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_fooWithAnys", collector
    = verifier, freeze = freeze) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blaWithVoid", collector =
        verifier, freeze = freeze)

    public val _blaWithInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blaWithInt", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blaWithInts: KMockContract.SyncFunProxy<Unit, (kotlin.IntArray) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blaWithInts", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_barWithVoid", collector =
        verifier, freeze = freeze)

    public val _barWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_barWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _barWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_barWithCollectionsLists",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blubbWithVoid", collector
        = verifier, freeze = freeze)

    public val _blubbWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blubbWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blubbWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blubbWithCollectionsLists",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_bussWithVoid", collector
        = verifier, freeze = freeze)

    public val _bussWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_bussWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _bussWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_bussWithCollectionsLists",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_bossWithVoid", collector
        = verifier, freeze = freeze)

    public val _bossWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_bossWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _bossWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_bossWithCollectionsLists",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_buzzWithVoid", collector
        = verifier, freeze = freeze)

    public val _buzzWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_buzzWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _buzzWithCollectionsLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_buzzWithCollectionsLists",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_ozzWithVoid", collector =
        verifier, freeze = freeze)

    public val _ozzWithL: KMockContract.SyncFunProxy<Unit, (L) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_ozzWithL", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _ozzWithLs: KMockContract.SyncFunProxy<Unit, (Array<out L>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_ozzWithLs", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_brassWithVoid", collector
        = verifier, freeze = freeze)

    public val _brassWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_brassWithComparable",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _brassWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_brassWithComparables",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blissWithVoid", collector
        = verifier, freeze = freeze)

    public val _blissWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blissWithComparable",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blissWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blissWithComparables",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lossWithVoid", collector
        = verifier, freeze = freeze)

    public val _lossWithCollectionsMap:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String, kotlin.String>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lossWithCollectionsMap",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _lossWithCollectionsMaps: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lossWithCollectionsMaps",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_uzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _uzzWithMockTemplateGenericSomeGenericCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_uzzWithMockTemplateGenericSomeGenericCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _uzzWithMockTemplateGenericSomeGenericCollectionsLists:
        KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_uzzWithMockTemplateGenericSomeGenericCollectionsLists",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _lzzWithMockTemplateGenericSomeGenericCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lzzWithMockTemplateGenericSomeGenericCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _lzzWithMockTemplateGenericSomeGenericCollectionsLists:
        KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lzzWithMockTemplateGenericSomeGenericCollectionsLists",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_tzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _tzzWithMockTemplateGenericSomeGenericCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_tzzWithMockTemplateGenericSomeGenericCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _tzzWithMockTemplateGenericSomeGenericCollectionsLists:
        KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_tzzWithMockTemplateGenericSomeGenericCollectionsLists",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_rzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _rzzWithMockTemplateGenericSomeGenericCollectionsMap:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_rzzWithMockTemplateGenericSomeGenericCollectionsMap",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _rzzWithMockTemplateGenericSomeGenericCollectionsMaps:
        KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_rzzWithMockTemplateGenericSomeGenericCollectionsMaps",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_izzWithVoid", collector =
        verifier, freeze = freeze)

    public val _izzWithMockTemplateGenericSomeGenericComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_izzWithMockTemplateGenericSomeGenericComparable",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _izzWithMockTemplateGenericSomeGenericComparables:
        KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_izzWithMockTemplateGenericSomeGenericComparables",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _ossWithAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_ossWithAny", collector =
        verifier, freeze = freeze)

    public val _ossWithAnyAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_ossWithAnyAny", collector
        = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _ossWithAnyAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, Array<out
    kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_ossWithAnyAnys",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _kssWithMockTemplateGenericSomeGenericComparable:
        KMockContract.SyncFunProxy<Any, (kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_kssWithMockTemplateGenericSomeGenericComparable",
            collector = verifier, freeze = freeze)

    public
    val _kssWithMockTemplateGenericSomeGenericComparableMockTemplateGenericSomeGenericComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_kssWithMockTemplateGenericSomeGenericComparableMockTemplateGenericSomeGenericComparable",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _issWithAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_issWithAny", collector =
        verifier, freeze = freeze)

    public val _issWithAnyMockTemplateGenericSomeGenericComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_issWithAnyMockTemplateGenericSomeGenericComparable",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _pssWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<mock.template.generic.SomeGeneric<kotlin.String>, (mock.template.generic.SomeGeneric<kotlin.String>) ->
        mock.template.generic.SomeGeneric<kotlin.String>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_pssWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze)

    public val _pssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (mock.template.generic.SomeGeneric<kotlin.String>,
            mock.template.generic.SomeGeneric<kotlin.String>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_pssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _xssWithAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_xssWithAny", collector =
        verifier, freeze = freeze)

    public val _xssWithAnySequencesSequenceCharSequence:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_xssWithAnySequencesSequenceCharSequence",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithAny.invoke(payload)

    public override fun <T> foo(vararg payload: T): Unit = _fooWithAnys.invoke(payload)

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

    public override fun <T : List<Array<Int>>> buzz(payload: T?): Unit =
        _buzzWithCollectionsList.invoke(payload)

    public override fun <T : List<Array<Int>>> buzz(vararg payload: T?): Unit =
        _buzzWithCollectionsLists.invoke(payload)

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
        _uzzWithMockTemplateGenericSomeGenericCollectionsList.invoke(payload)

    public override fun <T> uzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    List<String> = _uzzWithMockTemplateGenericSomeGenericCollectionsLists.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithVoid.invoke() as T

    public override fun <T> lzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithMockTemplateGenericSomeGenericCollectionsList.invoke(payload)

    public override fun <T> lzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    List<String>? = _lzzWithMockTemplateGenericSomeGenericCollectionsLists.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithVoid.invoke() as T

    public override fun <T> tzz(payload: T): Unit where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithMockTemplateGenericSomeGenericCollectionsList.invoke(payload)

    public override fun <T> tzz(vararg payload: T): Unit where T : SomeGeneric<String>?, T :
    List<String>? = _tzzWithMockTemplateGenericSomeGenericCollectionsLists.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzzWithVoid.invoke() as T

    public override fun <T> rzz(payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzzWithMockTemplateGenericSomeGenericCollectionsMap.invoke(payload)

    public override fun <T> rzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzzWithMockTemplateGenericSomeGenericCollectionsMaps.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izzWithVoid.invoke() as T

    public override fun <T> izz(payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izzWithMockTemplateGenericSomeGenericComparable.invoke(payload)

    public override fun <T> izz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izzWithMockTemplateGenericSomeGenericComparables.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithAny.invoke(arg0) as R

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithAnyAny.invoke(arg0, arg1)

    public override fun <T : R, R> oss(arg0: R, vararg arg1: T): Unit = _ossWithAnyAnys.invoke(arg0,
        arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kssWithMockTemplateGenericSomeGenericComparable.invoke(arg0) as
        R

    public override fun <T : R, R> kss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        _kssWithMockTemplateGenericSomeGenericComparableMockTemplateGenericSomeGenericComparable.invoke(arg0,
            arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithAny.invoke(arg0) as R

    public override fun <R, T> iss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithAnyMockTemplateGenericSomeGenericComparable.invoke(arg0,
        arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <R : T, T : X, X : SomeGeneric<String>> pss(arg0: T): R =
        _pssWithMockTemplateGenericSomeGeneric.invoke(arg0) as R

    public override fun <R : T, T : X, X : SomeGeneric<String>> pss(arg0: T, arg1: R): Unit =
        _pssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> xss(arg0: T): R where R : Sequence<Char>, R : CharSequence =
        _xssWithAny.invoke(arg0) as R

    public override fun <R, T> xss(arg0: T, arg1: R): Unit where R : Sequence<Char>, R : CharSequence
        = _xssWithAnySequencesSequenceCharSequence.invoke(arg0, arg1)

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
        _uzzWithMockTemplateGenericSomeGenericCollectionsList.clear()
        _uzzWithMockTemplateGenericSomeGenericCollectionsLists.clear()
        _lzzWithVoid.clear()
        _lzzWithMockTemplateGenericSomeGenericCollectionsList.clear()
        _lzzWithMockTemplateGenericSomeGenericCollectionsLists.clear()
        _tzzWithVoid.clear()
        _tzzWithMockTemplateGenericSomeGenericCollectionsList.clear()
        _tzzWithMockTemplateGenericSomeGenericCollectionsLists.clear()
        _rzzWithVoid.clear()
        _rzzWithMockTemplateGenericSomeGenericCollectionsMap.clear()
        _rzzWithMockTemplateGenericSomeGenericCollectionsMaps.clear()
        _izzWithVoid.clear()
        _izzWithMockTemplateGenericSomeGenericComparable.clear()
        _izzWithMockTemplateGenericSomeGenericComparables.clear()
        _ossWithAny.clear()
        _ossWithAnyAny.clear()
        _ossWithAnyAnys.clear()
        _kssWithMockTemplateGenericSomeGenericComparable.clear()
        _kssWithMockTemplateGenericSomeGenericComparableMockTemplateGenericSomeGenericComparable.clear()
        _issWithAny.clear()
        _issWithAnyMockTemplateGenericSomeGenericComparable.clear()
        _pssWithMockTemplateGenericSomeGeneric.clear()
        _pssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric.clear()
        _xssWithAny.clear()
        _xssWithAnySequencesSequenceCharSequence.clear()
    }
}
