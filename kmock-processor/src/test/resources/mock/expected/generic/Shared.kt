package mock.template.generic

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Char
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Enum
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.sequences.Sequence
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SharedMock<K : Any, L>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Shared<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Shared<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.executeOnGet()
        set(`value`) = _template.executeOnSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.generic.SharedMock#_template", collector =
        collector, freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_fooWithVoid", collector =
        collector, freeze = freeze)

    public val _fooWithZTAny: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_fooWithZTAny", collector =
        collector, freeze = freeze)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blaWithVoid", collector =
        collector, freeze = freeze)

    public val _blaWithTInt: KMockContract.SyncFunProxy<Unit, (Int) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blaWithTInt", collector =
        collector, freeze = freeze)

    public val _blaWithZTInt: KMockContract.SyncFunProxy<Unit, (Int?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blaWithZTInt", collector =
        collector, freeze = freeze)

    public val _blaWithTCharSequenceTComparable: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blaWithTCharSequenceTComparable",
            collector = collector, freeze = freeze)

    public val _blaWithTCharSequenceTComparableKCharSequenceKComparable:
        KMockContract.SyncFunProxy<Unit, (Any, Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blaWithTCharSequenceTComparableKCharSequenceKComparable",
            collector = collector, freeze = freeze)

    public val _barWithTList: KMockContract.SyncFunProxy<Unit, (List<Array<String>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_barWithTList", collector =
        collector, freeze = freeze)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<List<Array<String>>, () -> List<Array<String>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_barWithVoid", collector =
        collector, freeze = freeze)

    public val _blubbWithTList: KMockContract.SyncFunProxy<Unit, (List<Array<String?>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blubbWithTList", collector
        = collector, freeze = freeze)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<List<Array<String?>>, () -> List<Array<String?>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blubbWithVoid", collector =
        collector, freeze = freeze)

    public val _bussWithZTList: KMockContract.SyncFunProxy<Unit, (List<Array<Int>>?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_bussWithZTList", collector
        = collector, freeze = freeze)

    public val _bussWithVoid: KMockContract.SyncFunProxy<List<Array<Int>>?, () -> List<Array<Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_bussWithVoid", collector =
        collector, freeze = freeze)

    public val _bossWithTList: KMockContract.SyncFunProxy<Unit, (List<Array<Int>?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_bossWithTList", collector =
        collector, freeze = freeze)

    public val _bossWithVoid: KMockContract.SyncFunProxy<List<Array<Int>?>, () -> List<Array<Int>?>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_bossWithVoid", collector =
        collector, freeze = freeze)

    public val _buzzWithTList: KMockContract.SyncFunProxy<Unit, (List<Array<Int>>?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_buzzWithTList", collector =
        collector, freeze = freeze)

    public val _buzzWithVoid: KMockContract.SyncFunProxy<List<Array<Int>>?, () -> List<Array<Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_buzzWithVoid", collector =
        collector, freeze = freeze)

    public val _ozzWithTL: KMockContract.SyncFunProxy<Unit, (L) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_ozzWithTL", collector =
        collector, freeze = freeze)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_ozzWithVoid", collector =
        collector, freeze = freeze)

    public val _brassWithTComparable:
        KMockContract.SyncFunProxy<Unit, (Comparable<List<Array<Any>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_brassWithTComparable",
            collector = collector, freeze = freeze)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<Comparable<List<Array<Any>>>, () -> Comparable<List<Array<Any>>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_brassWithVoid", collector =
        collector, freeze = freeze)

    public val _blissWithZTComparable:
        KMockContract.SyncFunProxy<Unit, (Comparable<List<Array<Any?>>>?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blissWithZTComparable",
            collector = collector, freeze = freeze)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<Comparable<List<Array<Any?>>>?, () -> Comparable<List<Array<Any?>>>?>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blissWithVoid", collector
    = collector, freeze = freeze)

    public val _lossWithTMap: KMockContract.SyncFunProxy<Unit, (Map<String, String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_lossWithTMap", collector =
        collector, freeze = freeze)

    public val _lossWithVoid:
        KMockContract.SyncFunProxy<Map<String, String>, () -> Map<String, String>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_lossWithVoid", collector =
        collector, freeze = freeze)

    public val _uzzWithTSomeGenericTList: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_uzzWithTSomeGenericTList",
            collector = collector, freeze = freeze)

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_uzzWithVoid", collector =
        collector, freeze = freeze)

    public val _lzzWithTSomeGenericTList: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_lzzWithTSomeGenericTList",
            collector = collector, freeze = freeze)

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_lzzWithVoid", collector =
        collector, freeze = freeze)

    public val _tzzWithZTSomeGenericZTList: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_tzzWithZTSomeGenericZTList",
            collector = collector, freeze = freeze)

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_tzzWithVoid", collector =
        collector, freeze = freeze)

    public val _rzzWithTSomeGenericTMap: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_rzzWithTSomeGenericTMap",
            collector = collector, freeze = freeze)

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_rzzWithVoid", collector =
        collector, freeze = freeze)

    public val _izzWithTSomeGenericTComparable: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_izzWithTSomeGenericTComparable",
            collector = collector, freeze = freeze)

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_izzWithVoid", collector =
        collector, freeze = freeze)

    public val _ossWithZTAnyZRAny: KMockContract.SyncFunProxy<Unit, (Any?, Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_ossWithZTAnyZRAny",
            collector = collector, freeze = freeze)

    public val _ossWithZTAny: KMockContract.SyncFunProxy<Any?, (Any?) -> Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_ossWithZTAny", collector =
        collector, freeze = freeze)

    public val _kssWithTSomeGenericTComparableRSomeGenericRComparable:
        KMockContract.SyncFunProxy<Unit, (Any, Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_kssWithTSomeGenericTComparableRSomeGenericRComparable",
            collector = collector, freeze = freeze)

    public val _kssWithTSomeGenericTComparable: KMockContract.SyncFunProxy<Any, (Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_kssWithTSomeGenericTComparable",
            collector = collector, freeze = freeze)

    public val _issWithZTAnyRSomeGenericRComparable: KMockContract.SyncFunProxy<Unit, (Any?,
        Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_issWithZTAnyRSomeGenericRComparable",
            collector = collector, freeze = freeze)

    public val _issWithZTAny: KMockContract.SyncFunProxy<Any, (Any?) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_issWithZTAny", collector =
        collector, freeze = freeze)

    public val _pssWithTSomeGeneric:
        KMockContract.SyncFunProxy<SomeGeneric<String>, (SomeGeneric<String>) -> SomeGeneric<String>>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_pssWithTSomeGeneric",
        collector = collector, freeze = freeze)

    public val _pssWithTSomeGenericRSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (SomeGeneric<String>, SomeGeneric<String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_pssWithTSomeGenericRSomeGeneric",
            collector = collector, freeze = freeze)

    public val _xssWithZTAny: KMockContract.SyncFunProxy<Any, (Any?) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_xssWithZTAny", collector =
        collector, freeze = freeze)

    public val _xssWithZTAnyRSequenceRCharSequence: KMockContract.SyncFunProxy<Unit, (Any?,
        Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_xssWithZTAnyRSequenceRCharSequence",
            collector = collector, freeze = freeze)

    public val _rrr: KMockContract.SyncFunProxy<Unit, (List<Any>, Sequence<List<Any>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_rrr", collector =
        collector, freeze = freeze)

    public val _rol: KMockContract.SyncFunProxy<Unit, (List<Array<out Int>>,
        Sequence<List<Array<out Int>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_rol", collector =
        collector, freeze = freeze)

    public val _lol: KMockContract.SyncFunProxy<Unit, (List<Array<in Int>>,
        Sequence<List<Array<in Int>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_lol", collector =
        collector, freeze = freeze)

    public val _nol: KMockContract.SyncFunProxy<Unit, (List<Array<*>>,
        Sequence<List<Array<*>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_nol", collector =
        collector, freeze = freeze)

    public val _zok: KMockContract.SyncFunProxy<Unit, (Enum<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_zok", collector =
        collector, freeze = freeze)

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

    public override fun <T : Int?> bla(payload: T): Unit = _blaWithZTInt.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> bla(payload: T): Unit where T : CharSequence, T : Comparable<T>? =
        _blaWithTCharSequenceTComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : K, K> bla(arg0: T, arg1: K): Unit where K : CharSequence, K :
    Comparable<K>? = _blaWithTCharSequenceTComparableKCharSequenceKComparable.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : List<Array<String>>> bar(payload: T): Unit =
        _barWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String>>> bar(): T = _barWithVoid.invoke() as T

    public override fun <T : List<Array<String?>>> blubb(payload: T): Unit =
        _blubbWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String?>>> blubb(): T = _blubbWithVoid.invoke() as T

    public override fun <T : List<Array<Int>>?> buss(payload: T): Unit =
        _bussWithZTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>?> buss(): T = _bussWithVoid.invoke() as T

    public override fun <T : List<Array<Int>?>> boss(payload: T): Unit =
        _bossWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>?>> boss(): T = _bossWithVoid.invoke() as T

    public override fun <T : List<Array<Int>>> buzz(payload: T?): Unit =
        _buzzWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>> buzz(): T? = _buzzWithVoid.invoke() as T?

    public override fun <T : L> ozz(payload: T): Unit = _ozzWithTL.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : L> ozz(): T = _ozzWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>> brass(payload: T): Unit =
        _brassWithTComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>> brass(): T = _brassWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>?> bliss(payload: T): Unit =
        _blissWithZTComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>?> bliss(): T = _blissWithVoid.invoke() as T

    public override fun <T : Map<String, String>> loss(payload: T): Unit =
        _lossWithTMap.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Map<String, String>> loss(): T = _lossWithVoid.invoke() as T

    public override fun <T> uzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String> =
        _uzzWithTSomeGenericTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String> =
        _uzzWithVoid.invoke() as T

    public override fun <T> lzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithTSomeGenericTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithVoid.invoke() as T

    public override fun <T> tzz(payload: T): Unit where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithZTSomeGenericZTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithVoid.invoke() as T

    public override fun <T> rzz(payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzzWithTSomeGenericTMap.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzzWithVoid.invoke() as T

    public override fun <T> izz(payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izzWithTSomeGenericTComparable.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izzWithVoid.invoke() as T

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithZTAnyZRAny.invoke(arg0, arg1)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithZTAny.invoke(arg0) as R

    public override fun <T : R, R> kss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        _kssWithTSomeGenericTComparableRSomeGenericRComparable.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kssWithTSomeGenericTComparable.invoke(arg0) as R

    public override fun <R, T> iss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithZTAnyRSomeGenericRComparable.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithZTAny.invoke(arg0) as R

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

    public override fun <R : Sequence<T>, T : List<R>> rrr(arg0: T, arg1: R): Unit = _rrr.invoke(arg0,
        arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <R : Sequence<T>, T : List<Array<out Int>>> rol(arg0: T, arg1: R): Unit =
        _rol.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <R : Sequence<T>, T : List<Array<in Int>>> lol(arg0: T, arg1: R): Unit =
        _lol.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <R : Sequence<T>, T : List<Array<*>>> nol(arg0: T, arg1: R): Unit =
        _nol.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <X : Enum<*>> zok(arg0: X): Unit = _zok.invoke(arg0) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _template.clear()
        _fooWithVoid.clear()
        _fooWithZTAny.clear()
        _blaWithVoid.clear()
        _blaWithTInt.clear()
        _blaWithZTInt.clear()
        _blaWithTCharSequenceTComparable.clear()
        _blaWithTCharSequenceTComparableKCharSequenceKComparable.clear()
        _barWithTList.clear()
        _barWithVoid.clear()
        _blubbWithTList.clear()
        _blubbWithVoid.clear()
        _bussWithZTList.clear()
        _bussWithVoid.clear()
        _bossWithTList.clear()
        _bossWithVoid.clear()
        _buzzWithTList.clear()
        _buzzWithVoid.clear()
        _ozzWithTL.clear()
        _ozzWithVoid.clear()
        _brassWithTComparable.clear()
        _brassWithVoid.clear()
        _blissWithZTComparable.clear()
        _blissWithVoid.clear()
        _lossWithTMap.clear()
        _lossWithVoid.clear()
        _uzzWithTSomeGenericTList.clear()
        _uzzWithVoid.clear()
        _lzzWithTSomeGenericTList.clear()
        _lzzWithVoid.clear()
        _tzzWithZTSomeGenericZTList.clear()
        _tzzWithVoid.clear()
        _rzzWithTSomeGenericTMap.clear()
        _rzzWithVoid.clear()
        _izzWithTSomeGenericTComparable.clear()
        _izzWithVoid.clear()
        _ossWithZTAnyZRAny.clear()
        _ossWithZTAny.clear()
        _kssWithTSomeGenericTComparableRSomeGenericRComparable.clear()
        _kssWithTSomeGenericTComparable.clear()
        _issWithZTAnyRSomeGenericRComparable.clear()
        _issWithZTAny.clear()
        _pssWithTSomeGeneric.clear()
        _pssWithTSomeGenericRSomeGeneric.clear()
        _xssWithZTAny.clear()
        _xssWithZTAnyRSequenceRCharSequence.clear()
        _rrr.clear()
        _rol.clear()
        _lol.clear()
        _nol.clear()
        _zok.clear()
    }
}
