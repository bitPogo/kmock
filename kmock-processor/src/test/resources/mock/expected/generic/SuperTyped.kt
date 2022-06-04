package mock.template.generic

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.CharArray
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Int
import kotlin.IntArray
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SuperTypedMock<K : Any, L>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: SuperTyped<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : SuperTyped<K, L> where L : Any, L : Comparable<L> {
    public val _pptWithZTAnys: KMockContract.SyncFunProxy<Unit, (Array<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithZTAnys",
            collector = collector, freeze = freeze)

    public val _pptWithTCharSequenceComparables:
        KMockContract.SyncFunProxy<Unit, (Array<out Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithTCharSequenceComparables",
            collector = collector, freeze = freeze)

    public val _pptWithTComparables:
        KMockContract.SyncFunProxy<Unit, (Array<out Comparable<Any>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithTComparables",
            collector = collector, freeze = freeze)

    public val _pptWithZTAny: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithZTAny",
            collector = collector, freeze = freeze)

    public val _pptWithZTCharSequenceComparable: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithZTCharSequenceComparable",
            collector = collector, freeze = freeze)

    public val _pptWithZTComparable: KMockContract.SyncFunProxy<Unit, (Comparable<Any?>?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithZTComparable",
            collector = collector, freeze = freeze)

    public val _lolWithZKAnyTComparables: KMockContract.SyncFunProxy<Unit, (Any?,
        Array<out Comparable<Any>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_lolWithZKAnyTComparables",
            collector = collector, freeze = freeze)

    public val _lolWithTAnys: KMockContract.SyncFunProxy<Unit, (Array<out K>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_lolWithTAnys",
            collector = collector, freeze = freeze)

    public val _lolWithZKAnyTComparable: KMockContract.SyncFunProxy<Unit, (Any?,
        Comparable<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_lolWithZKAnyTComparable",
            collector = collector, freeze = freeze)

    public val _lolWithTAny: KMockContract.SyncFunProxy<Unit, (K) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_lolWithTAny", collector
        = collector, freeze = freeze)

    public val _buzz: KMockContract.SyncFunProxy<Unit, (Array<out List<Array<Int>>?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_buzz", collector =
        collector, freeze = freeze)

    public val _narvWithLs: KMockContract.SyncFunProxy<Unit, (Array<out L>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_narvWithLs", collector
        = collector, freeze = freeze)

    public val _narvWithArrays: KMockContract.SyncFunProxy<Unit, (Array<out Array<out Any>>) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_narvWithArrays",
        collector = collector, freeze = freeze)

    public val _narvWithIntArrays: KMockContract.SyncFunProxy<Unit, (Array<out IntArray>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_narvWithIntArrays",
            collector = collector, freeze = freeze)

    public val _ooWithIntAnys: KMockContract.SyncFunProxy<Any, (Int, Array<out Any>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_ooWithIntAnys",
            collector = collector, freeze = freeze)

    public val _ooWithAnyInts: KMockContract.SyncFunProxy<Any, (Any?, IntArray) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_ooWithAnyInts",
            collector = collector, freeze = freeze)

    public val _ooWithAnyZTAnys: KMockContract.SyncFunProxy<Any, (Any, Array<*>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_ooWithAnyZTAnys",
            collector = collector, freeze = freeze)

    public val _ooWithAnyRRRs: KMockContract.SyncFunProxy<Any, (Any, Array<out RRR<*>>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_ooWithAnyRRRs",
            collector = collector, freeze = freeze)

    public val _ooWithAnyCharArray: KMockContract.SyncFunProxy<Any, (Any, CharArray) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_ooWithAnyCharArray",
            collector = collector, freeze = freeze)

    public val _roo: KMockContract.SyncFunProxy<Any, (Any, Array<out RRR<*>>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_roo", collector =
        collector, freeze = freeze)

    public override fun <T> ppt(vararg x: T): Unit = _pptWithZTAnys.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> ppt(vararg x: T): Unit where T : CharSequence, T : Comparable<T> =
        _pptWithTCharSequenceComparables.invoke(x) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<T>> ppt(vararg x: T): Unit = _pptWithTComparables.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> ppt(x: T): Unit = _pptWithZTAny.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> ppt(x: T): Unit where T : CharSequence?, T : Comparable<T>? =
        _pptWithZTCharSequenceComparable.invoke(x) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<T>?> ppt(x: T): Unit = _pptWithZTComparable.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Comparable<T>, K> lol(arg: K, vararg x: T): Unit =
        _lolWithZKAnyTComparables.invoke(arg, x) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : K, K> lol(vararg x: T): Unit = _lolWithTAnys.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Comparable<T>, K> lol(arg: K, x: T): Unit =
        _lolWithZKAnyTComparable.invoke(arg, x) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : K, K> lol(x: T): Unit = _lolWithTAny.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <nulled : List<Array<Int>>> buzz(vararg payload: nulled?): Unit =
        _buzz.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun narv(vararg x: L): Unit = _narvWithLs.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun narv(vararg x: Array<out Any>): Unit = _narvWithArrays.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun narv(vararg x: IntArray): Unit = _narvWithIntArrays.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun oo(fuzz: Int, vararg ozz: Any): Any = _ooWithIntAnys.invoke(fuzz, ozz)

    public override fun oo(fuzz: Any?, vararg ozz: Int): Any = _ooWithAnyInts.invoke(fuzz, ozz)

    public override fun <T> oo(fuzz: Any, vararg ozz: T): Any = _ooWithAnyZTAnys.invoke(fuzz, ozz)

    public override fun <T> oo(fuzz: Any, vararg ozz: RRR<out T>): Any = _ooWithAnyRRRs.invoke(fuzz,
        ozz)

    public override fun <T> oo(fuzz: Any, ozz: CharArray): Any = _ooWithAnyCharArray.invoke(fuzz, ozz)

    public override fun <T> roo(fuzz: Any, vararg ozz: RRR<*>): Any = _roo.invoke(fuzz, ozz)

    public fun _clearMock(): Unit {
        _pptWithZTAnys.clear()
        _pptWithTCharSequenceComparables.clear()
        _pptWithTComparables.clear()
        _pptWithZTAny.clear()
        _pptWithZTCharSequenceComparable.clear()
        _pptWithZTComparable.clear()
        _lolWithZKAnyTComparables.clear()
        _lolWithTAnys.clear()
        _lolWithZKAnyTComparable.clear()
        _lolWithTAny.clear()
        _buzz.clear()
        _narvWithLs.clear()
        _narvWithArrays.clear()
        _narvWithIntArrays.clear()
        _ooWithIntAnys.clear()
        _ooWithAnyInts.clear()
        _ooWithAnyZTAnys.clear()
        _ooWithAnyRRRs.clear()
        _ooWithAnyCharArray.clear()
        _roo.clear()
    }
}
