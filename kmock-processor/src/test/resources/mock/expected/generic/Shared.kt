package mock.template.generic

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SharedMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Shared<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Shared<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.generic.SharedMock#_template", collector =
        verifier, freeze = freeze)

    public val _fooWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_fooWithVoid", collector =
        verifier, freeze = freeze)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_fooWithAny", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blaWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blaWithVoid", collector =
        verifier, freeze = freeze)

    public val _blaWithInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blaWithInt", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _barWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_barWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _barWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_barWithVoid", collector =
        verifier, freeze = freeze)

    public val _blubbWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.String?>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blubbWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blubbWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.String?>>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.String?>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blubbWithVoid", collector =
        verifier, freeze = freeze)

    public val _bussWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_bussWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _bussWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_bussWithVoid", collector =
        verifier, freeze = freeze)

    public val _bossWithCollectionsList:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>?>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_bossWithCollectionsList",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _bossWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>?>, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>?>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_bossWithVoid", collector =
        verifier, freeze = freeze)

    public val _buzzWithT:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Array<kotlin.Int>>?) ->
        kotlin.Unit> = ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_buzzWithT",
        collector = verifier, freeze = freeze) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public val _buzzWithVoid:
        KMockContract.SyncFunProxy<kotlin.collections.List<kotlin.Array<kotlin.Int>>?, () ->
        kotlin.collections.List<kotlin.Array<kotlin.Int>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_buzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _ozzWithL: KMockContract.SyncFunProxy<Unit, (L) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_ozzWithL", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _ozzWithVoid: KMockContract.SyncFunProxy<L, () -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_ozzWithVoid", collector =
        verifier, freeze = freeze)

    public val _brassWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_brassWithComparable",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _brassWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any>>>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_brassWithVoid", collector =
        verifier, freeze = freeze)

    public val _blissWithComparable:
        KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blissWithComparable",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _blissWithVoid:
        KMockContract.SyncFunProxy<kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?, () ->
        kotlin.Comparable<kotlin.collections.List<kotlin.Array<kotlin.Any?>>>?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_blissWithVoid", collector =
        verifier, freeze = freeze)

    public val _lossWithCollectionsMap:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.Map<kotlin.String, kotlin.String>) ->
        kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_lossWithCollectionsMap",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _lossWithVoid: KMockContract.SyncFunProxy<kotlin.collections.Map<kotlin.String,
        kotlin.String>, () -> kotlin.collections.Map<kotlin.String, kotlin.String>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_lossWithVoid", collector =
        verifier, freeze = freeze)

    public val _uzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_uzzWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _uzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_uzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _lzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_lzzWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _lzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_lzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _tzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_tzzWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _tzzWithVoid: KMockContract.SyncFunProxy<Any?, () -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_tzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _rzzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_rzzWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _rzzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_rzzWithVoid", collector =
        verifier, freeze = freeze)

    public val _izzWithMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_izzWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _izzWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_izzWithVoid", collector =
        verifier, freeze = freeze)

    public val _ossWithAnyAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any?) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_ossWithAnyAny", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _ossWithAny: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_ossWithAny", collector =
        verifier, freeze = freeze)

    public val _kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _kssWithMockTemplateGenericSomeGeneric: KMockContract.SyncFunProxy<Any, (kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_kssWithMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze)

    public val _issWithAnyMockTemplateGenericSomeGeneric:
        KMockContract.SyncFunProxy<Unit, (kotlin.Any?, kotlin.Any) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_issWithAnyMockTemplateGenericSomeGeneric",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _issWithAny: KMockContract.SyncFunProxy<Any, (kotlin.Any?) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.SharedMock#_issWithAny", collector =
        verifier, freeze = freeze)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _fooWithVoid.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithAny.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Int> bla(): T = _blaWithVoid.invoke() as T

    public override fun <T : Int> bla(payload: T): Unit = _blaWithInt.invoke(payload)

    public override fun <T : List<Array<String>>> bar(payload: T): Unit =
        _barWithCollectionsList.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String>>> bar(): T = _barWithVoid.invoke() as T

    public override fun <T : List<Array<String?>>> blubb(payload: T): Unit =
        _blubbWithCollectionsList.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<String?>>> blubb(): T = _blubbWithVoid.invoke() as T

    public override fun <T : List<Array<Int>>?> buss(payload: T): Unit =
        _bussWithCollectionsList.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>?> buss(): T = _bussWithVoid.invoke() as T

    public override fun <T : List<Array<Int>?>> boss(payload: T): Unit =
        _bossWithCollectionsList.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>?>> boss(): T = _bossWithVoid.invoke() as T

    public override fun <T : List<Array<Int>>> buzz(payload: T?): Unit = _buzzWithT.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : List<Array<Int>>> buzz(): T? = _buzzWithVoid.invoke() as T?

    public override fun <T : L> ozz(payload: T): Unit = _ozzWithL.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : L> ozz(): T = _ozzWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>> brass(payload: T): Unit =
        _brassWithComparable.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>> brass(): T = _brassWithVoid.invoke() as T

    public override fun <T : Comparable<List<Array<T>>>?> bliss(payload: T): Unit =
        _blissWithComparable.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<Array<T>>>?> bliss(): T = _blissWithVoid.invoke() as T

    public override fun <T : Map<String, String>> loss(payload: T): Unit =
        _lossWithCollectionsMap.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Map<String, String>> loss(): T = _lossWithVoid.invoke() as T

    public override fun <T> uzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String> =
        _uzzWithMockTemplateGenericSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String> =
        _uzzWithVoid.invoke() as T

    public override fun <T> lzz(payload: T): Unit where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithMockTemplateGenericSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>? =
        _lzzWithVoid.invoke() as T

    public override fun <T> tzz(payload: T): Unit where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithMockTemplateGenericSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>? =
        _tzzWithVoid.invoke() as T

    public override fun <T> rzz(payload: T): Unit where T : SomeGeneric<String>, T :
    Map<String, String> = _rzzWithMockTemplateGenericSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String> =
        _rzzWithVoid.invoke() as T

    public override fun <T> izz(payload: T): Unit where T : SomeGeneric<String>, T :
    Comparable<List<Array<T>>> = _izzWithMockTemplateGenericSomeGeneric.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>> =
        _izzWithVoid.invoke() as T

    public override fun <T : R, R> oss(arg0: T, arg1: R): Unit = _ossWithAnyAny.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> oss(arg0: T): R = _ossWithAny.invoke(arg0) as R

    public override fun <T : R, R> kss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> =
        _kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<R>>> = _kssWithMockTemplateGenericSomeGeneric.invoke(arg0) as R

    public override fun <R, T> iss(arg0: T, arg1: R): Unit where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithAnyMockTemplateGenericSomeGeneric.invoke(arg0, arg1)

    @Suppress("UNCHECKED_CAST")
    public override fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R :
    Comparable<List<Array<T>>> = _issWithAny.invoke(arg0) as R

    public fun _clearMock(): Unit {
        _template.clear()
        _fooWithVoid.clear()
        _fooWithAny.clear()
        _blaWithVoid.clear()
        _blaWithInt.clear()
        _barWithCollectionsList.clear()
        _barWithVoid.clear()
        _blubbWithCollectionsList.clear()
        _blubbWithVoid.clear()
        _bussWithCollectionsList.clear()
        _bussWithVoid.clear()
        _bossWithCollectionsList.clear()
        _bossWithVoid.clear()
        _buzzWithT.clear()
        _buzzWithVoid.clear()
        _ozzWithL.clear()
        _ozzWithVoid.clear()
        _brassWithComparable.clear()
        _brassWithVoid.clear()
        _blissWithComparable.clear()
        _blissWithVoid.clear()
        _lossWithCollectionsMap.clear()
        _lossWithVoid.clear()
        _uzzWithMockTemplateGenericSomeGeneric.clear()
        _uzzWithVoid.clear()
        _lzzWithMockTemplateGenericSomeGeneric.clear()
        _lzzWithVoid.clear()
        _tzzWithMockTemplateGenericSomeGeneric.clear()
        _tzzWithVoid.clear()
        _rzzWithMockTemplateGenericSomeGeneric.clear()
        _rzzWithVoid.clear()
        _izzWithMockTemplateGenericSomeGeneric.clear()
        _izzWithVoid.clear()
        _ossWithAnyAny.clear()
        _ossWithAny.clear()
        _kssWithMockTemplateGenericSomeGenericMockTemplateGenericSomeGeneric.clear()
        _kssWithMockTemplateGenericSomeGeneric.clear()
        _issWithAnyMockTemplateGenericSomeGeneric.clear()
        _issWithAny.clear()
    }
}
