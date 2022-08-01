package mock.template.overloaded

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Function1
import kotlin.Int
import kotlin.IntArray
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class RefinedMock<Q : List<Int>>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Refined<Q>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Refined<Q> {
    public override val foo: Any
        get() = _foo.executeOnGet()

    public val _foo: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.RefinedMock#_foo", collector =
        collector, freeze = freeze)

    public override var hashCode: Int
        get() = _hashCode.executeOnGet()
        set(`value`) = _hashCode.executeOnSet(value)

    public val _hashCode: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.RefinedMock#_hashCode", collector =
        collector, freeze = freeze)

    public val _fooWithIntAny: KMockContract.SyncFunProxy<Any, (Int, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithIntAny",
            collector = collector, freeze = freeze)

    public val _fooWithZAnyInt: KMockContract.SyncFunProxy<Any, (Any?, Int) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithZAnyInt",
            collector = collector, freeze = freeze)

    public val _fooWithAnyString: KMockContract.SyncFunProxy<Any, (Any, String) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithAnyString",
            collector = collector, freeze = freeze)

    public val _fooWithStringAny: KMockContract.SyncFunProxy<Any, (String, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithStringAny",
            collector = collector, freeze = freeze)

    public val _fooWithZStringAbc: KMockContract.SyncFunProxy<Any, (String?, Abc) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithZStringAbc",
            collector = collector, freeze = freeze)

    public val _fooWithFunction1_Any_Unit:
        KMockContract.SyncFunProxy<Any, (Function1<Any, Unit>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithFunction1_Any_Unit",
            collector = collector, freeze = freeze)

    public val _fooWithTZAny: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTZAny",
            collector = collector, freeze = freeze)

    public val _fooWithTRefined_ZAny: KMockContract.SyncFunProxy<Unit, (Refined<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTRefined_ZAny",
            collector = collector, freeze = freeze)

    public val _fooWithTLPG: KMockContract.SyncFunProxy<Unit, (LPG) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTLPG", collector
        = collector, freeze = freeze)

    public val _fooWithArray_Any: KMockContract.SyncFunProxy<Any, (Array<out Any>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithArray_Any",
            collector = collector, freeze = freeze)

    public val _fooWithComparable_Array_Map_String_TZAny:
        KMockContract.SyncFunProxy<Any, (Comparable<Array<Map<String, Any?>>>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithComparable_Array_Map_String_TZAny",
            collector = collector, freeze = freeze)

    public val _fooWithComparable_Array_Map_TZAny_Any:
        KMockContract.SyncFunProxy<Any, (Comparable<Array<Map<Any?, Any>>>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithComparable_Array_Map_TZAny_Any",
            collector = collector, freeze = freeze)

    public val _fooWithComparable_Array_Map_String_Any:
        KMockContract.SyncFunProxy<Any, (Comparable<Array<Map<String, Any>>>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithComparable_Array_Map_String_Any",
            collector = collector, freeze = freeze)

    public val _fooWithArray_Any: KMockContract.SyncFunProxy<Any, (Array<in Any>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithArray_Any",
            collector = collector, freeze = freeze)

    public val _fooWithZTZCharSequenceZTComparable_AnyAbc: KMockContract.SyncFunProxy<Any, (Any?,
        Abc) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithZTZCharSequenceZTComparable_AnyAbc",
            collector = collector, freeze = freeze)

    public val _fooWithTCharSequenceTZComparable_AnyAbc: KMockContract.SyncFunProxy<Any, (Any,
        Abc) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTCharSequenceTZComparable_AnyAbc",
            collector = collector, freeze = freeze)

    public val _fooWithTZCharSequenceTZComparable_ZAnyAbc: KMockContract.SyncFunProxy<Any, (Any?,
        Abc) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTZCharSequenceTZComparable_ZAnyAbc",
            collector = collector, freeze = freeze)

    public val _fooWithZTCharSequenceZTComparable_AnyAbc: KMockContract.SyncFunProxy<Any, (Any?,
        Abc) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithZTCharSequenceZTComparable_AnyAbc",
            collector = collector, freeze = freeze)

    public val _fooWithAlias: KMockContract.SyncFunProxy<Any, (alias) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithAlias",
            collector = collector, freeze = freeze)

    public val _fooWithRList_Any: KMockContract.SyncFunProxy<Unit, (List<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithRList_Any",
            collector = collector, freeze = freeze)

    public val _fooWithRList_ZAny: KMockContract.SyncFunProxy<Unit, (List<Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithRList_ZAny",
            collector = collector, freeze = freeze)

    public val _fooWithTComparable_ZAny: KMockContract.SyncFunProxy<Unit, (Comparable<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTComparable_ZAny",
            collector = collector, freeze = freeze)

    public val _fooWithQ: KMockContract.SyncFunProxy<Unit, (Q) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithQ", collector =
        collector, freeze = freeze)

    public val _fooWithTComparable_Q: KMockContract.SyncFunProxy<Unit, (Comparable<Q>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTComparable_Q",
            collector = collector, freeze = freeze)

    public val _fooWithZQ: KMockContract.SyncFunProxy<Unit, (Q?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithZQ", collector =
        collector, freeze = freeze)

    public val _fooWithTComparable_ZQ: KMockContract.SyncFunProxy<Unit, (Comparable<Q?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTComparable_ZQ",
            collector = collector, freeze = freeze)

    public val _fooWithTQString: KMockContract.SyncFunProxy<Unit, (Q, String) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTQString",
            collector = collector, freeze = freeze)

    public val _fooWithTCharSequenceTZComparable_AnyQCharSequenceQZComparable_Any:
        KMockContract.SyncFunProxy<Unit, (Any, Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_fooWithTCharSequenceTZComparable_AnyQCharSequenceQZComparable_Any",
            collector = collector, freeze = freeze)

    public val _ooWithIntArray_Any: KMockContract.SyncFunProxy<Any, (Int, Array<out Any>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_ooWithIntArray_Any",
            collector = collector, freeze = freeze)

    public val _ooWithZAnyArray_Int: KMockContract.SyncFunProxy<Any, (Any?, IntArray) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_ooWithZAnyArray_Int",
            collector = collector, freeze = freeze)

    public val _ooWithAnyArray_TAny: KMockContract.SyncFunProxy<Any, (Any, Array<*>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_ooWithAnyArray_TAny",
            collector = collector, freeze = freeze)

    public val _ooWithAnyArray_RRR_TAny: KMockContract.SyncFunProxy<Any, (Any,
        Array<out RRR<Any?>>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.RefinedMock#_ooWithAnyArray_RRR_TAny",
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

    public override fun <T : Refined<*>> foo(fuzz: T): Unit = _fooWithTRefined_ZAny.invoke(fuzz) {
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

    public override fun foo(fuzz: Array<in Any>): Any = _fooWithArray_Any.invoke(fuzz)

    public override fun <T> foo(fuzz: T?, ozz: Abc): Any where T : CharSequence?, T : Comparable<T> =
        _fooWithZTZCharSequenceZTComparable_AnyAbc.invoke(fuzz, ozz)

    public override fun <T> foo(fuzz: T, ozz: Abc): Any where T : CharSequence, T : Comparable<T>? =
        _fooWithTCharSequenceTZComparable_AnyAbc.invoke(fuzz, ozz)

    public override fun <T> foo(fuzz: T, ozz: Abc): Any where T : CharSequence?, T : Comparable<T>? =
        _fooWithTZCharSequenceTZComparable_ZAnyAbc.invoke(fuzz, ozz)

    public override fun <T> foo(fuzz: T?, ozz: Abc): Any where T : CharSequence, T : Comparable<T> =
        _fooWithZTCharSequenceZTComparable_AnyAbc.invoke(fuzz, ozz)

    public override fun foo(fuzz: alias): Any = _fooWithAlias.invoke(fuzz)

    public override fun <T : Comparable<Array<R>>, R : List<T>> foo(fuzz: R): Unit =
        _fooWithRList_Any.invoke(fuzz) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<Array<R>>, R : List<T?>> foo(fuzz: R): Unit =
        _fooWithRList_ZAny.invoke(fuzz) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Comparable<*>> foo(fuzz: T): Unit = _fooWithTComparable_ZAny.invoke(fuzz)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun foo(fuzz: Q): Unit = _fooWithQ.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Comparable<Q>> foo(fuzz: T): Unit = _fooWithTComparable_Q.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun foo(fuzz: Q?): Unit = _fooWithZQ.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Comparable<Q?>> foo(fuzz: T): Unit = _fooWithTComparable_ZQ.invoke(fuzz)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Q> foo(arg0: T, arg1: String): Unit = _fooWithTQString.invoke(arg0, arg1)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Q, Q> foo(arg0: T, arg1: Q): Unit where Q : CharSequence, Q :
    Comparable<Q>? =
        _fooWithTCharSequenceTZComparable_AnyQCharSequenceQZComparable_Any.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun oo(fuzz: Int, vararg ozz: Any): Any = _ooWithIntArray_Any.invoke(fuzz, ozz)

    public override fun oo(fuzz: Any?, vararg ozz: Int): Any = _ooWithZAnyArray_Int.invoke(fuzz, ozz)

    public override fun <T> oo(fuzz: Any, vararg ozz: T): Any = _ooWithAnyArray_TAny.invoke(fuzz, ozz)

    public override fun <T> oo(fuzz: Any, vararg ozz: RRR<T>): Any =
        _ooWithAnyArray_RRR_TAny.invoke(fuzz, ozz)

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
        _fooWithTRefined_ZAny.clear()
        _fooWithTLPG.clear()
        _fooWithArray_Any.clear()
        _fooWithComparable_Array_Map_String_TZAny.clear()
        _fooWithComparable_Array_Map_TZAny_Any.clear()
        _fooWithComparable_Array_Map_String_Any.clear()
        _fooWithArray_Any.clear()
        _fooWithZTZCharSequenceZTComparable_AnyAbc.clear()
        _fooWithTCharSequenceTZComparable_AnyAbc.clear()
        _fooWithTZCharSequenceTZComparable_ZAnyAbc.clear()
        _fooWithZTCharSequenceZTComparable_AnyAbc.clear()
        _fooWithAlias.clear()
        _fooWithRList_Any.clear()
        _fooWithRList_ZAny.clear()
        _fooWithTComparable_ZAny.clear()
        _fooWithQ.clear()
        _fooWithTComparable_Q.clear()
        _fooWithZQ.clear()
        _fooWithTComparable_ZQ.clear()
        _fooWithTQString.clear()
        _fooWithTCharSequenceTZComparable_AnyQCharSequenceQZComparable_Any.clear()
        _ooWithIntArray_Any.clear()
        _ooWithZAnyArray_Int.clear()
        _ooWithAnyArray_TAny.clear()
        _ooWithAnyArray_RRR_TAny.clear()
    }
}
