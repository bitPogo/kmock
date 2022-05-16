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

internal class CommonMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
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

    public val _fooWithTAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_fooWithTAny", collector =
        verifier, freeze = freeze)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blaWithVoid", collector =
        verifier, freeze = freeze)

    public val _blaWithTInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blaWithTInt", collector =
        verifier, freeze = freeze)

    public val _blaWithTInts: KMockContract.SyncFunProxy<Unit, (kotlin.IntArray) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blaWithTInts", collector =
        verifier, freeze = freeze)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_barWithVoid", collector =
        verifier, freeze = freeze)

    public val _barWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_barWithTList", collector =
        verifier, freeze = freeze)

    public val _barWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_barWithTLists", collector =
        verifier, freeze = freeze)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blubbWithVoid", collector =
        verifier, freeze = freeze)

    public val _blubbWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blubbWithTList", collector
        = verifier, freeze = freeze)

    public val _blubbWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blubbWithTLists", collector
        = verifier, freeze = freeze)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bussWithVoid", collector =
        verifier, freeze = freeze)

    public val _bussWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bussWithTList", collector =
        verifier, freeze = freeze)

    public val _bussWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bussWithTLists", collector
        = verifier, freeze = freeze)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bossWithVoid", collector =
        verifier, freeze = freeze)

    public val _bossWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bossWithTList", collector =
        verifier, freeze = freeze)

    public val _bossWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bossWithTLists", collector
        = verifier, freeze = freeze)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_buzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _buzzWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_buzzWithTList", collector =
        verifier, freeze = freeze)

    public val _buzzWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_buzzWithTLists", collector
        = verifier, freeze = freeze)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ozzWithVoid", collector =
        verifier, freeze = freeze)

    public val _ozzWithTL: KMockContract.SyncFunProxy<Unit, (L) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ozzWithTL", collector =
        verifier, freeze = freeze)

    public val _ozzWithTLs: KMockContract.SyncFunProxy<Unit, (Array<out L>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ozzWithTLs", collector =
        verifier, freeze = freeze)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_brassWithVoid", collector =
        verifier, freeze = freeze)

    public val _brassWithTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_brassWithTComparable",
            collector = verifier, freeze = freeze)

    public val _brassWithTComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_brassWithTComparables",
            collector = verifier, freeze = freeze)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blissWithVoid", collector =
        verifier, freeze = freeze)

    public val _blissWithTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blissWithTComparable",
            collector = verifier, freeze = freeze)

    public val _blissWithTComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blissWithTComparables",
            collector = verifier, freeze = freeze)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lossWithVoid", collector =
        verifier, freeze = freeze)

    public val _lossWithTMap: KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String,
        kotlin.String>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lossWithTMap", collector =
        verifier, freeze = freeze)

    public val _lossWithTMaps: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lossWithTMaps", collector =
        verifier, freeze = freeze)

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_uzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _uzzWithTSomeGenericList: KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_uzzWithTSomeGenericList",
        collector = verifier, freeze = freeze)

    public val _uzzWithTSomeGenericLists: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_uzzWithTSomeGenericLists",
            collector = verifier, freeze = freeze)

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _lzzWithTSomeGenericList: KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lzzWithTSomeGenericList",
        collector = verifier, freeze = freeze)

    public val _lzzWithTSomeGenericLists: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lzzWithTSomeGenericLists",
            collector = verifier, freeze = freeze)

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_tzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _tzzWithTSomeGenericList: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_tzzWithTSomeGenericList",
            collector = verifier, freeze = freeze)

    public val _tzzWithTSomeGenericLists: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_tzzWithTSomeGenericLists",
            collector = verifier, freeze = freeze)

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_rzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _rzzWithTSomeGenericMap: KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_rzzWithTSomeGenericMap",
        collector = verifier, freeze = freeze)

    public val _rzzWithTSomeGenericMaps: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_rzzWithTSomeGenericMaps",
            collector = verifier, freeze = freeze)

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_izzWithVoid", collector =
        verifier, freeze = freeze)

    public val _izzWithTSomeGenericComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Any) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_izzWithTSomeGenericComparable",
            collector = verifier, freeze = freeze)

    public val _izzWithTSomeGenericComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_izzWithTSomeGenericComparables",
            collector = verifier, freeze = freeze)

    public val _ossWithTAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ossWithTAny", collector =
        verifier, freeze = freeze)

    public val _ossWithTAnyRAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ossWithTAnyRAny", collector
        = verifier, freeze = freeze)

    public val _ossWithRAnyTAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, Array<out
    kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ossWithRAnyTAnys",
            collector = verifier, freeze = freeze)

    public val _kssWithTSomeGenericComparable: KMockContract.SyncFunProxy<Any, (kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_kssWithTSomeGenericComparable",
            collector = verifier, freeze = freeze)

    public val _kssWithTSomeGenericComparableRSomeGenericComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_kssWithTSomeGenericComparableRSomeGenericComparable",
            collector = verifier, freeze = freeze)

    public val _issWithTAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_issWithTAny", collector =
        verifier, freeze = freeze)

    public val _issWithTAnyRSomeGenericComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Any?,
        kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_issWithTAnyRSomeGenericComparable",
            collector = verifier, freeze = freeze)

    public val _pssWithTSomeGeneric:
        KMockContract.SyncFunProxy<mock.template.generic.SomeGeneric<kotlin.String>, (mock.template.generic.SomeGeneric<kotlin.String>) ->
        mock.template.generic.SomeGeneric<kotlin.String>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_pssWithTSomeGeneric",
            collector = verifier, freeze = freeze)

    public val _pssWithTSomeGenericRSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (mock.template.generic.SomeGeneric<kotlin.String>,
            mock.template.generic.SomeGeneric<kotlin.String>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_pssWithTSomeGenericRSomeGeneric",
            collector = verifier, freeze = freeze)

    public val _xssWithTAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_xssWithTAny", collector =
        verifier, freeze = freeze)

    public val _xssWithTAnyRSequenceCharSequence: KMockContract.SyncFunProxy<Unit, (kotlin.Any?,
        kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_xssWithTAnyRSequenceCharSequence",
            collector = verifier, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithTAny.invoke(payload) {
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
        _barWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<String>>> bar(vararg payload: T): Unit =
        _barWithTLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String?>>> blubb(): T = _blubbWithVoid.invoke() as T

    public override fun <T : List<Array<String?>>> blubb(payload: T): Unit =
        _blubbWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<String?>>> blubb(vararg payload: T): Unit =
        _blubbWithTLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>?> buss(): T = _bussWithVoid.invoke() as T

    public override fun <T : List<Array<Int>>?> buss(payload: T): Unit =
        _bussWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<Int>>?> buss(vararg payload: T): Unit =
        _bussWithTLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>?>> boss(): T = _bossWithVoid.invoke() as T

    public override fun <T : List<Array<Int>?>> boss(payload: T): Unit =
        _bossWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<Int>?>> boss(vararg payload: T): Unit =
        _bossWithTLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>> buzz(): T? = _buzzWithVoid.invoke() as T?

    public override fun <T : List<Array<Int>>> buzz(payload: T?): Unit =
        _buzzWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<Int>>> buzz(vararg payload: T?): Unit =
        _buzzWithTLists.invoke(payload) {
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
        _blissWithTComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<List<Array<T>>>?> bliss(vararg payload: T): Unit =
        _blissWithTComparables.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Map<String, String>> loss(): T = _lossWithVoid.invoke() as T

    public override fun <T : Map<String, String>> loss(payload: T): Unit =
        _lossWithTMap.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Map<String, String>> loss(vararg payload: T): Unit =
        _lossWithTMaps.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String> =
        _uzzWithVoid.invoke() as T

    public override fun <T> uzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String> =
        _uzzWithTSomeGenericList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> uzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    List<String> = _uzzWithTSomeGenericLists.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithVoid.invoke() as T

    public override fun <T> lzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithTSomeGenericList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> lzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    List<String>? = _lzzWithTSomeGenericLists.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithVoid.invoke() as T

    public override fun <T> tzz(payload: T): Unit where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithTSomeGenericList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> tzz(vararg payload: T): Unit where T : SomeGeneric<String>?, T :
    List<String>? = _tzzWithTSomeGenericLists.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzzWithVoid.invoke() as T

    public override fun <T> rzz(payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzzWithTSomeGenericMap.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> rzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzzWithTSomeGenericMaps.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izzWithVoid.invoke() as T

    public override fun <T> izz(payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izzWithTSomeGenericComparable.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> izz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izzWithTSomeGenericComparables.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithTAny.invoke(arg0) as R

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithTAnyRAny.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : R, R> oss(arg0: R, vararg arg1: T): Unit = _ossWithRAnyTAnys.invoke(arg0,
        arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kssWithTSomeGenericComparable.invoke(arg0) as R

    public override fun <T : R, R> kss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kssWithTSomeGenericComparableRSomeGenericComparable.invoke(arg0,
        arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithTAny.invoke(arg0) as R

    public override fun <R, T> iss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithTAnyRSomeGenericComparable.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <R : T, T : X, X : SomeGeneric<String>> pss(arg0: T): R =
        _pssWithTSomeGeneric.invoke(arg0) as R

    public override fun <R : T, T : X, X : SomeGeneric<String>> pss(arg0: T, arg1: R): Unit =
        _pssWithTSomeGenericRSomeGeneric.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> xss(arg0: T): R where R : Sequence<Char>, R : CharSequence =
        _xssWithTAny.invoke(arg0) as R

    public override fun <R, T> xss(arg0: T, arg1: R): Unit where R : Sequence<Char>, R : CharSequence
        = _xssWithTAnyRSequenceCharSequence.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _template.clear()
        _fooWithVoid.clear()
        _fooWithTAny.clear()
        _blaWithVoid.clear()
        _blaWithTInt.clear()
        _blaWithTInts.clear()
        _barWithVoid.clear()
        _barWithTList.clear()
        _barWithTLists.clear()
        _blubbWithVoid.clear()
        _blubbWithTList.clear()
        _blubbWithTLists.clear()
        _bussWithVoid.clear()
        _bussWithTList.clear()
        _bussWithTLists.clear()
        _bossWithVoid.clear()
        _bossWithTList.clear()
        _bossWithTLists.clear()
        _buzzWithVoid.clear()
        _buzzWithTList.clear()
        _buzzWithTLists.clear()
        _ozzWithVoid.clear()
        _ozzWithTL.clear()
        _ozzWithTLs.clear()
        _brassWithVoid.clear()
        _brassWithTComparable.clear()
        _brassWithTComparables.clear()
        _blissWithVoid.clear()
        _blissWithTComparable.clear()
        _blissWithTComparables.clear()
        _lossWithVoid.clear()
        _lossWithTMap.clear()
        _lossWithTMaps.clear()
        _uzzWithVoid.clear()
        _uzzWithTSomeGenericList.clear()
        _uzzWithTSomeGenericLists.clear()
        _lzzWithVoid.clear()
        _lzzWithTSomeGenericList.clear()
        _lzzWithTSomeGenericLists.clear()
        _tzzWithVoid.clear()
        _tzzWithTSomeGenericList.clear()
        _tzzWithTSomeGenericLists.clear()
        _rzzWithVoid.clear()
        _rzzWithTSomeGenericMap.clear()
        _rzzWithTSomeGenericMaps.clear()
        _izzWithVoid.clear()
        _izzWithTSomeGenericComparable.clear()
        _izzWithTSomeGenericComparables.clear()
        _ossWithTAny.clear()
        _ossWithTAnyRAny.clear()
        _ossWithRAnyTAnys.clear()
        _kssWithTSomeGenericComparable.clear()
        _kssWithTSomeGenericComparableRSomeGenericComparable.clear()
        _issWithTAny.clear()
        _issWithTAnyRSomeGenericComparable.clear()
        _pssWithTSomeGeneric.clear()
        _pssWithTSomeGenericRSomeGeneric.clear()
        _xssWithTAny.clear()
        _xssWithTAnyRSequenceCharSequence.clear()
    }
}
