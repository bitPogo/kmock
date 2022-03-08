package generatorTest

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class GenericsMock<K : Any, L>(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: Generics<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Generics<K, L> where L : CharSequence, L : Comparable<L> {
    public override var template: K
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<K> = if (spyOn == null) {
        PropertyProxy("generatorTest.Generics#_template", spyOnGet = null, spyOnSet = null,
            collector = verifier, freeze = freeze, relaxer = null)
    } else {
        PropertyProxy("generatorTest.Generics#_template", spyOnGet = { spyOn.template }, spyOnSet =
        { spyOn.template = it; Unit }, collector = verifier, freeze = freeze, relaxer = null)
    }


    public val _foo: KMockContract.SyncFunProxy<Any?, () -> Any?> =
        SyncFunProxy("generatorTest.Generics#_foo", spyOn = if (spyOn != null) { { foo() } } else {
            null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (Any?) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_fooWithAny", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _fooWithInt: KMockContract.SyncFunProxy<Unit, (kotlin.Int) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_fooWithInt", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _buss: KMockContract.SyncFunProxy<Any?, () -> Any?> =
        SyncFunProxy("generatorTest.Generics#_buss", spyOn = if (spyOn != null) { { throw
        IllegalArgumentException(
            "Multi-Bound generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _bla: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        SyncFunProxy("generatorTest.Generics#_bla", spyOn = if (spyOn != null) { { bla() } } else {
            null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _buzz: KMockContract.SyncFunProxy<Unit, (L) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_buzz", spyOn = if (spyOn != null) { { payload ->
            buzz(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                         (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _bar: KMockContract.SyncFunProxy<Unit, (Any) -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Generics#_bar", spyOn = if (spyOn != null) { { payload ->
            throw IllegalArgumentException(
                "Multi-Bound generics are not supported on function level spies (yet)."
            ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
        { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _fuzz: KMockContract.SyncFunProxy<Int, (kotlin.Int) -> kotlin.Int> =
        SyncFunProxy("generatorTest.Generics#_fuzz", spyOn = if (spyOn != null) { { payload ->
            fuzz(payload) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _ozz: KMockContract.SyncFunProxy<L, () -> L> =
        SyncFunProxy("generatorTest.Generics#_ozz", spyOn = if (spyOn != null) { { ozz() } } else {
            null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _ozzWithCharSequencecollectionsMap: KMockContract.SyncFunProxy<Unit, (Any?) ->
    kotlin.Unit> = SyncFunProxy("generatorTest.Generics#_ozzWithCharSequencecollectionsMap", spyOn
    = if (spyOn != null) { { payload ->
        throw IllegalArgumentException(
            "Multi-Bound generics are not supported on function level spies (yet)."
        ) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun)
    { { relaxVoidFunction() } } else { null }, relaxer = null)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> foo(): T = _foo.invoke() as T

    public override fun <T> foo(payload: T): Unit = _fooWithAny.invoke(payload)

    public override fun <K : Int> foo(payload: K): Unit = _fooWithInt.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> buss(): T where T : CharSequence?, T : Map<String, Comparable<T>>? =
        _buss.invoke() as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Int> bla(): T = _bla.invoke() as T

    public override fun <T : L> buzz(payload: T): Unit = _buzz.invoke(payload)

    public override fun <T> bar(payload: T): Unit where T : CharSequence?, T :
    Map<String, Comparable<List<Array<T>>>> = _bar.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <K : Int> fuzz(payload: K): K = _fuzz.invoke(payload) as K

    @Suppress("UNCHECKED_CAST")
    public override fun <K : L> ozz(): K = _ozz.invoke() as K

    public override fun <T> ozz(payload: T): Unit where T : CharSequence?, T :
    Map<String, Comparable<List<Array<T>>>>? = _ozzWithCharSequencecollectionsMap.invoke(payload)

    public fun _clearMock(): Unit {
        _template.clear()
        _foo.clear()
        _fooWithAny.clear()
        _fooWithInt.clear()
        _buss.clear()
        _bla.clear()
        _buzz.clear()
        _bar.clear()
        _fuzz.clear()
        _ozz.clear()
        _ozzWithCharSequencecollectionsMap.clear()
    }
}
