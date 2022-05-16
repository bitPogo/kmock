package multi

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
import multi.template.sharedGeneric.Generic1
import multi.template.sharedGeneric.GenericSharedContract
import multi.template.sharedGeneric.SomeGeneric
import multi.template.sharedGeneric.nested.Generic2
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SharedGenericMultiMock<KMockTypeParameter0 : Any, KMockTypeParameter1,
    KMockTypeParameter2 : Any, KMockTypeParameter3, KMockTypeParameter4, KMockTypeParameter5,
    MultiMock>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: MultiMock? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Generic1<KMockTypeParameter0, KMockTypeParameter1>,
    Generic2<KMockTypeParameter2, KMockTypeParameter3>,
    GenericSharedContract.Generic3<KMockTypeParameter4, KMockTypeParameter5> where
KMockTypeParameter1 : Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>,
KMockTypeParameter3 : Any, KMockTypeParameter3 : Comparable<KMockTypeParameter3>, MultiMock :
Generic1<KMockTypeParameter0, KMockTypeParameter1>, MultiMock :
Generic2<KMockTypeParameter2, KMockTypeParameter3>, MultiMock :
GenericSharedContract.Generic3<KMockTypeParameter4, KMockTypeParameter5> {
    public override var template: KMockTypeParameter1
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<KMockTypeParameter1> =
        ProxyFactory.createPropertyProxy("multi.SharedGenericMultiMock#_template", collector =
        verifier, freeze = freeze)

    public override val lol: KMockTypeParameter2
        get() = _lol.onGet()

    public val _lol: KMockContract.PropertyProxy<KMockTypeParameter2> =
        ProxyFactory.createPropertyProxy("multi.SharedGenericMultiMock#_lol", collector = verifier,
            freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_fooWithVoid", collector =
        verifier, freeze = freeze)

    public val _fooWithTAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_fooWithTAny", collector =
        verifier, freeze = freeze)

    public val _fooWithTAnys: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) ->
    kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_fooWithTAnys",
        collector = verifier, freeze = freeze)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_blaWithVoid", collector =
        verifier, freeze = freeze)

    public val _blaWithTInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_blaWithTInt", collector =
        verifier, freeze = freeze)

    public val _blaWithTInts: KMockContract.SyncFunProxy<Unit, (kotlin.IntArray) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_blaWithTInts", collector =
        verifier, freeze = freeze)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_barWithVoid", collector =
        verifier, freeze = freeze)

    public val _barWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_barWithTList",
        collector = verifier, freeze = freeze)

    public val _barWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_barWithTLists", collector =
        verifier, freeze = freeze)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_blubbWithVoid", collector =
        verifier, freeze = freeze)

    public val _blubbWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_blubbWithTList",
        collector = verifier, freeze = freeze)

    public val _blubbWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_blubbWithTLists", collector =
        verifier, freeze = freeze)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_bussWithVoid", collector =
        verifier, freeze = freeze)

    public val _bussWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_bussWithTList",
        collector = verifier, freeze = freeze)

    public val _bussWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_bussWithTLists", collector =
        verifier, freeze = freeze)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_bossWithVoid", collector =
        verifier, freeze = freeze)

    public val _bossWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_bossWithTList",
        collector = verifier, freeze = freeze)

    public val _bossWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_bossWithTLists", collector =
        verifier, freeze = freeze)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_buzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _buzzWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_buzzWithTList",
        collector = verifier, freeze = freeze)

    public val _buzzWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_buzzWithTLists", collector =
        verifier, freeze = freeze)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<KMockTypeParameter1, () ->
    KMockTypeParameter1> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_ozzWithVoid", collector =
        verifier, freeze = freeze)

    public val _ozzWithTKMockTypeParameter3: KMockContract.SyncFunProxy<Unit, (KMockTypeParameter3) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_ozzWithTKMockTypeParameter3",
            collector = verifier, freeze = freeze)

    public val _ozzWithTKMockTypeParameter3s: KMockContract.SyncFunProxy<Unit, (Array<out
    KMockTypeParameter3>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_ozzWithTKMockTypeParameter3s",
            collector = verifier, freeze = freeze)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_brassWithVoid", collector =
        verifier, freeze = freeze)

    public val _brassWithTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_brassWithTComparable",
            collector = verifier, freeze = freeze)

    public val _brassWithTComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_brassWithTComparables",
            collector = verifier, freeze = freeze)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_blissWithVoid", collector =
        verifier, freeze = freeze)

    public val _blissWithTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_blissWithTComparable",
            collector = verifier, freeze = freeze)

    public val _blissWithTComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_blissWithTComparables",
            collector = verifier, freeze = freeze)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_lossWithVoid", collector =
        verifier, freeze = freeze)

    public val _lossWithTMap: KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String,
        kotlin.String>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_lossWithTMap", collector =
        verifier, freeze = freeze)

    public val _lossWithTMaps: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_lossWithTMaps", collector =
        verifier, freeze = freeze)

    public val _uzz: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_uzz", collector = verifier,
            freeze = freeze)

    public val _lzz: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_lzz", collector = verifier,
            freeze = freeze)

    public val _tzz: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_tzz", collector = verifier,
            freeze = freeze)

    public val _rzz: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_rzz", collector = verifier,
            freeze = freeze)

    public val _izz: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_izz", collector = verifier,
            freeze = freeze)

    public val _ossWithTAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_ossWithTAny", collector =
        verifier, freeze = freeze)

    public val _ossWithTAnyRAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_ossWithTAnyRAny", collector =
        verifier, freeze = freeze)

    public val _ossWithRAnyTAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, Array<out
    kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_ossWithRAnyTAnys", collector =
        verifier, freeze = freeze)

    public val _kss: KMockContract.SyncFunProxy<Any, (kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_kss", collector = verifier,
            freeze = freeze)

    public val _iss: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_iss", collector = verifier,
            freeze = freeze)

    public val _pss:
        KMockContract.SyncFunProxy<multi.template.sharedGeneric.SomeGeneric<kotlin.String>, (multi.template.sharedGeneric.SomeGeneric<kotlin.String>) ->
        multi.template.sharedGeneric.SomeGeneric<kotlin.String>> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_pss", collector = verifier,
            freeze = freeze)

    public val _xssWithTAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_xssWithTAny", collector =
        verifier, freeze = freeze)

    public val _xssWithTAnyRSequenceCharSequence: KMockContract.SyncFunProxy<Unit, (kotlin.Any?,
        kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_xssWithTAnyRSequenceCharSequence",
            collector = verifier, freeze = freeze)

    public val _doSomething: KMockContract.SyncFunProxy<KMockTypeParameter5, (KMockTypeParameter4) ->
    KMockTypeParameter5> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_doSomething", collector =
        verifier, freeze = freeze)

    public val _compareTo:
        KMockContract.SyncFunProxy<Int, (multi.template.sharedGeneric.GenericSharedContract.Generic3<KMockTypeParameter5,
            KMockTypeParameter4>) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("multi.SharedGenericMultiMock#_compareTo", collector =
        verifier, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithTAny.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> foo(vararg payload: T): Unit = _fooWithTAnys.invoke(payload) {
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
    public override fun <T : KMockTypeParameter1> ozz(): T = _ozzWithVoid.invoke() as T

    public override fun <T : KMockTypeParameter3> ozz(payload: T): Unit =
        _ozzWithTKMockTypeParameter3.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : KMockTypeParameter3> ozz(vararg payload: T): Unit =
        _ozzWithTKMockTypeParameter3s.invoke(payload) {
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
    public override fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String> = _uzz.invoke()
        as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? = _lzz.invoke()
        as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? = _tzz.invoke()
        as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzz.invoke() as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izz.invoke() as T

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
    Comparable<List<Array<R>>> = _kss.invoke(arg0) as R

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _iss.invoke(arg0) as R

    @Suppress("UNCHECKED_CAST")
    public override fun <R : T, T : X, X : SomeGeneric<String>> pss(arg0: T): R = _pss.invoke(arg0) as
        R

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> xss(arg0: T): R where R : Sequence<Char>, R : CharSequence =
        _xssWithTAny.invoke(arg0) as R

    public override fun <R, T> xss(arg0: T, arg1: R): Unit where R : Sequence<Char>, R : CharSequence
        = _xssWithTAnyRSequenceCharSequence.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun doSomething(arg: KMockTypeParameter4): KMockTypeParameter5 =
        _doSomething.invoke(arg)

    public override
    fun compareTo(other: GenericSharedContract.Generic3<KMockTypeParameter5, KMockTypeParameter4>):
        Int = _compareTo.invoke(other)

    public fun _clearMock(): Unit {
        _template.clear()
        _lol.clear()
        _fooWithVoid.clear()
        _fooWithTAny.clear()
        _fooWithTAnys.clear()
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
        _ozzWithTKMockTypeParameter3.clear()
        _ozzWithTKMockTypeParameter3s.clear()
        _brassWithVoid.clear()
        _brassWithTComparable.clear()
        _brassWithTComparables.clear()
        _blissWithVoid.clear()
        _blissWithTComparable.clear()
        _blissWithTComparables.clear()
        _lossWithVoid.clear()
        _lossWithTMap.clear()
        _lossWithTMaps.clear()
        _uzz.clear()
        _lzz.clear()
        _tzz.clear()
        _rzz.clear()
        _izz.clear()
        _ossWithTAny.clear()
        _ossWithTAnyRAny.clear()
        _ossWithRAnyTAnys.clear()
        _kss.clear()
        _iss.clear()
        _pss.clear()
        _xssWithTAny.clear()
        _xssWithTAnyRSequenceCharSequence.clear()
        _doSomething.clear()
        _compareTo.clear()
    }
}
