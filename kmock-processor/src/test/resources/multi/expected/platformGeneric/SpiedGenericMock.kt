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
import multi.template.platformGeneric.Generic1
import multi.template.platformGeneric.GenericPlatformContract
import multi.template.platformGeneric.SomeGeneric
import multi.template.platformGeneric.nested.Generic2
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformGenericMultiMock<KMockTypeParameter0 : Any, KMockTypeParameter1,
    KMockTypeParameter2 : Any, KMockTypeParameter3, KMockTypeParameter4, KMockTypeParameter5,
    MultiMock>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: MultiMock? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Generic1<KMockTypeParameter0, KMockTypeParameter1>,
    Generic2<KMockTypeParameter2, KMockTypeParameter3>,
    GenericPlatformContract.Generic3<KMockTypeParameter4, KMockTypeParameter5> where
KMockTypeParameter1 : Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>,
KMockTypeParameter3 : Any, KMockTypeParameter3 : Comparable<KMockTypeParameter3>, MultiMock :
Generic1<KMockTypeParameter0, KMockTypeParameter1>, MultiMock :
Generic2<KMockTypeParameter2, KMockTypeParameter3>, MultiMock :
GenericPlatformContract.Generic3<KMockTypeParameter4, KMockTypeParameter5> {
    private val __spyOn: MultiMock? = spyOn

    public override var template: KMockTypeParameter1
        get() = _template.onGet {
            useSpyIf(__spyOn) { __spyOn!!.template }
        }
        set(`value`) = _template.onSet(value) {
            useSpyIf(__spyOn) { __spyOn!!.template = value }
        }

    public val _template: KMockContract.PropertyProxy<KMockTypeParameter1> =
        ProxyFactory.createPropertyProxy("multi.PlatformGenericMultiMock#_template", collector =
        collector, freeze = freeze)

    public override val lol: KMockTypeParameter2
        get() = _lol.onGet {
            useSpyIf(__spyOn) { __spyOn!!.lol }
        }

    public val _lol: KMockContract.PropertyProxy<KMockTypeParameter2> =
        ProxyFactory.createPropertyProxy("multi.PlatformGenericMultiMock#_lol", collector = collector,
            freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_fooWithVoid", collector =
        collector, freeze = freeze)

    public val _fooWithZTAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_fooWithZTAny", collector =
        collector, freeze = freeze)

    public val _fooWithZTAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out kotlin.Any?>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_fooWithZTAnys", collector =
        collector, freeze = freeze)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_blaWithVoid", collector =
        collector, freeze = freeze)

    public val _blaWithTInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_blaWithTInt", collector =
        collector, freeze = freeze)

    public val _blaWithTInts: KMockContract.SyncFunProxy<Unit, (kotlin.IntArray) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_blaWithTInts", collector =
        collector, freeze = freeze)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_barWithVoid", collector =
        collector, freeze = freeze)

    public val _barWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_barWithTList",
        collector = collector, freeze = freeze)

    public val _barWithTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_barWithTLists", collector =
        collector, freeze = freeze)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_blubbWithVoid", collector =
        collector, freeze = freeze)

    public val _blubbWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_blubbWithTList", collector =
        collector, freeze = freeze)

    public val _blubbWithTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_blubbWithTLists", collector =
        collector, freeze = freeze)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_bussWithVoid", collector =
        collector, freeze = freeze)

    public val _bussWithZTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_bussWithZTList", collector =
        collector, freeze = freeze)

    public val _bussWithZTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_bussWithZTLists", collector =
        collector, freeze = freeze)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_bossWithVoid", collector =
        collector, freeze = freeze)

    public val _bossWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_bossWithTList", collector =
        collector, freeze = freeze)

    public val _bossWithTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_bossWithTLists", collector =
        collector, freeze = freeze)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_buzzWithVoid", collector =
        collector, freeze = freeze)

    public val _buzzWithTList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_buzzWithTList", collector =
        collector, freeze = freeze)

    public val _buzzWithTLists: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_buzzWithTLists", collector =
        collector, freeze = freeze)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<KMockTypeParameter1, () ->
    KMockTypeParameter1> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_ozzWithVoid", collector =
        collector, freeze = freeze)

    public val _ozzWithTKMockTypeParameter3: KMockContract.SyncFunProxy<Unit, (KMockTypeParameter3) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_ozzWithTKMockTypeParameter3",
            collector = collector, freeze = freeze)

    public val _ozzWithTKMockTypeParameter3s: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    KMockTypeParameter3>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_ozzWithTKMockTypeParameter3s",
            collector = collector, freeze = freeze)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_brassWithVoid", collector =
        collector, freeze = freeze)

    public val _brassWithTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_brassWithTComparable",
            collector = collector, freeze = freeze)

    public val _brassWithTComparables: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_brassWithTComparables",
            collector = collector, freeze = freeze)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_blissWithVoid", collector =
        collector, freeze = freeze)

    public val _blissWithZTComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_blissWithZTComparable",
            collector = collector, freeze = freeze)

    public val _blissWithZTComparables: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_blissWithZTComparables",
            collector = collector, freeze = freeze)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_lossWithVoid", collector =
        collector, freeze = freeze)

    public val _lossWithTMap: KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String,
        kotlin.String>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_lossWithTMap", collector =
        collector, freeze = freeze)

    public val _lossWithTMaps: KMockContract.SyncFunProxy<Unit, (kotlin.Array<out
    kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_lossWithTMaps", collector =
        collector, freeze = freeze)

    public val _uzz: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_uzz", collector = collector,
            freeze = freeze)

    public val _lzz: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_lzz", collector = collector,
            freeze = freeze)

    public val _tzz: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_tzz", collector = collector,
            freeze = freeze)

    public val _rzz: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_rzz", collector = collector,
            freeze = freeze)

    public val _izz: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_izz", collector = collector,
            freeze = freeze)

    public val _ossWithTAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_ossWithTAny", collector =
        collector, freeze = freeze)

    public val _ossWithTAnyZRAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_ossWithTAnyZRAny", collector
        = collector, freeze = freeze)

    public val _ossWithZRAnyTAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Array<out
    kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_ossWithZRAnyTAnys", collector
        = collector, freeze = freeze)

    public val _kss: KMockContract.SyncFunProxy<Any, (kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_kss", collector = collector,
            freeze = freeze)

    public val _iss: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_iss", collector = collector,
            freeze = freeze)

    public val _pss:
        KMockContract.SyncFunProxy<multi.template.platformGeneric.SomeGeneric<kotlin.String>, (multi.template.platformGeneric.SomeGeneric<kotlin.String>) ->
        multi.template.platformGeneric.SomeGeneric<kotlin.String>> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_pss", collector = collector,
            freeze = freeze)

    public val _xssWithZTAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_xssWithZTAny", collector =
        collector, freeze = freeze)

    public val _xssWithZTAnyRSequenceCharSequence: KMockContract.SyncFunProxy<Unit, (kotlin.Any?,
        kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_xssWithZTAnyRSequenceCharSequence",
            collector = collector, freeze = freeze)

    public val _doSomething: KMockContract.SyncFunProxy<KMockTypeParameter5, (KMockTypeParameter4) ->
    KMockTypeParameter5> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_doSomething", collector =
        collector, freeze = freeze)

    public val _compareTo:
        KMockContract.SyncFunProxy<Int, (multi.template.platformGeneric.GenericPlatformContract.Generic3<KMockTypeParameter5,
            KMockTypeParameter4>) -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_compareTo", collector =
        collector, freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_toString", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_equals", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("multi.PlatformGenericMultiMock#_hashCode", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.foo() }
    } as T

    public override fun <T> foo(payload: T): Unit = _fooWithZTAny.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.foo(payload) }
    }

    public override fun <T> foo(vararg payload: T): Unit = _fooWithZTAnys.invoke(payload) {
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
        _bussWithZTList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.buss(payload) }
        }

    public override fun <T : List<Array<Int>>?> buss(vararg payload: T): Unit =
        _bussWithZTLists.invoke(payload) {
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
        _blissWithZTComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.bliss(payload) }
        }

    public override fun <T : Comparable<List<Array<T>>>?> bliss(vararg payload: T): Unit =
        _blissWithZTComparables.invoke(payload) {
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
        useSpyIf(__spyOn) { __spyOn!!.uzz<T>() }
    } as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? = _lzz.invoke()
    {
        useSpyIf(__spyOn) { __spyOn!!.lzz<T>() }
    } as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? = _tzz.invoke()
    {
        useSpyIf(__spyOn) { __spyOn!!.tzz<T>() }
    } as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzz.invoke() {
            useSpyIf(__spyOn) { __spyOn!!.rzz<T>() }
        } as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izz.invoke() {
            useSpyIf(__spyOn) { __spyOn!!.izz<T>() }
        } as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithTAny.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.oss(arg0) }
    } as R

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithTAnyZRAny.invoke(arg0, arg1)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.oss(arg0, arg1) }
    }

    public override fun <T : R, R> oss(arg0: R, vararg arg1: T): Unit =
        _ossWithZRAnyTAnys.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.oss(arg0, *arg1) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kss.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.kss<T, R>(arg0) }
    } as R

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _iss.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.iss<R, T>(arg0) }
    } as R

    @Suppress("UNCHECKED_CAST")
    public override fun <R : T, T : X, X : SomeGeneric<String>> pss(arg0: T): R = _pss.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.pss(arg0) }
    } as R

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> xss(arg0: T): R where R : Sequence<Char>, R : CharSequence =
        _xssWithZTAny.invoke(arg0) {
            useSpyIf(__spyOn) { __spyOn!!.xss<R, T>(arg0) }
        } as R

    public override fun <R, T> xss(arg0: T, arg1: R): Unit where R : Sequence<Char>, R : CharSequence
        = _xssWithZTAnyRSequenceCharSequence.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.xss<R, T>(arg0, arg1) }
    }

    public override fun doSomething(arg: KMockTypeParameter4): KMockTypeParameter5 =
        _doSomething.invoke(arg) {
            useSpyIf(__spyOn) { __spyOn!!.doSomething(arg) }
        }

    public override
    fun compareTo(other: GenericPlatformContract.Generic3<KMockTypeParameter5, KMockTypeParameter4>):
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
            mockKlass = PlatformGenericMultiMock::class
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
        _fooWithZTAny.clear()
        _fooWithZTAnys.clear()
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
        _ozzWithTKMockTypeParameter3.clear()
        _ozzWithTKMockTypeParameter3s.clear()
        _brassWithVoid.clear()
        _brassWithTComparable.clear()
        _brassWithTComparables.clear()
        _blissWithVoid.clear()
        _blissWithZTComparable.clear()
        _blissWithZTComparables.clear()
        _lossWithVoid.clear()
        _lossWithTMap.clear()
        _lossWithTMaps.clear()
        _uzz.clear()
        _lzz.clear()
        _tzz.clear()
        _rzz.clear()
        _izz.clear()
        _ossWithTAny.clear()
        _ossWithTAnyZRAny.clear()
        _ossWithZRAnyTAnys.clear()
        _kss.clear()
        _iss.clear()
        _pss.clear()
        _xssWithZTAny.clear()
        _xssWithZTAnyRSequenceCharSequence.clear()
        _doSomething.clear()
        _compareTo.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
