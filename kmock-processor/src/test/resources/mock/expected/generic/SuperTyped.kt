package mock.template.generic

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.CharArray
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SuperTypedMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: SuperTyped<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : SuperTyped<K, L> where L : Any, L : Comparable<L> {
    public val _pptWithZTAnys: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithZTAnys",
            collector = verifier, freeze = freeze)

    public val _pptWithTCharSequenceComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithTCharSequenceComparables",
            collector = verifier, freeze = freeze)

    public val _pptWithTComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.Any?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithTComparables",
            collector = verifier, freeze = freeze)

    public val _pptWithZTAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithZTAny",
            collector = verifier, freeze = freeze)

    public val _pptWithTCharSequenceComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Any) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithTCharSequenceComparable",
            collector = verifier, freeze = freeze)

    public val _pptWithTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithTComparable",
            collector = verifier, freeze = freeze)

    public val _lolWithZKAnyTComparables: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, Array<out
    kotlin.Comparable<kotlin.Any?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_lolWithZKAnyTComparables",
            collector = verifier, freeze = freeze)

    public val _lolWithTAnys: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_lolWithTAnys",
            collector = verifier, freeze = freeze)

    public val _lolWithZKAnyTComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Any?,
        kotlin.Comparable<kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_lolWithZKAnyTComparable",
            collector = verifier, freeze = freeze)

    public val _lolWithTAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_lolWithTAny", collector
        = verifier, freeze = freeze)

    public val _buzz: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_buzz", collector =
        verifier, freeze = freeze)

    public val _narv: KMockContract.SyncFunProxy<Unit, (Array<out L>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_narv", collector =
        verifier, freeze = freeze)

    public val _ooWithIntAnys: KMockContract.SyncFunProxy<Any, (kotlin.Int, Array<out kotlin.Any>) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_ooWithIntAnys",
            collector = verifier, freeze = freeze)

    public val _ooWithAnyInts: KMockContract.SyncFunProxy<Any, (kotlin.Any?, kotlin.IntArray) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_ooWithAnyInts",
            collector = verifier, freeze = freeze)

    public val _ooWithAnyZTAnys: KMockContract.SyncFunProxy<Any, (kotlin.Any, Array<out
    kotlin.Any?>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_ooWithAnyZTAnys",
            collector = verifier, freeze = freeze)

    public val _ooWithAnyRRRs: KMockContract.SyncFunProxy<Any, (kotlin.Any, Array<out
    mock.template.generic.RRR<kotlin.Any?>>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_ooWithAnyRRRs",
            collector = verifier, freeze = freeze)

    public val _ooWithAnyCharArray: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.CharArray) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_ooWithAnyCharArray",
            collector = verifier, freeze = freeze)

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

    public override fun <T> ppt(x: T): Unit where T : CharSequence, T : Comparable<T> =
        _pptWithTCharSequenceComparable.invoke(x) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<T>> ppt(x: T): Unit = _pptWithTComparable.invoke(x) {
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

    public override fun narv(vararg x: L): Unit = _narv.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun oo(fuzz: Int, vararg ozz: Any): Any = _ooWithIntAnys.invoke(fuzz, ozz)

    public override fun oo(fuzz: Any?, vararg ozz: Int): Any = _ooWithAnyInts.invoke(fuzz, ozz)

    public override fun <T> oo(fuzz: Any, vararg ozz: T): Any = _ooWithAnyZTAnys.invoke(fuzz, ozz)

    public override fun <T> oo(fuzz: Any, vararg ozz: RRR<T>): Any = _ooWithAnyRRRs.invoke(fuzz, ozz)

    public override fun <T> oo(fuzz: Any, ozz: CharArray): Any = _ooWithAnyCharArray.invoke(fuzz, ozz)

    public fun _clearMock(): Unit {
        _pptWithZTAnys.clear()
        _pptWithTCharSequenceComparables.clear()
        _pptWithTComparables.clear()
        _pptWithZTAny.clear()
        _pptWithTCharSequenceComparable.clear()
        _pptWithTComparable.clear()
        _lolWithZKAnyTComparables.clear()
        _lolWithTAnys.clear()
        _lolWithZKAnyTComparable.clear()
        _lolWithTAny.clear()
        _buzz.clear()
        _narv.clear()
        _ooWithIntAnys.clear()
        _ooWithAnyInts.clear()
        _ooWithAnyZTAnys.clear()
        _ooWithAnyRRRs.clear()
        _ooWithAnyCharArray.clear()
    }
}
