package mock.template.generic

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Char
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Int
import kotlin.IntArray
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.sequences.Sequence
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonMock<K : Any, L>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common<K, L>? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Common<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.executeOnGet()
        set(`value`) = _template.executeOnSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.generic.CommonMock#_template", collector =
        collector, freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_fooWithVoid", collector =
        collector, freeze = freeze)

    public val _fooWithZTAny: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_fooWithZTAny", collector =
        collector, freeze = freeze)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blaWithVoid", collector =
        collector, freeze = freeze)

    public val _blaWithTInt: KMockContract.SyncFunProxy<Unit, (Int) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blaWithTInt", collector =
        collector, freeze = freeze)

    public val _blaWithTInts: KMockContract.SyncFunProxy<Unit, (IntArray) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blaWithTInts", collector =
        collector, freeze = freeze)

    public val _lolWithArray: KMockContract.SyncFunProxy<Any, (Array<Any?>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lolWithArray", collector =
        collector, freeze = freeze)

    public val _lolWithZTAny: KMockContract.SyncFunProxy<Array<Any?>, (Any?) -> Array<Any?>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lolWithZTAny", collector =
        collector, freeze = freeze)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<List<Array<String>>, () -> List<Array<String>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_barWithVoid", collector =
        collector, freeze = freeze)

    public val _barWithTList: KMockContract.SyncFunProxy<Unit, (List<Array<String>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_barWithTList", collector =
        collector, freeze = freeze)

    public val _barWithTLists:
        KMockContract.SyncFunProxy<Unit, (Array<out List<Array<String>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_barWithTLists", collector =
        collector, freeze = freeze)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<List<Array<String?>>, () -> List<Array<String?>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blubbWithVoid", collector =
        collector, freeze = freeze)

    public val _blubbWithTList: KMockContract.SyncFunProxy<Unit, (List<Array<String?>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blubbWithTList", collector
        = collector, freeze = freeze)

    public val _blubbWithTLists:
        KMockContract.SyncFunProxy<Unit, (Array<out List<Array<String?>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blubbWithTLists", collector
        = collector, freeze = freeze)

    public val _bussWithVoid: KMockContract.SyncFunProxy<List<Array<Int>>?, () -> List<Array<Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bussWithVoid", collector =
        collector, freeze = freeze)

    public val _bussWithZTList: KMockContract.SyncFunProxy<Unit, (List<Array<Int>>?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bussWithZTList", collector
        = collector, freeze = freeze)

    public val _bussWithZTLists:
        KMockContract.SyncFunProxy<Unit, (Array<out List<Array<Int>>?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bussWithZTLists", collector
        = collector, freeze = freeze)

    public val _bossWithVoid: KMockContract.SyncFunProxy<List<Array<Int>?>, () -> List<Array<Int>?>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bossWithVoid", collector =
        collector, freeze = freeze)

    public val _bossWithTList: KMockContract.SyncFunProxy<Unit, (List<Array<Int>?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bossWithTList", collector =
        collector, freeze = freeze)

    public val _bossWithTLists:
        KMockContract.SyncFunProxy<Unit, (Array<out List<Array<Int>?>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_bossWithTLists", collector
        = collector, freeze = freeze)

    public val _buzzWithVoid: KMockContract.SyncFunProxy<List<Array<Int>>?, () -> List<Array<Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_buzzWithVoid", collector =
        collector, freeze = freeze)

    public val _buzzWithTList: KMockContract.SyncFunProxy<Unit, (List<Array<Int>>?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_buzzWithTList", collector =
        collector, freeze = freeze)

    public val _buzzWithTLists:
        KMockContract.SyncFunProxy<Unit, (Array<out List<Array<Int>>?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_buzzWithTLists", collector
        = collector, freeze = freeze)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ozzWithVoid", collector =
        collector, freeze = freeze)

    public val _ozzWithTL: KMockContract.SyncFunProxy<Unit, (L) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ozzWithTL", collector =
        collector, freeze = freeze)

    public val _ozzWithTLs: KMockContract.SyncFunProxy<Unit, (Array<out L>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ozzWithTLs", collector =
        collector, freeze = freeze)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<Comparable<List<Array<Any>>>, () -> Comparable<List<Array<Any>>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_brassWithVoid", collector =
        collector, freeze = freeze)

    public val _brassWithTComparable:
        KMockContract.SyncFunProxy<Unit, (Comparable<List<Array<Any>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_brassWithTComparable",
            collector = collector, freeze = freeze)

    public val _brassWithTComparables:
        KMockContract.SyncFunProxy<Unit, (Array<out Comparable<List<Array<Any>>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_brassWithTComparables",
            collector = collector, freeze = freeze)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<Comparable<List<Array<Any?>>>?, () -> Comparable<List<Array<Any?>>>?>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blissWithVoid", collector
    = collector, freeze = freeze)

    public val _blissWithZTComparable:
        KMockContract.SyncFunProxy<Unit, (Comparable<List<Array<Any?>>>?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blissWithZTComparable",
            collector = collector, freeze = freeze)

    public val _blissWithZTComparables:
        KMockContract.SyncFunProxy<Unit, (Array<out Comparable<List<Array<Any?>>>?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_blissWithZTComparables",
            collector = collector, freeze = freeze)

    public val _lossWithVoid:
        KMockContract.SyncFunProxy<Map<String, String>, () -> Map<String, String>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lossWithVoid", collector =
        collector, freeze = freeze)

    public val _lossWithTMap: KMockContract.SyncFunProxy<Unit, (Map<String, String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lossWithTMap", collector =
        collector, freeze = freeze)

    public val _lossWithTMaps:
        KMockContract.SyncFunProxy<Unit, (Array<out Map<String, String>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lossWithTMaps", collector =
        collector, freeze = freeze)

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_uzzWithVoid", collector =
        collector, freeze = freeze)

    public val _uzzWithTSomeGenericTList: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_uzzWithTSomeGenericTList",
            collector = collector, freeze = freeze)

    public val _uzzWithTSomeGenericTLists: KMockContract.SyncFunProxy<Unit, (Array<out Any>) -> Unit>
        =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_uzzWithTSomeGenericTLists",
            collector = collector, freeze = freeze)

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lzzWithVoid", collector =
        collector, freeze = freeze)

    public val _lzzWithTSomeGenericTList: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lzzWithTSomeGenericTList",
            collector = collector, freeze = freeze)

    public val _lzzWithTSomeGenericTLists: KMockContract.SyncFunProxy<Unit, (Array<out Any>) -> Unit>
        =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_lzzWithTSomeGenericTLists",
            collector = collector, freeze = freeze)

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_tzzWithVoid", collector =
        collector, freeze = freeze)

    public val _tzzWithZTSomeGenericZTList: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_tzzWithZTSomeGenericZTList",
            collector = collector, freeze = freeze)

    public val _tzzWithZTSomeGenericZTLists: KMockContract.SyncFunProxy<Unit, (Array<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_tzzWithZTSomeGenericZTLists",
            collector = collector, freeze = freeze)

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_rzzWithVoid", collector =
        collector, freeze = freeze)

    public val _rzzWithTSomeGenericTMap: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_rzzWithTSomeGenericTMap",
            collector = collector, freeze = freeze)

    public val _rzzWithTSomeGenericTMaps: KMockContract.SyncFunProxy<Unit, (Array<out Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_rzzWithTSomeGenericTMaps",
            collector = collector, freeze = freeze)

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_izzWithVoid", collector =
        collector, freeze = freeze)

    public val _izzWithTSomeGenericTComparable: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_izzWithTSomeGenericTComparable",
            collector = collector, freeze = freeze)

    public val _izzWithTSomeGenericTComparables:
        KMockContract.SyncFunProxy<Unit, (Array<out Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_izzWithTSomeGenericTComparables",
            collector = collector, freeze = freeze)

    public val _ossWithZTAny: KMockContract.SyncFunProxy<Any?, (Any?) -> Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ossWithZTAny", collector =
        collector, freeze = freeze)

    public val _ossWithZTAnyZRAny: KMockContract.SyncFunProxy<Unit, (Any?, Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ossWithZTAnyZRAny",
            collector = collector, freeze = freeze)

    public val _ossWithZRAnyZTAnys: KMockContract.SyncFunProxy<Unit, (Any?, Array<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_ossWithZRAnyZTAnys",
            collector = collector, freeze = freeze)

    public val _kssWithTSomeGenericTComparable: KMockContract.SyncFunProxy<Any, (Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_kssWithTSomeGenericTComparable",
            collector = collector, freeze = freeze)

    public val _kssWithTSomeGenericTComparableRSomeGenericRComparable:
        KMockContract.SyncFunProxy<Unit, (Any, Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_kssWithTSomeGenericTComparableRSomeGenericRComparable",
            collector = collector, freeze = freeze)

    public val _issWithZTAny: KMockContract.SyncFunProxy<Any, (Any?) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_issWithZTAny", collector =
        collector, freeze = freeze)

    public val _issWithZTAnyRSomeGenericRComparable: KMockContract.SyncFunProxy<Unit, (Any?,
        Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_issWithZTAnyRSomeGenericRComparable",
            collector = collector, freeze = freeze)

    public val _pssWithTSomeGeneric:
        KMockContract.SyncFunProxy<SomeGeneric<String>, (SomeGeneric<String>) -> SomeGeneric<String>>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_pssWithTSomeGeneric",
        collector = collector, freeze = freeze)

    public val _pssWithTSomeGenericRSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (SomeGeneric<String>, SomeGeneric<String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_pssWithTSomeGenericRSomeGeneric",
            collector = collector, freeze = freeze)

    public val _xssWithZTAny: KMockContract.SyncFunProxy<Any, (Any?) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_xssWithZTAny", collector =
        collector, freeze = freeze)

    public val _xssWithZTAnyRSequenceRCharSequence: KMockContract.SyncFunProxy<Unit, (Any?,
        Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_xssWithZTAnyRSequenceRCharSequence",
            collector = collector, freeze = freeze)

    public val _rrrWithTListRSequence: KMockContract.SyncFunProxy<Unit, (List<Any>,
        Sequence<List<Any>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_rrrWithTListRSequence",
            collector = collector, freeze = freeze)

    public val _rrrWithRSequenceTList: KMockContract.SyncFunProxy<Unit, (Sequence<List<Any>>,
        List<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_rrrWithRSequenceTList",
            collector = collector, freeze = freeze)

    public val _trrWithTKString: KMockContract.SyncFunProxy<Unit, (K, String) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_trrWithTKString", collector
        = collector, freeze = freeze)

    public val _trrWithTCharSequenceTComparableKCharSequenceKComparable:
        KMockContract.SyncFunProxy<Unit, (Any, Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.CommonMock#_trrWithTCharSequenceTComparableKCharSequenceKComparable",
            collector = collector, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithZTAny.invoke(payload) {
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

    public override fun <T> lol(fuzz: Array<T>): Any = _lolWithArray.invoke(fuzz)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lol(fuzz: T): Array<T> = _lolWithZTAny.invoke(fuzz) as kotlin.Array<T>

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
        _bussWithZTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : List<Array<Int>>?> buss(vararg payload: T): Unit =
        _bussWithZTLists.invoke(payload) {
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
        _uzzWithTSomeGenericTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> uzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    List<String> = _uzzWithTSomeGenericTLists.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithVoid.invoke() as T

    public override fun <T> lzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithTSomeGenericTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> lzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    List<String>? = _lzzWithTSomeGenericTLists.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithVoid.invoke() as T

    public override fun <T> tzz(payload: T): Unit where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithZTSomeGenericZTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> tzz(vararg payload: T): Unit where T : SomeGeneric<String>?, T :
    List<String>? = _tzzWithZTSomeGenericZTLists.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzzWithVoid.invoke() as T

    public override fun <T> rzz(payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzzWithTSomeGenericTMap.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> rzz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzzWithTSomeGenericTMaps.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izzWithVoid.invoke() as T

    public override fun <T> izz(payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izzWithTSomeGenericTComparable.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> izz(vararg payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izzWithTSomeGenericTComparables.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithZTAny.invoke(arg0) as R

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithZTAnyZRAny.invoke(arg0, arg1)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : R, R> oss(arg0: R, vararg arg1: T): Unit =
        _ossWithZRAnyZTAnys.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kssWithTSomeGenericTComparable.invoke(arg0) as R

    public override fun <T : R, R> kss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        _kssWithTSomeGenericTComparableRSomeGenericRComparable.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithZTAny.invoke(arg0) as R

    public override fun <R, T> iss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithZTAnyRSomeGenericRComparable.invoke(arg0, arg1) {
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
        _xssWithZTAny.invoke(arg0) as R

    public override fun <R, T> xss(arg0: T, arg1: R): Unit where R : Sequence<Char>, R : CharSequence
        = _xssWithZTAnyRSequenceRCharSequence.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <R : Sequence<T>, T : List<R>> rrr(arg0: T, arg1: R): Unit =
        _rrrWithTListRSequence.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <R : Sequence<T>, T : List<R>> rrr(arg0: R, arg1: T): Unit =
        _rrrWithRSequenceTList.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : K> trr(arg0: T, arg1: String): Unit = _trrWithTKString.invoke(arg0, arg1)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : K, K> trr(arg0: T, arg1: K): Unit where K : CharSequence, K :
    Comparable<K>? = _trrWithTCharSequenceTComparableKCharSequenceKComparable.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock() {
        _template.clear()
        _fooWithVoid.clear()
        _fooWithZTAny.clear()
        _blaWithVoid.clear()
        _blaWithTInt.clear()
        _blaWithTInts.clear()
        _lolWithArray.clear()
        _lolWithZTAny.clear()
        _barWithVoid.clear()
        _barWithTList.clear()
        _barWithTLists.clear()
        _blubbWithVoid.clear()
        _blubbWithTList.clear()
        _blubbWithTLists.clear()
        _bussWithVoid.clear()
        _bussWithZTList.clear()
        _bussWithZTLists.clear()
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
        _blissWithZTComparable.clear()
        _blissWithZTComparables.clear()
        _lossWithVoid.clear()
        _lossWithTMap.clear()
        _lossWithTMaps.clear()
        _uzzWithVoid.clear()
        _uzzWithTSomeGenericTList.clear()
        _uzzWithTSomeGenericTLists.clear()
        _lzzWithVoid.clear()
        _lzzWithTSomeGenericTList.clear()
        _lzzWithTSomeGenericTLists.clear()
        _tzzWithVoid.clear()
        _tzzWithZTSomeGenericZTList.clear()
        _tzzWithZTSomeGenericZTLists.clear()
        _rzzWithVoid.clear()
        _rzzWithTSomeGenericTMap.clear()
        _rzzWithTSomeGenericTMaps.clear()
        _izzWithVoid.clear()
        _izzWithTSomeGenericTComparable.clear()
        _izzWithTSomeGenericTComparables.clear()
        _ossWithZTAny.clear()
        _ossWithZTAnyZRAny.clear()
        _ossWithZRAnyZTAnys.clear()
        _kssWithTSomeGenericTComparable.clear()
        _kssWithTSomeGenericTComparableRSomeGenericRComparable.clear()
        _issWithZTAny.clear()
        _issWithZTAnyRSomeGenericRComparable.clear()
        _pssWithTSomeGeneric.clear()
        _pssWithTSomeGenericRSomeGeneric.clear()
        _xssWithZTAny.clear()
        _xssWithZTAnyRSequenceRCharSequence.clear()
        _rrrWithTListRSequence.clear()
        _rrrWithRSequenceTList.clear()
        _trrWithTKString.clear()
        _trrWithTCharSequenceTComparableKCharSequenceKComparable.clear()
    }
}
