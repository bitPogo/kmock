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

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_fooWithAny", collector =
        verifier, freeze = freeze)

    public val _fooWithAnys: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) -> kotlin.Unit>
        = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_fooWithAnys", collector =
    verifier, freeze = freeze)

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blaWithVoid", collector =
        verifier, freeze = freeze)

    public val _blaWithInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blaWithInt", collector =
        verifier, freeze = freeze)

    public val _blaWithInts: KMockContract.SyncFunProxy<Unit, (kotlin.IntArray) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blaWithInts", collector =
        verifier, freeze = freeze)

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_barWithVoid", collector =
        verifier, freeze = freeze)

    public val _barWithList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_barWithList",
        collector = verifier, freeze = freeze)

    public val _barWithLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_barWithLists", collector =
        verifier, freeze = freeze)

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blubbWithVoid", collector =
        verifier, freeze = freeze)

    public val _blubbWithList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blubbWithList",
        collector = verifier, freeze = freeze)

    public val _blubbWithLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.String?>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blubbWithLists", collector =
        verifier, freeze = freeze)

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_bussWithVoid", collector =
        verifier, freeze = freeze)

    public val _bussWithList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_bussWithList",
        collector = verifier, freeze = freeze)

    public val _bussWithLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_bussWithLists", collector =
        verifier, freeze = freeze)

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_bossWithVoid", collector =
        verifier, freeze = freeze)

    public val _bossWithList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_bossWithList",
        collector = verifier, freeze = freeze)

    public val _bossWithLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_bossWithLists", collector =
        verifier, freeze = freeze)

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_buzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _buzzWithList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_buzzWithList",
        collector = verifier, freeze = freeze)

    public val _buzzWithLists: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.List<kotlin.Array<kotlin.Int>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_buzzWithLists", collector =
        verifier, freeze = freeze)

    public val _ozzWithVoid: KMockContract.SyncFunProxy<KMockTypeParameter1, () ->
    KMockTypeParameter1> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_ozzWithVoid", collector =
        verifier, freeze = freeze)

    public val _ozzWithKMockTypeParameter3: KMockContract.SyncFunProxy<Unit, (KMockTypeParameter3) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_ozzWithKMockTypeParameter3",
            collector = verifier, freeze = freeze)

    public val _ozzWithKMockTypeParameter3s: KMockContract.SyncFunProxy<Unit, (Array<out
    KMockTypeParameter3>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_ozzWithKMockTypeParameter3s",
            collector = verifier, freeze = freeze)

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_brassWithVoid", collector =
        verifier, freeze = freeze)

    public val _brassWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_brassWithComparable", collector
        = verifier, freeze = freeze)

    public val _brassWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_brassWithComparables",
            collector = verifier, freeze = freeze)

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blissWithVoid", collector =
        verifier, freeze = freeze)

    public val _blissWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blissWithComparable", collector
        = verifier, freeze = freeze)

    public val _blissWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<kotlin.collections.List<kotlin.Array<Any?>>>?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_blissWithComparables",
            collector = verifier, freeze = freeze)

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_lossWithVoid", collector =
        verifier, freeze = freeze)

    public val _lossWithMap: KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String,
        kotlin.String>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_lossWithMap", collector =
        verifier, freeze = freeze)

    public val _lossWithMaps: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_lossWithMaps", collector =
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

    public val _ossWithAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_ossWithAny", collector =
        verifier, freeze = freeze)

    public val _ossWithAnyAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> = ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_ossWithAnyAny",
        collector = verifier, freeze = freeze)

    public val _ossWithAnyAnys: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, Array<out
    kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_ossWithAnyAnys", collector =
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

    public val _xssWithAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_xssWithAny", collector =
        verifier, freeze = freeze)

    public val _xssWithAnySequenceCharSequence: KMockContract.SyncFunProxy<Unit, (kotlin.Any?,
        kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("multi.CommonGenericMultiMock#_xssWithAnySequenceCharSequence",
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

    public override fun <T> foo(payload: T): Unit = _fooWithAny.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.foo(payload) }
    }

    public override fun <T> foo(vararg payload: T): Unit = _fooWithAnys.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.foo(*payload) }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Int> bla(): T = _blaWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.bla() }
    } as T

    public override fun <T : Int> bla(payload: T): Unit = _blaWithInt.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.bla(payload) }
    }

    public override fun <T : Int> bla(vararg payload: T): Unit = _blaWithInts.invoke(payload) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.bla(*payload) }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String>>> bar(): T = _barWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.bar() }
    } as T

    public override fun <T : List<Array<String>>> bar(payload: T): Unit = _barWithList.invoke(payload)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.bar(payload) }
    }

    public override fun <T : List<Array<String>>> bar(vararg payload: T): Unit =
        _barWithLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.bar(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String?>>> blubb(): T = _blubbWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.blubb() }
    } as T

    public override fun <T : List<Array<String?>>> blubb(payload: T): Unit =
        _blubbWithList.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.blubb(payload) }
        }

    public override fun <T : List<Array<String?>>> blubb(vararg payload: T): Unit =
        _blubbWithLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.blubb(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>?> buss(): T = _bussWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.buss() }
    } as T

    public override fun <T : List<Array<Int>>?> buss(payload: T): Unit = _bussWithList.invoke(payload)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.buss(payload) }
    }

    public override fun <T : List<Array<Int>>?> buss(vararg payload: T): Unit =
        _bussWithLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.buss(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>?>> boss(): T = _bossWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.boss() }
    } as T

    public override fun <T : List<Array<Int>?>> boss(payload: T): Unit = _bossWithList.invoke(payload)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.boss(payload) }
    }

    public override fun <T : List<Array<Int>?>> boss(vararg payload: T): Unit =
        _bossWithLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.boss(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>> buzz(): T? = _buzzWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.buzz() }
    } as T?

    public override fun <T : List<Array<Int>>> buzz(payload: T?): Unit = _buzzWithList.invoke(payload)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.buzz(payload) }
    }

    public override fun <T : List<Array<Int>>> buzz(vararg payload: T?): Unit =
        _buzzWithLists.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.buzz(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : KMockTypeParameter1> ozz(): T = _ozzWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.ozz() }
    } as T

    public override fun <T : KMockTypeParameter3> ozz(payload: T): Unit =
        _ozzWithKMockTypeParameter3.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.ozz(payload) }
        }

    public override fun <T : KMockTypeParameter3> ozz(vararg payload: T): Unit =
        _ozzWithKMockTypeParameter3s.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.ozz(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>> brass(): T = _brassWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.brass() }
    } as T

    public override fun <T : Comparable<List<Array<T>>>> brass(payload: T): Unit =
        _brassWithComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.brass(payload) }
        }

    public override fun <T : Comparable<List<Array<T>>>> brass(vararg payload: T): Unit =
        _brassWithComparables.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.brass(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>?> bliss(): T = _blissWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.bliss() }
    } as T

    public override fun <T : Comparable<List<Array<T>>>?> bliss(payload: T): Unit =
        _blissWithComparable.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.bliss(payload) }
        }

    public override fun <T : Comparable<List<Array<T>>>?> bliss(vararg payload: T): Unit =
        _blissWithComparables.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.bliss(*payload) }
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Map<String, String>> loss(): T = _lossWithVoid.invoke() {
        useSpyIf(__spyOn) { __spyOn!!.loss() }
    } as T

    public override fun <T : Map<String, String>> loss(payload: T): Unit =
        _lossWithMap.invoke(payload) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
            useSpyIf(__spyOn) { __spyOn!!.loss(payload) }
        }

    public override fun <T : Map<String, String>> loss(vararg payload: T): Unit =
        _lossWithMaps.invoke(payload) {
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
    public override fun <T : R, R> oss(arg0: T): R = _ossWithAny.invoke(arg0) {
        useSpyIf(__spyOn) { __spyOn!!.oss(arg0) }
    } as R

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithAnyAny.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        useSpyIf(__spyOn) { __spyOn!!.oss(arg0, arg1) }
    }

    public override fun <T : R, R> oss(arg0: R, vararg arg1: T): Unit = _ossWithAnyAnys.invoke(arg0,
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
        _xssWithAny.invoke(arg0) {
            useSpyIf(__spyOn) { __spyOn!!.xss(arg0) }
        } as R

    public override fun <R, T> xss(arg0: T, arg1: R): Unit where R : Sequence<Char>, R : CharSequence
        = _xssWithAnySequenceCharSequence.invoke(arg0, arg1) {
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
        _fooWithAny.clear()
        _fooWithAnys.clear()
        _blaWithVoid.clear()
        _blaWithInt.clear()
        _blaWithInts.clear()
        _barWithVoid.clear()
        _barWithList.clear()
        _barWithLists.clear()
        _blubbWithVoid.clear()
        _blubbWithList.clear()
        _blubbWithLists.clear()
        _bussWithVoid.clear()
        _bussWithList.clear()
        _bussWithLists.clear()
        _bossWithVoid.clear()
        _bossWithList.clear()
        _bossWithLists.clear()
        _buzzWithVoid.clear()
        _buzzWithList.clear()
        _buzzWithLists.clear()
        _ozzWithVoid.clear()
        _ozzWithKMockTypeParameter3.clear()
        _ozzWithKMockTypeParameter3s.clear()
        _brassWithVoid.clear()
        _brassWithComparable.clear()
        _brassWithComparables.clear()
        _blissWithVoid.clear()
        _blissWithComparable.clear()
        _blissWithComparables.clear()
        _lossWithVoid.clear()
        _lossWithMap.clear()
        _lossWithMaps.clear()
        _uzz.clear()
        _lzz.clear()
        _tzz.clear()
        _rzz.clear()
        _izz.clear()
        _ossWithAny.clear()
        _ossWithAnyAny.clear()
        _ossWithAnyAnys.clear()
        _kss.clear()
        _iss.clear()
        _pss.clear()
        _xssWithAny.clear()
        _xssWithAnySequenceCharSequence.clear()
        _doSomething.clear()
        _compareTo.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}