package mock.template.overloaded

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Function1
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

internal class RefinedMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Refined? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Refined {
    public override val foo: Any
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.RefinedMock#_foo", collector =
        collector, freeze = freeze)

    public override var hashCode: Int
        get() = _hashCode.onGet()
        set(`value`) = _hashCode.onSet(value)

    public val _hashCode: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.RefinedMock#_hashCode", collector =
        collector, freeze = freeze)

    public val _fooWithIntAny: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any>
        = ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithIntAny",
        collector = collector, freeze = freeze)

    public val _fooWithZAnyInt: KMockContract.SyncFunProxy<Any, (kotlin.Any?, kotlin.Int) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithZAnyInt",
            collector = collector, freeze = freeze)

    public val _fooWithAnyString: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.String) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithAnyString",
            collector = collector, freeze = freeze)

    public val _fooWithStringAny: KMockContract.SyncFunProxy<Any, (kotlin.String, kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithStringAny",
            collector = collector, freeze = freeze)

    public val _fooWithZStringAbc: KMockContract.SyncFunProxy<Any, (kotlin.String?,
        mock.template.overloaded.Abc) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithZStringAbc",
            collector = collector, freeze = freeze)

    public val _fooWithFunction1_Any_Unit:
        KMockContract.SyncFunProxy<Any, (kotlin.Function1<kotlin.Any, kotlin.Unit>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithFunction1_Any_Unit",
            collector = collector, freeze = freeze)

    public val _fooWithTZAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTZAny",
            collector = collector, freeze = freeze)

    public val _fooWithTRefined:
        KMockContract.SyncFunProxy<Unit, (mock.template.overloaded.Refined) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTRefined",
            collector = collector, freeze = freeze)

    public val _fooWithTLPG: KMockContract.SyncFunProxy<Unit, (mock.template.overloaded.LPG) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTLPG", collector
        = collector, freeze = freeze)

    public val _fooWithArray_Any: KMockContract.SyncFunProxy<Any, (kotlin.Array<out kotlin.Any>) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithArray_Any",
            collector = collector, freeze = freeze)

    public val _fooWithComparable_Array_Map_String_TZAny:
        KMockContract.SyncFunProxy<Any, (kotlin.Comparable<kotlin.Array<kotlin.collections.Map<kotlin.String,
            kotlin.Any?>>>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithComparable_Array_Map_String_TZAny",
            collector = collector, freeze = freeze)

    public val _fooWithComparable_Array_Map_TZAny_Any:
        KMockContract.SyncFunProxy<Any, (kotlin.Comparable<kotlin.Array<kotlin.collections.Map<kotlin.Any?,
            kotlin.Any>>>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithComparable_Array_Map_TZAny_Any",
            collector = collector, freeze = freeze)

    public val _fooWithComparable_Array_Map_String_Any:
        KMockContract.SyncFunProxy<Any, (kotlin.Comparable<kotlin.Array<kotlin.collections.Map<kotlin.String,
            kotlin.Any>>>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithComparable_Array_Map_String_Any",
            collector = collector, freeze = freeze)

    public val _fooWithTZCharSequenceComparable_TAbc: KMockContract.SyncFunProxy<Any, (kotlin.Any?,
        mock.template.overloaded.Abc) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTZCharSequenceComparable_TAbc",
            collector = collector, freeze = freeze)

    public val _fooWithTCharSequenceZComparable_TAbc: KMockContract.SyncFunProxy<Any, (kotlin.Any,
        mock.template.overloaded.Abc) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTCharSequenceZComparable_TAbc",
            collector = collector, freeze = freeze)

    public val _fooWithTZCharSequenceZComparable_TAbc: KMockContract.SyncFunProxy<Any, (kotlin.Any?,
        mock.template.overloaded.Abc) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTZCharSequenceZComparable_TAbc",
            collector = collector, freeze = freeze)

    public val _fooWithTCharSequenceComparable_TAbc: KMockContract.SyncFunProxy<Any, (kotlin.Any?,
        mock.template.overloaded.Abc) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTCharSequenceComparable_TAbc",
            collector = collector, freeze = freeze)

    public val _fooWithAlias: KMockContract.SyncFunProxy<Any, (mock.template.overloaded.alias) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithAlias",
            collector = collector, freeze = freeze)

    public val _fooWithRList_T:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithRList_T",
            collector = collector, freeze = freeze)

    public val _fooWithRList_ZT:
        KMockContract.SyncFunProxy<Unit, (kotlin.collections.List<kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithRList_ZT",
            collector = collector, freeze = freeze)

    public val _ooWithIntArray_Any: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Array<out
    kotlin.Any>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_ooWithIntArray_Any",
            collector = collector, freeze = freeze)

    public val _ooWithZAnyArray_Int: KMockContract.SyncFunProxy<Any, (kotlin.Any?, kotlin.IntArray) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_ooWithZAnyArray_Int",
            collector = collector, freeze = freeze)

    public val _ooWithAnyArray_TZAny: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.Array<out
    kotlin.Any?>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_ooWithAnyArray_TZAny",
            collector = collector, freeze = freeze)

    public val _ooWithAnyArray_RRR_TZAny: KMockContract.SyncFunProxy<Any, (kotlin.Any,
        kotlin.Array<out mock.template.overloaded.RRR<kotlin.Any?>>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_ooWithAnyArray_RRR_TZAny",
            collector = collector, freeze = freeze)

    public override fun foo(fuzz: Int, ozz: Any): Any = _fooWithIntAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any?, ozz: Int): Any = _fooWithZAnyInt.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: String): Any = _fooWithAnyString.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Any): Any = _fooWithStringAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: String?, ozz: Abc): Any = _fooWithZStringAbc.invoke(fuzz, ozz)

    public override fun foo(fuzz: Function1<Any, Unit>): Any = _fooWithFunction1_Any_Unit.invoke(fuzz)

    public override fun <T> foo(fuzz: T): Unit = _fooWithTZAny.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Refined> foo(fuzz: T): Unit = _fooWithTRefined.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : LPG> foo(fuzz: T): Unit = _fooWithTLPG.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun foo(vararg fuzz: Any): Any = _fooWithArray_Any.invoke(fuzz)

    public override fun <T> foo(fuzz: Comparable<Array<Map<String, T>>>): Any =
        _fooWithComparable_Array_Map_String_TZAny.invoke(fuzz)

    public override fun <T> foo(fuzz: Comparable<Array<Map<T, Any>>>): Any =
        _fooWithComparable_Array_Map_TZAny_Any.invoke(fuzz)

    public override fun foo(fuzz: Comparable<Array<Map<String, Any>>>): Any =
        _fooWithComparable_Array_Map_String_Any.invoke(fuzz)

    public override fun <T> foo(fuzz: T?, ozz: Abc): Any where T : CharSequence?, T : Comparable<T> =
        _fooWithTZCharSequenceComparable_TAbc.invoke(fuzz, ozz)

    public override fun <T> foo(fuzz: T, ozz: Abc): Any where T : CharSequence, T : Comparable<T>? =
        _fooWithTCharSequenceZComparable_TAbc.invoke(fuzz, ozz)

    public override fun <T> foo(fuzz: T, ozz: Abc): Any where T : CharSequence?, T : Comparable<T>? =
        _fooWithTZCharSequenceZComparable_TAbc.invoke(fuzz, ozz)

    public override fun <T> foo(fuzz: T?, ozz: Abc): Any where T : CharSequence, T : Comparable<T> =
        _fooWithTCharSequenceComparable_TAbc.invoke(fuzz, ozz)

    public override fun foo(fuzz: alias): Any = _fooWithAlias.invoke(fuzz)

    public override fun <T : Comparable<Array<R>>, R : List<T>> foo(fuzz: R): Unit =
        _fooWithRList_T.invoke(fuzz) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<Array<R>>, R : List<T?>> foo(fuzz: R): Unit =
        _fooWithRList_ZT.invoke(fuzz) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun oo(fuzz: Int, vararg ozz: Any): Any = _ooWithIntArray_Any.invoke(fuzz, ozz)

    public override fun oo(fuzz: Any?, vararg ozz: Int): Any = _ooWithZAnyArray_Int.invoke(fuzz, ozz)

    public override fun <T> oo(fuzz: Any, vararg ozz: T): Any = _ooWithAnyArray_TZAny.invoke(fuzz,
        ozz)

    public override fun <T> oo(fuzz: Any, vararg ozz: RRR<T>): Any =
        _ooWithAnyArray_RRR_TZAny.invoke(fuzz, ozz)

    public fun _clearMock(): Unit {
        _foo.clear()
        _hashCode.clear()
        _fooWithIntAny.clear()
        _fooWithZAnyInt.clear()
        _fooWithAnyString.clear()
        _fooWithStringAny.clear()
        _fooWithZStringAbc.clear()
        _fooWithFunction1_Any_Unit.clear()
        _fooWithTZAny.clear()
        _fooWithTRefined.clear()
        _fooWithTLPG.clear()
        _fooWithArray_Any.clear()
        _fooWithComparable_Array_Map_String_TZAny.clear()
        _fooWithComparable_Array_Map_TZAny_Any.clear()
        _fooWithComparable_Array_Map_String_Any.clear()
        _fooWithTZCharSequenceComparable_TAbc.clear()
        _fooWithTCharSequenceZComparable_TAbc.clear()
        _fooWithTZCharSequenceZComparable_TAbc.clear()
        _fooWithTCharSequenceComparable_TAbc.clear()
        _fooWithAlias.clear()
        _fooWithRList_T.clear()
        _fooWithRList_ZT.clear()
        _ooWithIntArray_Any.clear()
        _ooWithZAnyArray_Int.clear()
        _ooWithAnyArray_TZAny.clear()
        _ooWithAnyArray_RRR_TZAny.clear()
    }
}
