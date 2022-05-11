package mock.template.generic

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
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
    public val _pptWithAnys: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) -> kotlin.Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithAnys",
        collector = verifier, freeze = freeze)

    public val _pptWithCharSequenceComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Any>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithCharSequenceComparables",
            collector = verifier, freeze = freeze)

    public val _pptWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<Any?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithComparables",
            collector = verifier, freeze = freeze)

    public val _pptWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithAny", collector
        = verifier, freeze = freeze)

    public val _pptWithCharSequenceComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Any) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithCharSequenceComparable",
            collector = verifier, freeze = freeze)

    public val _pptWithComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<Any?>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_pptWithComparable",
            collector = verifier, freeze = freeze)

    public val _lolWithAnyComparables: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, Array<out
    kotlin.Comparable<Any?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_lolWithAnyComparables",
            collector = verifier, freeze = freeze)

    public val _lolWithAnys: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) -> kotlin.Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_lolWithAnys",
        collector = verifier, freeze = freeze)

    public val _lolWithAnyComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Any?,
        kotlin.Comparable<Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_lolWithAnyComparable",
            collector = verifier, freeze = freeze)

    public val _lolWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_lolWithAny", collector
        = verifier, freeze = freeze)

    public val _buzz: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_buzz", collector =
        verifier, freeze = freeze)

    public val _narv: KMockContract.SyncFunProxy<Unit, (Array<out L>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SuperTypedMock#_narv", collector =
        verifier, freeze = freeze)

    public override fun <T> ppt(vararg x: T): Unit = _pptWithAnys.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> ppt(vararg x: T): Unit where T : CharSequence, T : Comparable<T> =
        _pptWithCharSequenceComparables.invoke(x) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<T>> ppt(vararg x: T): Unit = _pptWithComparables.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> ppt(x: T): Unit = _pptWithAny.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> ppt(x: T): Unit where T : CharSequence, T : Comparable<T> =
        _pptWithCharSequenceComparable.invoke(x) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<T>> ppt(x: T): Unit = _pptWithComparable.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Comparable<T>, K> lol(arg: K, vararg x: T): Unit =
        _lolWithAnyComparables.invoke(arg, x) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : K, K> lol(vararg x: T): Unit = _lolWithAnys.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Comparable<T>, K> lol(arg: K, x: T): Unit =
        _lolWithAnyComparable.invoke(arg, x) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : K, K> lol(x: T): Unit = _lolWithAny.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <nulled : List<Array<Int>>> buzz(vararg payload: nulled?): Unit =
        _buzz.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun narv(vararg x: L): Unit = _narv.invoke(x) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _pptWithAnys.clear()
        _pptWithCharSequenceComparables.clear()
        _pptWithComparables.clear()
        _pptWithAny.clear()
        _pptWithCharSequenceComparable.clear()
        _pptWithComparable.clear()
        _lolWithAnyComparables.clear()
        _lolWithAnys.clear()
        _lolWithAnyComparable.clear()
        _lolWithAny.clear()
        _buzz.clear()
        _narv.clear()
    }
}
