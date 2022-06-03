package mock.template.access

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Char
import kotlin.CharArray
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Int
import kotlin.IntArray
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.reflect.KProperty
import kotlin.sequences.Sequence
import tech.antibytes.kmock.Hint0
import tech.antibytes.kmock.Hint1
import tech.antibytes.kmock.Hint2
import tech.antibytes.kmock.Hint4
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.SafeJvmName
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class OverloadedMock<K : Any, L, U : Int?, W>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Overloaded<K, L, U, W>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Overloaded<K, L, U, W> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.access.OverloadedMock#_template", collector =
        collector, freeze = freeze)

    public val _trrWithVoid: KMockContract.SyncFunProxy<Unit, () -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_trrWithVoid", collector
        = collector, freeze = freeze)

    public val _trrWithAny: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_trrWithAny", collector =
        collector, freeze = freeze)

    public val _trrWithInt: KMockContract.SyncFunProxy<Unit, (Int) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_trrWithInt", collector =
        collector, freeze = freeze)

    public val _urrWithVoid: KMockContract.SyncFunProxy<Unit, () -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_urrWithVoid", collector
        = collector, freeze = freeze)

    public val _urrWithU: KMockContract.SyncFunProxy<Unit, (U) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_urrWithU", collector =
        collector, freeze = freeze)

    public val _urrWithW: KMockContract.SyncFunProxy<Unit, (W) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_urrWithW", collector =
        collector, freeze = freeze)

    public val _urrWithZUCharSequence: KMockContract.SyncFunProxy<Unit, (CharSequence?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_urrWithZUCharSequence",
            collector = collector, freeze = freeze)

    public val _krrWithTInt: KMockContract.SyncFunProxy<Unit, (Int) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_krrWithTInt", collector
        = collector, freeze = freeze)

    public val _krrWithZTInt: KMockContract.SyncFunProxy<Unit, (Int?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_krrWithZTInt", collector
        = collector, freeze = freeze)

    public val _krrWithZTAny: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_krrWithZTAny", collector
        = collector, freeze = freeze)

    public val _fooWithAny: KMockContract.SyncFunProxy<String, (Any) -> String> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_fooWithAny", collector =
        collector, freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_fooWithVoid", collector
        = collector, freeze = freeze)

    public val _fooWithZTAny: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_fooWithZTAny", collector
        = collector, freeze = freeze)

    public val _fooWithZTAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_fooWithZTAnys",
            collector = collector, freeze = freeze)

    public val _lolWithInt: KMockContract.SyncFunProxy<Unit, (Int) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_lolWithInt", collector =
        collector, freeze = freeze)

    public val _lolWithArrays: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out kotlin.Array<out
    kotlin.Any>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_lolWithArrays",
            collector = collector, freeze = freeze)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>, () -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_brassWithVoid",
        collector = collector, freeze = freeze)

    public val _brassWithTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_brassWithTComparable",
        collector = collector, freeze = freeze)

    public val _brassWithTComparables: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_brassWithTComparables",
            collector = collector, freeze = freeze)

    public val _blaWithVoid: KMockContract.AsyncFunProxy<Int, suspend () -> Int> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.OverloadedMock#_blaWithVoid", collector
        = collector, freeze = freeze)

    public val _blaWithTInt: KMockContract.AsyncFunProxy<Unit, suspend (Int) -> Unit> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.OverloadedMock#_blaWithTInt", collector
        = collector, freeze = freeze)

    public val _blaWithTInts: KMockContract.AsyncFunProxy<Unit, suspend (IntArray) -> Unit> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.OverloadedMock#_blaWithTInts",
            collector = collector, freeze = freeze)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () -> kotlin.collections.List<kotlin.Array<kotlin.String>>>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_barWithVoid",
        collector = collector, freeze = freeze)

    public val _barWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_barWithTList",
        collector = collector, freeze = freeze)

    public val _barWithTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_barWithTLists",
            collector = collector, freeze = freeze)

    public val _blubbWithStringCharArrayBooleanInt: KMockContract.SyncFunProxy<Unit, (
        String,
        CharArray,
        Boolean,
        Int,
    ) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_blubbWithStringCharArrayBooleanInt",
            collector = collector, freeze = freeze)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () -> kotlin.collections.List<kotlin.Array<kotlin.String?>>>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_blubbWithVoid",
        collector = collector, freeze = freeze)

    public val _blubbWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_blubbWithTList",
        collector = collector, freeze = freeze)

    public val _blubbWithTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_blubbWithTLists",
            collector = collector, freeze = freeze)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_bussWithVoid",
        collector = collector, freeze = freeze)

    public val _bussWithZTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_bussWithZTList",
        collector = collector, freeze = freeze)

    public val _bussWithZTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_bussWithZTLists",
            collector = collector, freeze = freeze)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () -> kotlin.collections.List<kotlin.Array<kotlin.Int>?>>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_bossWithVoid",
        collector = collector, freeze = freeze)

    public val _bossWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_bossWithTList",
        collector = collector, freeze = freeze)

    public val _bossWithTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_bossWithTLists",
            collector = collector, freeze = freeze)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_buzzWithVoid",
        collector = collector, freeze = freeze)

    public val _buzzWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_buzzWithTList",
        collector = collector, freeze = freeze)

    public val _buzzWithTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_buzzWithTLists",
            collector = collector, freeze = freeze)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_ozzWithVoid", collector
        = collector, freeze = freeze)

    public val _ozzWithTL: KMockContract.SyncFunProxy<Unit, (L) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_ozzWithTL", collector =
        collector, freeze = freeze)

    public val _ozzWithTLs: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out L>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_ozzWithTLs", collector =
        collector, freeze = freeze)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?, () -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_blissWithVoid",
        collector = collector, freeze = freeze)

    public val _blissWithZTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) -> Unit>
        =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_blissWithZTComparable",
            collector = collector, freeze = freeze)

    public val _blissWithZTComparables: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_blissWithZTComparables",
            collector = collector, freeze = freeze)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_lossWithVoid", collector
        = collector, freeze = freeze)

    public val _lossWithTMap: KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String,
        kotlin.String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_lossWithTMap", collector
        = collector, freeze = freeze)

    public val _lossWithTMaps: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.Map<kotlin.String, kotlin.String>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_lossWithTMaps",
            collector = collector, freeze = freeze)

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_uzzWithVoid", collector
        = collector, freeze = freeze)

    public val _uzzWithTSomeGenericList: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_uzzWithTSomeGenericList",
            collector = collector, freeze = freeze)

    public val _uzzWithTSomeGenericLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_uzzWithTSomeGenericLists",
            collector = collector, freeze = freeze)

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_lzzWithVoid", collector
        = collector, freeze = freeze)

    public val _lzzWithTSomeGenericList: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_lzzWithTSomeGenericList",
            collector = collector, freeze = freeze)

    public val _lzzWithTSomeGenericLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_lzzWithTSomeGenericLists",
            collector = collector, freeze = freeze)

    public val _tzzWithVoid: KMockContract.AsyncFunProxy<Any?, suspend () -> Any?> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.OverloadedMock#_tzzWithVoid", collector
        = collector, freeze = freeze)

    public val _tzzWithZTSomeGenericList: KMockContract.AsyncFunProxy<Unit, suspend (Any?) -> Unit> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.OverloadedMock#_tzzWithZTSomeGenericList",
            collector = collector, freeze = freeze)

    public val _tzzWithZTSomeGenericLists: KMockContract.AsyncFunProxy<Unit, suspend (kotlin.Array<out
    kotlin.Any?>) -> Unit> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.OverloadedMock#_tzzWithZTSomeGenericLists",
            collector = collector, freeze = freeze)

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_rzzWithVoid", collector
        = collector, freeze = freeze)

    public val _rzzWithTSomeGenericMap: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_rzzWithTSomeGenericMap",
            collector = collector, freeze = freeze)

    public val _rzzWithTSomeGenericMaps: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_rzzWithTSomeGenericMaps",
            collector = collector, freeze = freeze)

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_izzWithVoid", collector
        = collector, freeze = freeze)

    public val _izzWithTSomeGenericComparable: KMockContract.SyncFunProxy<Unit, (Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_izzWithTSomeGenericComparable",
            collector = collector, freeze = freeze)

    public val _izzWithTSomeGenericComparables: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_izzWithTSomeGenericComparables",
            collector = collector, freeze = freeze)

    public val _ossWithTAny: KMockContract.AsyncFunProxy<Any?, suspend (Any?) -> Any?> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.OverloadedMock#_ossWithTAny", collector
        = collector, freeze = freeze)

    public val _ossWithTAnyZRAny: KMockContract.AsyncFunProxy<Unit, suspend (Any?, Any?) -> Unit> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.OverloadedMock#_ossWithTAnyZRAny",
            collector = collector, freeze = freeze)

    public val _ossWithZRAnyTAnys: KMockContract.AsyncFunProxy<Unit, suspend (Any?, kotlin.Array<out
    kotlin.Any?>) -> Unit> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.OverloadedMock#_ossWithZRAnyTAnys",
            collector = collector, freeze = freeze)

    public val _kssWithTSomeGenericComparable: KMockContract.SyncFunProxy<Any, (Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_kssWithTSomeGenericComparable",
            collector = collector, freeze = freeze)

    public val _kssWithTSomeGenericComparableRSomeGenericComparable:
        KMockContract.SyncFunProxy<Unit, (Any, Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_kssWithTSomeGenericComparableRSomeGenericComparable",
            collector = collector, freeze = freeze)

    public val _issWithZTAny: KMockContract.AsyncFunProxy<Any, suspend (Any?) -> Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.OverloadedMock#_issWithZTAny",
            collector = collector, freeze = freeze)

    public val _issWithZTAnyRSomeGenericComparable: KMockContract.AsyncFunProxy<Unit, suspend (Any?,
        Any) -> Unit> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.OverloadedMock#_issWithZTAnyRSomeGenericComparable",
            collector = collector, freeze = freeze)

    public val _pssWithTSomeGeneric:
        KMockContract.SyncFunProxy<mock.template.access.SomeGeneric<kotlin.String>, (mock.template.access.SomeGeneric<kotlin.String>) -> mock.template.access.SomeGeneric<kotlin.String>>
        = ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_pssWithTSomeGeneric",
        collector = collector, freeze = freeze)

    public val _pssWithTSomeGenericRSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (mock.template.access.SomeGeneric<kotlin.String>,
            mock.template.access.SomeGeneric<kotlin.String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_pssWithTSomeGenericRSomeGeneric",
            collector = collector, freeze = freeze)

    public val _xssWithZTAny: KMockContract.SyncFunProxy<Any, (Any?) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_xssWithZTAny", collector
        = collector, freeze = freeze)

    public val _xssWithZTAnyRSequenceCharSequence: KMockContract.SyncFunProxy<Unit, (Any?,
        Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.OverloadedMock#_xssWithZTAnyRSequenceCharSequence",
            collector = collector, freeze = freeze)

    private val referenceStore: Map<String, KMockContract.Proxy<*, *>> = mapOf(
        "template|property" to _template,
        "trr|() -> kotlin.Unit|[]" to _trrWithVoid,
        "urr|() -> kotlin.Unit|[]" to _urrWithVoid,
        "trr|(kotlin.Any) -> kotlin.Unit|[]" to _trrWithAny,
        "trr|(kotlin.Int) -> kotlin.Unit|[]" to _trrWithInt,
        "lol|(kotlin.Int) -> kotlin.Unit|[]" to _lolWithInt,
        "urr|(U) -> kotlin.Unit|[]" to _urrWithU,
        "urr|(W) -> kotlin.Unit|[]" to _urrWithW,
        "urr|(kotlin.CharSequence?) -> kotlin.Unit|[[kotlin.CharSequence?]]" to
            _urrWithZUCharSequence,
        "krr|(kotlin.Int) -> kotlin.Unit|[[kotlin.Int]]" to _krrWithTInt,
        "krr|(kotlin.Int?) -> kotlin.Unit|[[kotlin.Int?]]" to _krrWithZTInt,
        "krr|(kotlin.Any?) -> kotlin.Unit|[[kotlin.Any?]]" to _krrWithZTAny,
        "foo|(kotlin.Any?) -> kotlin.Unit|[[kotlin.Any?]]" to _fooWithZTAny,
        "foo|(kotlin.Any) -> kotlin.String|[]" to _fooWithAny,
        "foo|() -> kotlin.Any?|[[kotlin.Any?]]" to _fooWithVoid,
        "foo|(kotlin.Array<out kotlin.Any?>) -> kotlin.Unit|[[kotlin.Any?]]" to _fooWithZTAnys,
        "lol|(kotlin.Array<out kotlin.Array<out kotlin.Any>>) -> kotlin.Unit|[]" to _lolWithArrays,
        "brass|() -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>|[[kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>]]"
            to _brassWithVoid,
        "brass|(kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>) -> kotlin.Unit|[[kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>]]"
            to _brassWithTComparable,
        "brass|(kotlin.Array<out kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>) -> kotlin.Unit|[[kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>]]"
            to _brassWithTComparables,
        "bar|() -> kotlin.collections.List<kotlin.Array<kotlin.String>>|[[kotlin.collections.List<kotlin.Array<kotlin.String>>]]"
            to _barWithVoid,
        "bar|(kotlin.collections.List<kotlin.Array<kotlin.String>>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.String>>]]"
            to _barWithTList,
        "bar|(kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.String>>]]"
            to _barWithTLists,
        "blubb|( kotlin.String, kotlin.CharArray, kotlin.Boolean, kotlin.Int,) -> kotlin.Unit|[]" to
            _blubbWithStringCharArrayBooleanInt,
        "blubb|() -> kotlin.collections.List<kotlin.Array<kotlin.String?>>|[[kotlin.collections.List<kotlin.Array<kotlin.String?>>]]"
            to _blubbWithVoid,
        "blubb|(kotlin.collections.List<kotlin.Array<kotlin.String?>>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.String?>>]]"
            to _blubbWithTList,
        "blubb|(kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.String?>>]]"
            to _blubbWithTLists,
        "buss|() -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?|[[kotlin.collections.List<kotlin.Array<kotlin.Int>>?]]"
            to _bussWithVoid,
        "buss|(kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.Int>>?]]"
            to _bussWithZTList,
        "buss|(kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.Int>>?]]"
            to _bussWithZTLists,
        "boss|() -> kotlin.collections.List<kotlin.Array<kotlin.Int>?>|[[kotlin.collections.List<kotlin.Array<kotlin.Int>?>]]"
            to _bossWithVoid,
        "boss|(kotlin.collections.List<kotlin.Array<kotlin.Int>?>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.Int>?>]]"
            to _bossWithTList,
        "boss|(kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.Int>?>]]"
            to _bossWithTLists,
        "buzz|() -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?|[[kotlin.collections.List<kotlin.Array<kotlin.Int>>]]"
            to _buzzWithVoid,
        "buzz|(kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.Int>>]]"
            to _buzzWithTList,
        "buzz|(kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.Int>>]]"
            to _buzzWithTLists,
        "ozz|() -> L|[[L]]" to _ozzWithVoid,
        "ozz|(L) -> kotlin.Unit|[[L]]" to _ozzWithTL,
        "ozz|(kotlin.Array<out L>) -> kotlin.Unit|[[L]]" to _ozzWithTLs,
        "bliss|() -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?|[[kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>?]]"
            to _blissWithVoid,
        "bliss|(kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) -> kotlin.Unit|[[kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>?]]"
            to _blissWithZTComparable,
        "bliss|(kotlin.Array<out kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>) -> kotlin.Unit|[[kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>?]]"
            to _blissWithZTComparables,
        "loss|() -> kotlin.collections.Map<kotlin.String, kotlin.String>|[[kotlin.collections.Map<kotlin.String, kotlin.String>]]"
            to _lossWithVoid,
        "loss|(kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.Unit|[[kotlin.collections.Map<kotlin.String, kotlin.String>]]"
            to _lossWithTMap,
        "loss|(kotlin.Array<out kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit|[[kotlin.collections.Map<kotlin.String, kotlin.String>]]"
            to _lossWithTMaps,
        "uzz|() -> kotlin.Any|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.List<kotlin.String>]]"
            to _uzzWithVoid,
        "uzz|(kotlin.Any) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.List<kotlin.String>]]"
            to _uzzWithTSomeGenericList,
        "uzz|(kotlin.Array<out kotlin.Any>) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.List<kotlin.String>]]"
            to _uzzWithTSomeGenericLists,
        "lzz|() -> kotlin.Any|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.List<kotlin.String>?]]"
            to _lzzWithVoid,
        "lzz|(kotlin.Any) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.List<kotlin.String>?]]"
            to _lzzWithTSomeGenericList,
        "lzz|(kotlin.Array<out kotlin.Any>) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.List<kotlin.String>?]]"
            to _lzzWithTSomeGenericLists,
        "rzz|() -> kotlin.Any|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.Map<kotlin.String, kotlin.String>]]"
            to _rzzWithVoid,
        "rzz|(kotlin.Any) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.Map<kotlin.String, kotlin.String>]]"
            to _rzzWithTSomeGenericMap,
        "rzz|(kotlin.Array<out kotlin.Any>) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.Map<kotlin.String, kotlin.String>]]"
            to _rzzWithTSomeGenericMaps,
        "izz|() -> kotlin.Any|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>]]"
            to _izzWithVoid,
        "izz|(kotlin.Any) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>]]"
            to _izzWithTSomeGenericComparable,
        "izz|(kotlin.Array<out kotlin.Any>) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>]]"
            to _izzWithTSomeGenericComparables,
        "kss|(kotlin.Any) -> kotlin.Any|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<R>>>], [mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<R>>>]]"
            to _kssWithTSomeGenericComparable,
        "kss|(kotlin.Any, kotlin.Any) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<R>>>], [mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<R>>>]]"
            to _kssWithTSomeGenericComparableRSomeGenericComparable,
        "pss|(mock.template.access.SomeGeneric<kotlin.String>) -> mock.template.access.SomeGeneric<kotlin.String>|[[mock.template.access.SomeGeneric<kotlin.String>], [mock.template.access.SomeGeneric<kotlin.String>], [mock.template.access.SomeGeneric<kotlin.String>]]"
            to _pssWithTSomeGeneric,
        "pss|(mock.template.access.SomeGeneric<kotlin.String>, mock.template.access.SomeGeneric<kotlin.String>) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String>], [mock.template.access.SomeGeneric<kotlin.String>], [mock.template.access.SomeGeneric<kotlin.String>]]"
            to _pssWithTSomeGenericRSomeGeneric,
        "xss|(kotlin.Any?) -> kotlin.Any|[[kotlin.sequences.Sequence<kotlin.Char> & kotlin.CharSequence], [kotlin.Any?]]"
            to _xssWithZTAny,
        "xss|(kotlin.Any?, kotlin.Any) -> kotlin.Unit|[[kotlin.sequences.Sequence<kotlin.Char>? & kotlin.CharSequence], [kotlin.Any?]]"
            to _xssWithZTAnyRSequenceCharSequence,
        "bla|suspend () -> kotlin.Int|[[kotlin.Int]]" to _blaWithVoid,
        "bla|suspend (kotlin.Int) -> kotlin.Unit|[[kotlin.Int]]" to _blaWithTInt,
        "bla|suspend (kotlin.IntArray) -> kotlin.Unit|[[kotlin.Int]]" to _blaWithTInts,
        "tzz|suspend () -> kotlin.Any?|[[mock.template.access.SomeGeneric<kotlin.String>? & kotlin.collections.List<kotlin.String>?]]"
            to _tzzWithVoid,
        "tzz|suspend (kotlin.Any?) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String>? & kotlin.collections.List<kotlin.String>?]]"
            to _tzzWithZTSomeGenericList,
        "tzz|suspend (kotlin.Array<out kotlin.Any?>) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String>? & kotlin.collections.List<kotlin.String>?]]"
            to _tzzWithZTSomeGenericLists,
        "oss|suspend (kotlin.Any?) -> kotlin.Any?|[[kotlin.Any?], [kotlin.Any?]]" to _ossWithTAny,
        "oss|suspend (kotlin.Any?, kotlin.Any?) -> kotlin.Unit|[[kotlin.Any?], [kotlin.Any?]]" to
            _ossWithTAnyZRAny,
        "oss|suspend (kotlin.Any?, kotlin.Array<out kotlin.Any?>) -> kotlin.Unit|[[kotlin.Any?], [kotlin.Any?]]"
            to _ossWithZRAnyTAnys,
        "iss|suspend (kotlin.Any?) -> kotlin.Any|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>], [kotlin.Any?]]"
            to _issWithZTAny,
        "iss|suspend (kotlin.Any?, kotlin.Any) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>], [kotlin.Any?]]"
            to _issWithZTAnyRSomeGenericComparable,
    )

    public override fun trr(): Unit = _trrWithVoid.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun trr(arg: Any): Unit = _trrWithAny.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun trr(arg: Int): Unit = _trrWithInt.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun urr(): Unit = _urrWithVoid.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun urr(arg: U): Unit = _urrWithU.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun urr(arg: W): Unit = _urrWithW.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <U : CharSequence?> urr(arg: U): Unit = _urrWithZUCharSequence.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Int> krr(arg: T): Unit = _krrWithTInt.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Int?> krr(arg: T): Unit = _krrWithZTInt.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> krr(arg: T): Unit = _krrWithZTAny.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun foo(payload: Any): String = _fooWithAny.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithZTAny.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> foo(vararg payload: T): Unit = _fooWithZTAnys.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun lol(arg: Int): Unit = _lolWithInt.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun lol(vararg payload: Array<out Any>): Unit = _lolWithArrays.invoke(payload) {
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
    public override suspend fun <T : Int> bla(): T = _blaWithVoid.invoke() as T

    public override suspend fun <T : Int> bla(payload: T): Unit = _blaWithTInt.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override suspend fun <T : Int> bla(vararg payload: T): Unit = _blaWithTInts.invoke(payload)
    {
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

    public override fun blubb(
        arg0: String,
        arg1: CharArray,
        arg2: Boolean,
        arg3: Int,
    ): Unit = _blubbWithStringCharArrayBooleanInt.invoke(arg0, arg1, arg2, arg3) {
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
    public override suspend fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithVoid.invoke() as T

    public override suspend fun <T> tzz(payload: T): Unit where T : SomeGeneric<String>?, T :
    List<String>? = _tzzWithZTSomeGenericList.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override suspend fun <T> tzz(vararg payload: T): Unit where T : SomeGeneric<String>?, T :
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
    public override suspend fun <T : R, R> oss(arg0: T): R = _ossWithTAny.invoke(arg0) as R

    public override suspend fun <T : R, R> oss(arg0: T, arg1: R): Unit =
        _ossWithTAnyZRAny.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override suspend fun <T : R, R> oss(arg0: R, vararg arg1: T): Unit =
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
    public override suspend fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithZTAny.invoke(arg0) as R

    public override suspend fun <R, T> iss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
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

    public override fun <R, T> xss(arg0: T, arg1: R): Unit where R : Sequence<Char>?, R : CharSequence
        = _xssWithZTAnyRSequenceCharSequence.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _template.clear()
        _trrWithVoid.clear()
        _trrWithAny.clear()
        _trrWithInt.clear()
        _urrWithVoid.clear()
        _urrWithU.clear()
        _urrWithW.clear()
        _urrWithZUCharSequence.clear()
        _krrWithTInt.clear()
        _krrWithZTInt.clear()
        _krrWithZTAny.clear()
        _fooWithAny.clear()
        _fooWithVoid.clear()
        _fooWithZTAny.clear()
        _fooWithZTAnys.clear()
        _lolWithInt.clear()
        _lolWithArrays.clear()
        _brassWithVoid.clear()
        _brassWithTComparable.clear()
        _brassWithTComparables.clear()
        _blaWithVoid.clear()
        _blaWithTInt.clear()
        _blaWithTInts.clear()
        _barWithVoid.clear()
        _barWithTList.clear()
        _barWithTLists.clear()
        _blubbWithStringCharArrayBooleanInt.clear()
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

    @Suppress("UNCHECKED_CAST")
    @KMockExperimental
    public fun <Property> propertyProxyOf(reference: KProperty<Property>):
        KMockContract.PropertyProxy<Property> = (referenceStore["""${reference.name}|property"""] ?:
    throw IllegalStateException("""Unknown property ${reference.name}!""")) as
        tech.antibytes.kmock.KMockContract.PropertyProxy<Property>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf0")
    public fun syncFunProxyOf(reference: () -> Unit, hint: Hint0):
        KMockContract.FunProxy<Unit, () -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, () -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf1")
    public fun syncFunProxyOf(reference: (Any) -> Unit, hint: Hint1<Any>):
        KMockContract.FunProxy<Unit, (Any) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Any) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf2")
    public fun syncFunProxyOf(reference: (Int) -> Unit, hint: Hint1<Int>):
        KMockContract.FunProxy<Unit, (Int) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Int) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Int) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Int) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf3")
    public fun syncFunProxyOf(reference: (U) -> Unit, hint: Hint1<Int>):
        KMockContract.FunProxy<Unit, (U) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(U) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (U) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (U) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf4")
    public fun syncFunProxyOf(reference: (W) -> Unit, hint: Hint1<Any>):
        KMockContract.FunProxy<Unit, (W) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(W) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (W) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (W) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf5")
    public fun <U : CharSequence?> syncFunProxyOf(reference: (U) -> Unit, hint: Hint1<CharSequence>):
        KMockContract.FunProxy<Unit, (CharSequence?) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.CharSequence?) -> kotlin.Unit|[[kotlin.CharSequence?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.CharSequence?) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.CharSequence?) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf6")
    public fun <T : Int> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (Int) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Int) -> kotlin.Unit|[[kotlin.Int]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Int) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Int) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf7")
    public fun <T : Int?> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<Int>):
        KMockContract.FunProxy<Unit, (Int?) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Int?) -> kotlin.Unit|[[kotlin.Int?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Int?) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Int?) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf8")
    public fun <T> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<Any>):
        KMockContract.FunProxy<Unit, (Any?) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any?) -> kotlin.Unit|[[kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any?) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Any?) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf9")
    public fun syncFunProxyOf(reference: (Any) -> String, hint: Hint1<Any>):
        KMockContract.FunProxy<String, (Any) -> String> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any) -> kotlin.String|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any) -> kotlin.String!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.String, (kotlin.Any) -> kotlin.String>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf10")
    public fun <T> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<Any?, () -> Any?> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Any?|[[kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.Any?!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any?, () -> kotlin.Any?>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf11")
    public fun <T> syncFunProxyOf(reference: (Array<out T>) -> Unit, hint: Hint1<Array<out T>>):
        KMockContract.FunProxy<Unit, (kotlin.Array<out kotlin.Any?>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.Any?>) -> kotlin.Unit|[[kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.Any?>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out kotlin.Any?>) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf12")
    public fun syncFunProxyOf(reference: (Array<out kotlin.Array<out kotlin.Any>>) -> Unit,
        hint: Hint1<Array<out kotlin.Array<out kotlin.Any>>>):
        KMockContract.FunProxy<Unit, (kotlin.Array<out kotlin.Array<out kotlin.Any>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.Array<out kotlin.Any>>) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.Array<out kotlin.Any>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out kotlin.Array<out
        kotlin.Any>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf13")
    public fun <T : Comparable<List<Array<T>>>> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>, () -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>|[[kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>!"""))
            as
            tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>,
                    () -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf14")
    public fun <T : Comparable<List<Array<T>>>> syncFunProxyOf(reference: (T) -> Unit,
        hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>) -> Unit>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>) -> kotlin.Unit|[[kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf15")
    public fun <T : Comparable<List<Array<T>>>> syncFunProxyOf(reference: (Array<out T>) -> Unit,
        hint: Hint1<Array<out T>>): KMockContract.FunProxy<Unit, (kotlin.Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>) -> kotlin.Unit|[[kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf16")
    public fun <T : List<Array<String>>> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () -> kotlin.collections.List<kotlin.Array<kotlin.String>>>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.collections.List<kotlin.Array<kotlin.String>>|[[kotlin.collections.List<kotlin.Array<kotlin.String>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.collections.List<kotlin.Array<kotlin.String>>!"""))
            as
            tech.antibytes.kmock.KMockContract.FunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>,
                    () -> kotlin.collections.List<kotlin.Array<kotlin.String>>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf17")
    public fun <T : List<Array<String>>> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.collections.List<kotlin.Array<kotlin.String>>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.String>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.collections.List<kotlin.Array<kotlin.String>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (kotlin.collections.List<kotlin.Array<kotlin.String>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf18")
    public fun <T : List<Array<String>>> syncFunProxyOf(reference: (Array<out T>) -> Unit,
        hint: Hint1<Array<out T>>): KMockContract.FunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.String>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out
        kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf19")
    public fun syncFunProxyOf(reference: (
        String,
        CharArray,
        Boolean,
        Int,
    ) -> Unit, hint: Hint4<String, CharArray, Boolean, Int>): KMockContract.FunProxy<Unit, (
        String,
        CharArray,
        Boolean,
        Int,
    ) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(  kotlin.String,  kotlin.CharArray,  kotlin.Boolean,  kotlin.Int,) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (  kotlin.String,  kotlin.CharArray,  kotlin.Boolean,  kotlin.Int,) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (
            kotlin.String,
            kotlin.CharArray,
            kotlin.Boolean,
            kotlin.Int,
        ) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf20")
    public fun <T : List<Array<String?>>> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () -> kotlin.collections.List<kotlin.Array<kotlin.String?>>>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.collections.List<kotlin.Array<kotlin.String?>>|[[kotlin.collections.List<kotlin.Array<kotlin.String?>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.collections.List<kotlin.Array<kotlin.String?>>!"""))
            as
            tech.antibytes.kmock.KMockContract.FunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>,
                    () -> kotlin.collections.List<kotlin.Array<kotlin.String?>>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf21")
    public fun <T : List<Array<String?>>> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) -> Unit>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.collections.List<kotlin.Array<kotlin.String?>>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.String?>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.collections.List<kotlin.Array<kotlin.String?>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (kotlin.collections.List<kotlin.Array<kotlin.String?>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf22")
    public fun <T : List<Array<String?>>> syncFunProxyOf(reference: (Array<out T>) -> Unit,
        hint: Hint1<Array<out T>>): KMockContract.FunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.String?>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out
        kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf23")
    public fun <T : List<Array<Int>>?> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?|[[kotlin.collections.List<kotlin.Array<kotlin.Int>>?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?!"""))
            as
            tech.antibytes.kmock.KMockContract.FunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?,
                    () -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf24")
    public fun <T : List<Array<Int>>?> syncFunProxyOf(reference: (T) -> Unit,
        hint: Hint1<List<Array<Int>>>):
        KMockContract.FunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.Int>>?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf25")
    public fun <T : List<Array<Int>>?> syncFunProxyOf(reference: (Array<out T>) -> Unit,
        hint: Hint1<Array<out T>>): KMockContract.FunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.Int>>?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf26")
    public fun <T : List<Array<Int>?>> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () -> kotlin.collections.List<kotlin.Array<kotlin.Int>?>>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.collections.List<kotlin.Array<kotlin.Int>?>|[[kotlin.collections.List<kotlin.Array<kotlin.Int>?>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.collections.List<kotlin.Array<kotlin.Int>?>!"""))
            as
            tech.antibytes.kmock.KMockContract.FunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>,
                    () -> kotlin.collections.List<kotlin.Array<kotlin.Int>?>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf27")
    public fun <T : List<Array<Int>?>> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.collections.List<kotlin.Array<kotlin.Int>?>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.Int>?>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf28")
    public fun <T : List<Array<Int>?>> syncFunProxyOf(reference: (Array<out T>) -> Unit,
        hint: Hint1<Array<out T>>): KMockContract.FunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.Int>?>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf29")
    public fun <T : List<Array<Int>>> syncFunProxyOf(reference: () -> T?, hint: Hint0):
        KMockContract.FunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?|[[kotlin.collections.List<kotlin.Array<kotlin.Int>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?!"""))
            as
            tech.antibytes.kmock.KMockContract.FunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?,
                    () -> kotlin.collections.List<kotlin.Array<kotlin.Int>>?>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf30")
    public fun <T : List<Array<Int>>> syncFunProxyOf(reference: (T?) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.Int>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf31")
    public fun <T : List<Array<Int>>> syncFunProxyOf(reference: (Array<out T?>) -> Unit,
        hint: Hint1<Array<out T?>>): KMockContract.FunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit|[[kotlin.collections.List<kotlin.Array<kotlin.Int>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf32")
    public fun <T : L> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<L, () -> L> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> L|[[L]]"""] ?:
        throw IllegalStateException("""Unknown method ${reference.name} with signature () -> L!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<L, () -> L>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf33")
    public fun <T : L> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (L) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(L) -> kotlin.Unit|[[L]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (L) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (L) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf34")
    public fun <T : L> syncFunProxyOf(reference: (Array<out T>) -> Unit, hint: Hint1<Array<out T>>):
        KMockContract.FunProxy<Unit, (kotlin.Array<out L>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out L>) -> kotlin.Unit|[[L]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out L>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out L>) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf35")
    public fun <T : Comparable<List<Array<T>>>?> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?, () -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?|[[kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?!"""))
            as
            tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?,
                    () -> kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf36")
    public fun <T : Comparable<List<Array<T>>>?> syncFunProxyOf(reference: (T) -> Unit,
        hint: Hint1<Comparable<List<Array<T>>>>):
        KMockContract.FunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) -> Unit>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) -> kotlin.Unit|[[kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf37")
    public fun <T : Comparable<List<Array<T>>>?> syncFunProxyOf(reference: (Array<out T>) -> Unit,
        hint: Hint1<Array<out T>>): KMockContract.FunProxy<Unit, (kotlin.Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>) -> kotlin.Unit|[[kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf38")
    public fun <T : Map<String, String>> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<kotlin.collections.Map<kotlin.String,
            kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.collections.Map<kotlin.String, kotlin.String>|[[kotlin.collections.Map<kotlin.String, kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.collections.Map<kotlin.String, kotlin.String>!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.collections.Map<kotlin.String,
            kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf39")
    public fun <T : Map<String, String>> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (kotlin.collections.Map<kotlin.String, kotlin.String>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.Unit|[[kotlin.collections.Map<kotlin.String, kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf40")
    public fun <T : Map<String, String>> syncFunProxyOf(reference: (Array<out T>) -> Unit,
        hint: Hint1<Array<out T>>): KMockContract.FunProxy<Unit, (kotlin.Array<out
    kotlin.collections.Map<kotlin.String, kotlin.String>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit|[[kotlin.collections.Map<kotlin.String, kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out
        kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf41")
    public fun <T> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<Any, () -> Any> where T : SomeGeneric<String>, T : List<String> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Any|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.List<kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, () -> kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf42")
    public fun <T> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (Any) -> Unit> where T : SomeGeneric<String>, T : List<String> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.List<kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Any) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf43")
    public fun <T> syncFunProxyOf(reference: (Array<out T>) -> Unit, hint: Hint1<Array<out T>>):
        KMockContract.FunProxy<Unit, (kotlin.Array<out kotlin.Any>) -> Unit> where T :
                                                                                   SomeGeneric<String>, T : List<String> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.Any>) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.List<kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.Any>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out kotlin.Any>) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf44")
    public fun <T> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<Any, () -> Any> where T : SomeGeneric<String>, T : List<String>? =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Any|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.List<kotlin.String>?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, () -> kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf45")
    public fun <T> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (Any) -> Unit> where T : SomeGeneric<String>, T : List<String>? =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.List<kotlin.String>?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Any) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf46")
    public fun <T> syncFunProxyOf(reference: (Array<out T>) -> Unit, hint: Hint1<Array<out T>>):
        KMockContract.FunProxy<Unit, (kotlin.Array<out kotlin.Any>) -> Unit> where T :
                                                                                   SomeGeneric<String>, T : List<String>? =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.Any>) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.List<kotlin.String>?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.Any>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out kotlin.Any>) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf47")
    public fun <T> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<Any, () -> Any> where T : SomeGeneric<String>, T : Map<String, String>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Any|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.Map<kotlin.String, kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, () -> kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf48")
    public fun <T> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (Any) -> Unit> where T : SomeGeneric<String>, T :
    Map<String, String> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.Map<kotlin.String, kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Any) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf49")
    public fun <T> syncFunProxyOf(reference: (Array<out T>) -> Unit, hint: Hint1<Array<out T>>):
        KMockContract.FunProxy<Unit, (kotlin.Array<out kotlin.Any>) -> Unit> where T :
                                                                                   SomeGeneric<String>, T : Map<String, String> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.Any>) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.collections.Map<kotlin.String, kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.Any>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out kotlin.Any>) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf50")
    public fun <T> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<Any, () -> Any> where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Any|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, () -> kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf51")
    public fun <T> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (Any) -> Unit> where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Any) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf52")
    public fun <T> syncFunProxyOf(reference: (Array<out T>) -> Unit, hint: Hint1<Array<out T>>):
        KMockContract.FunProxy<Unit, (kotlin.Array<out kotlin.Any>) -> Unit> where T :
                                                                                   SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.Any>) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.Any>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Array<out kotlin.Any>) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf53")
    public fun <T : R, R> syncFunProxyOf(reference: (T) -> R, hint: Hint1<T>):
        KMockContract.FunProxy<Any, (Any) -> Any> where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any) -> kotlin.Any|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<R>>>], [mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<R>>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any) -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (kotlin.Any) -> kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf54")
    public fun <T : R, R> syncFunProxyOf(reference: (T, R) -> Unit, hint: Hint2<T, R>):
        KMockContract.FunProxy<Unit, (Any, Any) -> Unit> where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any, kotlin.Any) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<R>>>], [mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<R>>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any, kotlin.Any) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Any, kotlin.Any) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf55")
    public fun <R : T, T : X, X : SomeGeneric<String>> syncFunProxyOf(reference: (T) -> R,
        hint: Hint1<T>):
        KMockContract.FunProxy<mock.template.access.SomeGeneric<kotlin.String>, (mock.template.access.SomeGeneric<kotlin.String>) -> mock.template.access.SomeGeneric<kotlin.String>>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.access.SomeGeneric<kotlin.String>) -> mock.template.access.SomeGeneric<kotlin.String>|[[mock.template.access.SomeGeneric<kotlin.String>], [mock.template.access.SomeGeneric<kotlin.String>], [mock.template.access.SomeGeneric<kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (mock.template.access.SomeGeneric<kotlin.String>) -> mock.template.access.SomeGeneric<kotlin.String>!"""))
            as
            tech.antibytes.kmock.KMockContract.FunProxy<mock.template.access.SomeGeneric<kotlin.String>,
                    (mock.template.access.SomeGeneric<kotlin.String>) ->
            mock.template.access.SomeGeneric<kotlin.String>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf56")
    public fun <R : T, T : X, X : SomeGeneric<String>> syncFunProxyOf(reference: (T, R) -> Unit,
        hint: Hint2<T, R>):
        KMockContract.FunProxy<Unit, (mock.template.access.SomeGeneric<kotlin.String>,
            mock.template.access.SomeGeneric<kotlin.String>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.access.SomeGeneric<kotlin.String>, mock.template.access.SomeGeneric<kotlin.String>) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String>], [mock.template.access.SomeGeneric<kotlin.String>], [mock.template.access.SomeGeneric<kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (mock.template.access.SomeGeneric<kotlin.String>, mock.template.access.SomeGeneric<kotlin.String>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (mock.template.access.SomeGeneric<kotlin.String>,
            mock.template.access.SomeGeneric<kotlin.String>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf57")
    public fun <R, T> syncFunProxyOf(reference: (T) -> R, hint: Hint1<Any>):
        KMockContract.FunProxy<Any, (Any?) -> Any> where R : Sequence<Char>, R : CharSequence =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any?) -> kotlin.Any|[[kotlin.sequences.Sequence<kotlin.Char> & kotlin.CharSequence], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any?) -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (kotlin.Any?) -> kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf58")
    public fun <R, T> syncFunProxyOf(reference: (T, R) -> Unit, hint: Hint2<Any, R>):
        KMockContract.FunProxy<Unit, (Any?, Any) -> Unit> where R : Sequence<Char>?, R : CharSequence
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any?, kotlin.Any) -> kotlin.Unit|[[kotlin.sequences.Sequence<kotlin.Char>? & kotlin.CharSequence], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any?, kotlin.Any) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Any?, kotlin.Any) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf0")
    public fun <T : Int> asyncFunProxyOf(reference: suspend () -> T, hint: Hint0):
        KMockContract.FunProxy<Int, suspend () -> Int> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend () -> kotlin.Int|[[kotlin.Int]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend () -> kotlin.Int!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Int, suspend () -> kotlin.Int>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf1")
    public fun <T : Int> asyncFunProxyOf(reference: suspend (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, suspend (Int) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.Int) -> kotlin.Unit|[[kotlin.Int]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (kotlin.Int) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, suspend (kotlin.Int) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf2")
    public fun <T : Int> asyncFunProxyOf(reference: suspend (Array<out T>) -> Unit,
        hint: Hint1<Array<out T>>): KMockContract.FunProxy<Unit, suspend (IntArray) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.IntArray) -> kotlin.Unit|[[kotlin.Int]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (kotlin.IntArray) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, suspend (kotlin.IntArray) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf3")
    public fun <T> asyncFunProxyOf(reference: suspend () -> T, hint: Hint0):
        KMockContract.FunProxy<Any?, suspend () -> Any?> where T : SomeGeneric<String>?, T :
    List<String>? =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend () -> kotlin.Any?|[[mock.template.access.SomeGeneric<kotlin.String>? & kotlin.collections.List<kotlin.String>?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend () -> kotlin.Any?!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any?, suspend () -> kotlin.Any?>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf4")
    public fun <T> asyncFunProxyOf(reference: suspend (T) -> Unit, hint: Hint1<Any>):
        KMockContract.FunProxy<Unit, suspend (Any?) -> Unit> where T : SomeGeneric<String>?, T :
    List<String>? =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.Any?) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String>? & kotlin.collections.List<kotlin.String>?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (kotlin.Any?) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, suspend (kotlin.Any?) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf5")
    public fun <T> asyncFunProxyOf(reference: suspend (Array<out T>) -> Unit, hint: Hint1<Array<out
    T>>): KMockContract.FunProxy<Unit, suspend (kotlin.Array<out kotlin.Any?>) -> Unit> where T :
                                                                                              SomeGeneric<String>?, T : List<String>? =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.Array<out kotlin.Any?>) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String>? & kotlin.collections.List<kotlin.String>?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (kotlin.Array<out kotlin.Any?>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, suspend (kotlin.Array<out
        kotlin.Any?>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf6")
    public fun <T : R, R> asyncFunProxyOf(reference: suspend (T) -> R, hint: Hint1<Any>):
        KMockContract.FunProxy<Any?, suspend (Any?) -> Any?> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.Any?) -> kotlin.Any?|[[kotlin.Any?], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (kotlin.Any?) -> kotlin.Any?!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any?, suspend (kotlin.Any?) ->
        kotlin.Any?>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf7")
    public fun <T : R, R> asyncFunProxyOf(reference: suspend (T, R) -> Unit, hint: Hint2<Any, Any>):
        KMockContract.FunProxy<Unit, suspend (Any?, Any?) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.Any?, kotlin.Any?) -> kotlin.Unit|[[kotlin.Any?], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (kotlin.Any?, kotlin.Any?) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, suspend (kotlin.Any?,
            kotlin.Any?) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf8")
    public fun <T : R, R> asyncFunProxyOf(reference: suspend (R, Array<out T>) -> Unit,
        hint: Hint2<Any, Array<out T>>): KMockContract.FunProxy<Unit, suspend (Any?, kotlin.Array<out
    kotlin.Any?>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.Any?, kotlin.Array<out kotlin.Any?>) -> kotlin.Unit|[[kotlin.Any?], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (kotlin.Any?, kotlin.Array<out kotlin.Any?>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, suspend (kotlin.Any?,
            kotlin.Array<out kotlin.Any?>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf9")
    public fun <R, T> asyncFunProxyOf(reference: suspend (T) -> R, hint: Hint1<Any>):
        KMockContract.FunProxy<Any, suspend (Any?) -> Any> where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.Any?) -> kotlin.Any|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (kotlin.Any?) -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, suspend (kotlin.Any?) ->
        kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf10")
    public fun <R, T> asyncFunProxyOf(reference: suspend (T, R) -> Unit, hint: Hint2<Any, R>):
        KMockContract.FunProxy<Unit, suspend (Any?, Any) -> Unit> where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.Any?, kotlin.Any) -> kotlin.Unit|[[mock.template.access.SomeGeneric<kotlin.String> & kotlin.Comparable<kotlin.collections.List<kotlin.Array<T>>>], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (kotlin.Any?, kotlin.Any) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, suspend (kotlin.Any?,
            kotlin.Any) -> kotlin.Unit>
}
