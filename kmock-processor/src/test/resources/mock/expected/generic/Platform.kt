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
        ProxyFactory.createPropertyProxy("mock.template.generic.PlatformMock#_template", collector =
        collector, freeze = freeze)

    public val _fooWithAny: KMockContract.SyncFunProxy<String, (Any) -> String> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_fooWithAny", collector =
        collector, freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_fooWithVoid", collector =
        collector, freeze = freeze)

    public val _fooWithZTAny: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_fooWithZTAny", collector
        = collector, freeze = freeze)

    public val _fooWithZTAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_fooWithZTAnys", collector
        = collector, freeze = freeze)

    public val _lolWithArrays: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out kotlin.Array<out
    kotlin.Any>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lolWithArrays", collector
        = collector, freeze = freeze)

    public val _lolWithArray: KMockContract.SyncFunProxy<Any, (Array<Any?>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lolWithArray", collector
        = collector, freeze = freeze)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blaWithVoid", collector =
        collector, freeze = freeze)

    public val _blaWithTInt: KMockContract.SyncFunProxy<Unit, (Int) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blaWithTInt", collector =
        collector, freeze = freeze)

    public val _blaWithTInts: KMockContract.SyncFunProxy<Unit, (IntArray) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blaWithTInts", collector
        = collector, freeze = freeze)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () -> kotlin.collections.List<kotlin.Array<kotlin.String>>>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_barWithVoid", collector
    = collector, freeze = freeze)

    public val _barWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_barWithTList",
        collector = collector, freeze = freeze)

    public val _barWithTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_barWithTLists", collector
        = collector, freeze = freeze)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () -> kotlin.collections.List<kotlin.Array<kotlin.String?>>>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blubbWithVoid",
        collector = collector, freeze = freeze)

    public val _blubbWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blubbWithTList",
        collector = collector, freeze = freeze)

    public val _blubbWithTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blubbWithTLists",
            collector = collector, freeze = freeze)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_bussWithVoid",
        collector = collector, freeze = freeze)

    public val _bussWithZTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_bussWithZTList",
        collector = collector, freeze = freeze)

    public val _bussWithZTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_bussWithZTLists",
            collector = collector, freeze = freeze)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () -> kotlin.collections.List<kotlin.Array<kotlin.Int>?>>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_bossWithVoid",
        collector = collector, freeze = freeze)

    public val _bossWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_bossWithTList",
        collector = collector, freeze = freeze)

    public val _bossWithTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_bossWithTLists",
            collector = collector, freeze = freeze)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_buzzWithVoid",
        collector = collector, freeze = freeze)

    public val _buzzWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_buzzWithTList",
        collector = collector, freeze = freeze)

    public val _buzzWithTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_buzzWithTLists",
            collector = collector, freeze = freeze)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_ozzWithVoid", collector =
        collector, freeze = freeze)

    public val _ozzWithTL: KMockContract.SyncFunProxy<Unit, (L) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_ozzWithTL", collector =
        collector, freeze = freeze)

    public val _ozzWithTLs: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out L>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_ozzWithTLs", collector =
        collector, freeze = freeze)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>, () -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_brassWithVoid",
        collector = collector, freeze = freeze)

    public val _brassWithTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_brassWithTComparable",
        collector = collector, freeze = freeze)

    public val _brassWithTComparables: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_brassWithTComparables",
            collector = collector, freeze = freeze)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?, () -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blissWithVoid",
        collector = collector, freeze = freeze)

    public val _blissWithZTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blissWithZTComparable",
        collector = collector, freeze = freeze)

    public val _blissWithZTComparables: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_blissWithZTComparables",
            collector = collector, freeze = freeze)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lossWithVoid", collector
        = collector, freeze = freeze)

    public val _lossWithTMap: KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String,
        kotlin.String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lossWithTMap", collector
        = collector, freeze = freeze)

    public val _lossWithTMaps: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.Map<kotlin.String, kotlin.String>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lossWithTMaps", collector
        = collector, freeze = freeze)

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_uzzWithVoid", collector =
        collector, freeze = freeze)

    public val _uzzWithTSomeGenericList: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_uzzWithTSomeGenericList",
            collector = collector, freeze = freeze)

    public val _uzzWithTSomeGenericLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_uzzWithTSomeGenericLists",
            collector = collector, freeze = freeze)

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lzzWithVoid", collector =
        collector, freeze = freeze)

    public val _lzzWithTSomeGenericList: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lzzWithTSomeGenericList",
            collector = collector, freeze = freeze)

    public val _lzzWithTSomeGenericLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_lzzWithTSomeGenericLists",
            collector = collector, freeze = freeze)

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_tzzWithVoid", collector =
        collector, freeze = freeze)

    public val _tzzWithZTSomeGenericList: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_tzzWithZTSomeGenericList",
            collector = collector, freeze = freeze)

    public val _tzzWithZTSomeGenericLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_tzzWithZTSomeGenericLists",
            collector = collector, freeze = freeze)

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_rzzWithVoid", collector =
        collector, freeze = freeze)

    public val _rzzWithTSomeGenericMap: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_rzzWithTSomeGenericMap",
            collector = collector, freeze = freeze)

    public val _rzzWithTSomeGenericMaps: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_rzzWithTSomeGenericMaps",
            collector = collector, freeze = freeze)

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_izzWithVoid", collector =
        collector, freeze = freeze)

    public val _izzWithTSomeGenericComparable: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_izzWithTSomeGenericComparable",
            collector = collector, freeze = freeze)

    public val _izzWithTSomeGenericComparables: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_izzWithTSomeGenericComparables",
            collector = collector, freeze = freeze)

    public val _ossWithTAny: KMockContract.SyncFunProxy<Any?, (Any?) -> Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_ossWithTAny", collector =
        collector, freeze = freeze)

    public val _ossWithTAnyZRAny: KMockContract.SyncFunProxy<Unit, (Any?, Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_ossWithTAnyZRAny",
            collector = collector, freeze = freeze)

    public val _ossWithZRAnyTAnys: KMockContract.SyncFunProxy<Unit, (Any?, kotlin.Array<out
    kotlin.Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_ossWithZRAnyTAnys",
            collector = collector, freeze = freeze)

    public val _kssWithTSomeGenericComparable: KMockContract.SyncFunProxy<Any, (Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_kssWithTSomeGenericComparable",
            collector = collector, freeze = freeze)

    public val _kssWithTSomeGenericComparableRSomeGenericComparable:
        KMockContract.SyncFunProxy<Unit, (Any, Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_kssWithTSomeGenericComparableRSomeGenericComparable",
            collector = collector, freeze = freeze)

    public val _issWithZTAny: KMockContract.SyncFunProxy<Any, (Any?) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_issWithZTAny", collector
        = collector, freeze = freeze)

    public val _issWithZTAnyRSomeGenericComparable: KMockContract.SyncFunProxy<Unit, (Any?,
        Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_issWithZTAnyRSomeGenericComparable",
            collector = collector, freeze = freeze)

    public val _pssWithTSomeGeneric:
        KMockContract.SyncFunProxy<mock.template.generic.SomeGeneric<kotlin.String>, (mock.template.generic.SomeGeneric<kotlin.String>) -> mock.template.generic.SomeGeneric<kotlin.String>>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_pssWithTSomeGeneric",
        collector = collector, freeze = freeze)

    public val _pssWithTSomeGenericRSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (mock.template.generic.SomeGeneric<kotlin.String>,
            mock.template.generic.SomeGeneric<kotlin.String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_pssWithTSomeGenericRSomeGeneric",
            collector = collector, freeze = freeze)

    public val _xssWithZTAny: KMockContract.SyncFunProxy<Any, (Any?) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_xssWithZTAny", collector
        = collector, freeze = freeze)

    public val _xssWithZTAnyRSequenceCharSequence: KMockContract.SyncFunProxy<Unit, (Any?,
        Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.PlatformMock#_xssWithZTAnyRSequenceCharSequence",
            collector = collector, freeze = freeze)

    public override fun foo(payload: Any): String = _fooWithAny.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithZTAny.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> foo(vararg payload: T): Unit = _fooWithZTAnys.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun lol(vararg payload: Array<out Any>): Unit = _lolWithArrays.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> lol(fuzz: Array<T>): Any = _lolWithArray.invoke(fuzz)

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
        _tzzWithZTSomeGenericList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T> tzz(vararg payload: T): Unit where T : SomeGeneric<String>?, T :
    List<String>? = _tzzWithZTSomeGenericLists.invoke(payload) {
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
    Comparable<List<Array<R>>> = _kssWithTSomeGenericComparable.invoke(arg0) as R

    public override fun <T : R, R> kss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kssWithTSomeGenericComparableRSomeGenericComparable.invoke(arg0,
        arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithZTAny.invoke(arg0) as R

    public override fun <R, T> iss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithZTAnyRSomeGenericComparable.invoke(arg0, arg1) {
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
        = _xssWithZTAnyRSequenceCharSequence.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _template.clear()
        _fooWithAny.clear()
        _fooWithVoid.clear()
        _fooWithZTAny.clear()
        _fooWithZTAnys.clear()
        _lolWithArrays.clear()
        _lolWithArray.clear()
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
        _uzzWithTSomeGenericList.clear()
        _uzzWithTSomeGenericLists.clear()
        _lzzWithVoid.clear()
        _lzzWithTSomeGenericList.clear()
        _lzzWithTSomeGenericLists.clear()
        _tzzWithVoid.clear()
        _tzzWithZTSomeGenericList.clear()
        _tzzWithZTSomeGenericLists.clear()
        _rzzWithVoid.clear()
        _rzzWithTSomeGenericMap.clear()
        _rzzWithTSomeGenericMaps.clear()
        _izzWithVoid.clear()
        _izzWithTSomeGenericComparable.clear()
        _izzWithTSomeGenericComparables.clear()
        _ossWithTAny.clear()
        _ossWithTAnyZRAny.clear()
        _ossWithZRAnyTAnys.clear()
        _kssWithTSomeGenericComparable.clear()
        _kssWithTSomeGenericComparableRSomeGenericComparable.clear()
        _issWithZTAny.clear()
        _issWithZTAnyRSomeGenericComparable.clear()
        _pssWithTSomeGeneric.clear()
        _pssWithTSomeGenericRSomeGeneric.clear()
        _xssWithZTAny.clear()
        _xssWithZTAnyRSequenceCharSequence.clear()
    }
}
