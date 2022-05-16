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
import multi.template.commonGeneric.Generic1
import multi.template.commonGeneric.GenericCommonContract
import multi.template.commonGeneric.SomeGeneric
import multi.template.commonGeneric.nested.Generic2
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonGenericMultiMock<KMockTypeParameter0 : Any, KMockTypeParameter1,
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
    GenericCommonContract.Generic3<KMockTypeParameter4, KMockTypeParameter5> where
KMockTypeParameter1 : Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>,
KMockTypeParameter3 : Any, KMockTypeParameter3 : Comparable<KMockTypeParameter3>, MultiMock :
Generic1<KMockTypeParameter0, KMockTypeParameter1>, MultiMock :
Generic2<KMockTypeParameter2, KMockTypeParameter3>, MultiMock :
GenericCommonContract.Generic3<KMockTypeParameter4, KMockTypeParameter5> {
    private val __spyOn: MultiMock? = spyOn

    public override var template: KMockTypeParameter1
        get() = _template.onGet {
            useSpyIf(__spyOn) { __spyOn!!.template }
        }
        set(`value`) = _template.onSet(value) {
            useSpyIf(__spyOn) { __spyOn!!.template = value }
        }

    public val _template: KMockContract.PropertyProxy<KMockTypeParameter1> =
        ProxyFactory.createPropertyProxy("multi.CommonGenericMultiMock#_template", collector =
        verifier, freeze = freeze)

    public override val lol: KMockTypeParameter2
        get() = _lol.onGet {
            useSpyIf(__spyOn) { __spyOn!!.lol }
        }

    public val _lol: KMockContract.PropertyProxy<KMockTypeParameter2> =
        ProxyFactory.createPropertyProxy("multi.CommonGenericMultiMock#_lol", collector = verifier,
            freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_fooWithVoid", collector =
        verifier, freeze = freeze)

    public val _fooWithTAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_fooWithTAny", collector =
        verifier, freeze = freeze)

    public val _fooWithTAnys: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) ->
    kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_fooWithTAnys",
        collector = verifier, freeze = freeze)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blaWithVoid", collector =
        verifier, freeze = freeze)

    public val _blaWithTInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blaWithTInt", collector =
        verifier, freeze = freeze)

    public val _blaWithTInts: KMockContract.SyncFunProxy<Unit, (kotlin.IntArray) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blaWithTInts", collector =
        verifier, freeze = freeze)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_barWithVoid", collector =
        verifier, freeze = freeze)

    public val _barWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_barWithTList",
        collector = verifier, freeze = freeze)

    public val _barWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_barWithTLists", collector =
        verifier, freeze = freeze)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blubbWithVoid", collector =
        verifier, freeze = freeze)

    public val _blubbWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blubbWithTList",
        collector = verifier, freeze = freeze)

    public val _blubbWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blubbWithTLists", collector =
        verifier, freeze = freeze)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_bussWithVoid", collector =
        verifier, freeze = freeze)

    public val _bussWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_bussWithTList",
        collector = verifier, freeze = freeze)

    public val _bussWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_bussWithTLists", collector =
        verifier, freeze = freeze)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_bossWithVoid", collector =
        verifier, freeze = freeze)

    public val _bossWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_bossWithTList",
        collector = verifier, freeze = freeze)

    public val _bossWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_bossWithTLists", collector =
        verifier, freeze = freeze)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_buzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _buzzWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_buzzWithTList",
        collector = verifier, freeze = freeze)

    public val _buzzWithTLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_buzzWithTLists", collector =
        verifier, freeze = freeze)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<KMockTypeParameter1, () ->
    KMockTypeParameter1> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_ozzWithVoid", collector =
        verifier, freeze = freeze)

    public val _ozzWithTKMockTypeParameter3: KMockContract.SyncFunProxy<Unit, (KMockTypeParameter3) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_ozzWithTKMockTypeParameter3",
            collector = verifier, freeze = freeze)

    public val _ozzWithTKMockTypeParameter3s: KMockContract.SyncFunProxy<Unit, (Array<out
    KMockTypeParameter3>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_ozzWithTKMockTypeParameter3s",
            collector = verifier, freeze = freeze)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_brassWithVoid", collector =
        verifier, freeze = freeze)

    public val _brassWithTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_brassWithTComparable",
            collector = verifier, freeze = freeze)

    public val _brassWithTComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_brassWithTComparables",
            collector = verifier, freeze = freeze)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blissWithVoid", collector =
        verifier, freeze = freeze)

    public val _blissWithTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blissWithTComparable",
            collector = verifier, freeze = freeze)

    public val _blissWithTComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blissWithTComparables",
            collector = verifier, freeze = freeze)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_lossWithVoid", collector =
        verifier, freeze = freeze)

    public val _lossWithTMap: KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String,
        kotlin.String>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_lossWithTMap", collector =
        verifier, freeze = freeze)

    public val _lossWithTMaps: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_lossWithTMaps", collector =
        verifier, freeze = freeze)

    public val _uzz: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_uzz", collector = verifier,
            freeze = freeze)

    public val _lzz: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_lzz", collector = verifier,
            freeze = freeze)

    public val _tzz: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_tzz", collector = verifier,
            freeze = freeze)

    public val _rzz: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_rzz", collector = verifier,
            freeze = freeze)

    public val _izz: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_izz", collector = verifier,
            freeze = freeze)

    public val _ossWithTAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_ossWithTAny", collector =
        verifier, freeze = freeze)

    public val _ossWithTAnyRAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_ossWithTAnyRAny", collector =
        verifier, freeze = freeze)

    public val _ossWithRAnyTAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, Array<out
    kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_ossWithRAnyTAnys", collector =
        verifier, freeze = freeze)

    public val _kss: KMockContract.SyncFunProxy<Any, (kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_kss", collector = verifier,
            freeze = freeze)

    public val _iss: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_iss", collector = verifier,
            freeze = freeze)

    public val _pss:
        KMockContract.SyncFunProxy<multi.template.commonGeneric.SomeGeneric<kotlin.String>, (multi.template.commonGeneric.SomeGeneric<kotlin.String>) ->
        multi.template.commonGeneric.SomeGeneric<kotlin.String>> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_pss", collector = verifier,
            freeze = freeze)

    public val _xssWithTAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_xssWithTAny", collector =
        verifier, freeze = freeze)

    public val _xssWithTAnyRSequenceCharSequence: KMockContract.SyncFunProxy<Unit, (kotlin.Any?,
        kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_xssWithTAnyRSequenceCharSequence",
            collector = verifier, freeze = freeze)

    public val _doSomething: KMockContract.SyncFunProxy<KMockTypeParameter5, (KMockTypeParameter4) ->
    KMockTypeParameter5> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_doSomething", collector =
        verifier, freeze = freeze)

    public val _compareTo:
        KMockContract.SyncFunProxy<Int, (multi.template.commonGeneric.GenericCommonContract.Generic3<KMockTypeParameter5,
            KMockTypeParameter4>) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_compareTo", collector =
        verifier, freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_toString", collector =
        verifier, freeze = freeze, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_equals", collector = verifier,
            freeze = freeze, ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_hashCode", collector =
        verifier, freeze = freeze, ignorableForVerification = true)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.foo() }
    } as T

    public override fun <T> foo(payload: T): Unit = _fooWithTAny.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.foo(payload) }
    }

    public override fun <T> foo(vararg payload: T): Unit = _fooWithTAnys.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.foo(*payload) }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Int> bla(): T = _blaWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.bla() }
    } as T

    public override fun <T : Int> bla(payload: T): Unit = _blaWithTInt.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.bla(payload) }
    }

    public override fun <T : Int> bla(vararg payload: T): Unit = _blaWithTInts.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.bla(*payload) }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String>>> bar(): T = _barWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.bar() }
    } as T

    public override fun <T : List<Array<String>>> bar(payload: T): Unit =
        _barWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.bar(payload) }
        }

    public override fun <T : List<Array<String>>> bar(vararg payload: T): Unit =
        _barWithTLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.bar(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String?>>> blubb(): T = _blubbWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.blubb() }
    } as T

    public override fun <T : List<Array<String?>>> blubb(payload: T): Unit =
        _blubbWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.blubb(payload) }
        }

    public override fun <T : List<Array<String?>>> blubb(vararg payload: T): Unit =
        _blubbWithTLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.blubb(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>?> buss(): T = _bussWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.buss() }
    } as T

    public override fun <T : List<Array<Int>>?> buss(payload: T): Unit =
        _bussWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.buss(payload) }
        }

    public override fun <T : List<Array<Int>>?> buss(vararg payload: T): Unit =
        _bussWithTLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.buss(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>?>> boss(): T = _bossWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.boss() }
    } as T

    public override fun <T : List<Array<Int>?>> boss(payload: T): Unit =
        _bossWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.boss(payload) }
        }

    public override fun <T : List<Array<Int>?>> boss(vararg payload: T): Unit =
        _bossWithTLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.boss(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>> buzz(): T? = _buzzWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.buzz() }
    } as T?

    public override fun <T : List<Array<Int>>> buzz(payload: T?): Unit =
        _buzzWithTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.buzz(payload) }
        }

    public override fun <T : List<Array<Int>>> buzz(vararg payload: T?): Unit =
        _buzzWithTLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.buzz(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : KMockTypeParameter1> ozz(): T = _ozzWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.ozz() }
    } as T

    public override fun <T : KMockTypeParameter3> ozz(payload: T): Unit =
        _ozzWithTKMockTypeParameter3.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.ozz(payload) }
        }

    public override fun <T : KMockTypeParameter3> ozz(vararg payload: T): Unit =
        _ozzWithTKMockTypeParameter3s.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.ozz(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>> brass(): T = _brassWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.brass() }
    } as T

    public override fun <T : Comparable<List<Array<T>>>> brass(payload: T): Unit =
        _brassWithTComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.brass(payload) }
        }

    public override fun <T : Comparable<List<Array<T>>>> brass(vararg payload: T): Unit =
        _brassWithTComparables.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.brass(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>?> bliss(): T = _blissWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.bliss() }
    } as T

    public override fun <T : Comparable<List<Array<T>>>?> bliss(payload: T): Unit =
        _blissWithTComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.bliss(payload) }
        }

    public override fun <T : Comparable<List<Array<T>>>?> bliss(vararg payload: T): Unit =
        _blissWithTComparables.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.bliss(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Map<String, String>> loss(): T = _lossWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.loss() }
    } as T

    public override fun <T : Map<String, String>> loss(payload: T): Unit =
        _lossWithTMap.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.loss(payload) }
        }

    public override fun <T : Map<String, String>> loss(vararg payload: T): Unit =
        _lossWithTMaps.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.loss(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String> = _uzz.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.uzz() }
    } as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? = _lzz.invoke()
    {
        useSpyIf(__spyOn) { __spyOn!!.lzz() }
    } as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? = _tzz.invoke()
    {
        useSpyIf(__spyOn) { __spyOn!!.tzz() }
    } as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzz.invoke() {
            useSpyIf(__spyOn) { __spyOn!!.rzz() }
        } as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izz.invoke() {
            useSpyIf(__spyOn) { __spyOn!!.izz() }
        } as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithTAny.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.oss(arg0) }
    } as R

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithTAnyRAny.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.oss(arg0, arg1) }
    }

    public override fun <T : R, R> oss(arg0: R, vararg arg1: T): Unit = _ossWithRAnyTAnys.invoke(arg0,
        arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.oss(arg0, *arg1) }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kss.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.kss(arg0) }
    } as R

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _iss.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.iss(arg0) }
    } as R

    @Suppress("UNCHECKED_CAST")
    public override fun <R : T, T : X, X : SomeGeneric<String>> pss(arg0: T): R = _pss.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.pss(arg0) }
    } as R

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> xss(arg0: T): R where R : Sequence<Char>, R : CharSequence =
        _xssWithTAny.invoke(arg0) {
            useSpyIf(__spyOn) { __spyOn!!.xss(arg0) }
        } as R

    public override fun <R, T> xss(arg0: T, arg1: R): Unit where R : Sequence<Char>, R : CharSequence
        = _xssWithTAnyRSequenceCharSequence.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.xss(arg0, arg1) }
    }

    public override fun doSomething(arg: KMockTypeParameter4): KMockTypeParameter5 =
        _doSomething.invoke(arg) {
            useSpyIf(__spyOn) { __spyOn!!.doSomething(arg) }
        }

    public override
    fun compareTo(other: GenericCommonContract.Generic3<KMockTypeParameter5, KMockTypeParameter4>):
        Int = _compareTo.invoke(other) {
        useSpyIf(__spyOn) { __spyOn!!.compareTo(other) }
    }

    public override fun toString(): String = _toString.invoke() {
        useRelaxerIf(true) { super.toString() }
        useSpyIf(__spyOn) { __spyOn!!.toString() }
    }

    public override fun equals(other: Any?): Boolean = _equals.invoke(other) {
        useRelaxerIf(true) { super.equals(other) }
        useSpyOnEqualsIf(
            spyTarget = __spyOn,
            other = other,
            spyOn = { super.equals(other) },
            mockKlass = CommonGenericMultiMock::class
        )
    }

    public override fun hashCode(): Int = _hashCode.invoke() {
        useRelaxerIf(true) { super.hashCode() }
        useSpyIf(__spyOn) { __spyOn!!.hashCode() }
    }

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
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
