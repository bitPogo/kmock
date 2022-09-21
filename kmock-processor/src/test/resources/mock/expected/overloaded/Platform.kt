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
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformMock<Q : List<Int>>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform<Q>? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Platform<Q> {
    public override val foo: Any
        get() = _foo.executeOnGet()

    public val _foo: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.PlatformMock#_foo", collector =
        collector, freeze = freeze)

    public override var hashCode: Int
        get() = _hashCode.executeOnGet()
        set(`value`) = _hashCode.executeOnSet(value)

    public val _hashCode: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.PlatformMock#_hashCode", collector
        = collector, freeze = freeze)

    public val _fooWithIntAny: KMockContract.SyncFunProxy<Any, (Int, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithIntAny",
            collector = collector, freeze = freeze)

    public val _fooWithAnyInt: KMockContract.SyncFunProxy<Any, (Any?, Int) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithAnyInt",
            collector = collector, freeze = freeze)

    public val _fooWithAnyString: KMockContract.SyncFunProxy<Any, (Any, String) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithAnyString",
            collector = collector, freeze = freeze)

    public val _fooWithStringAny: KMockContract.SyncFunProxy<Any, (String, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithStringAny",
            collector = collector, freeze = freeze)

    public val _fooWithStringAbc: KMockContract.SyncFunProxy<Any, (String, Abc) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithStringAbc",
            collector = collector, freeze = freeze)

    public val _fooWithFunction1: KMockContract.SyncFunProxy<Any, (Function1<Any, Unit>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithFunction1",
            collector = collector, freeze = freeze)

    public val _fooWithZTAny: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithZTAny",
            collector = collector, freeze = freeze)

    public val _fooWithTPlatform: KMockContract.SyncFunProxy<Unit, (Platform<*>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithTPlatform",
            collector = collector, freeze = freeze)

    public val _fooWithTLPG: KMockContract.SyncFunProxy<Unit, (LPG) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithTLPG",
            collector = collector, freeze = freeze)

    public val _fooWithTCharSequenceTComparable: KMockContract.SyncFunProxy<Any, (Any?) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithTCharSequenceTComparable",
            collector = collector, freeze = freeze)

    public val _fooWithAnys: KMockContract.SyncFunProxy<Any, (Array<out Any>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithAnys",
            collector = collector, freeze = freeze)

    public val _fooWithQ: KMockContract.SyncFunProxy<Unit, (Q) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithQ", collector =
        collector, freeze = freeze)

    public val _fooWithTQString: KMockContract.SyncFunProxy<Unit, (Q, String) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithTQString",
            collector = collector, freeze = freeze)

    public val _fooWithTCharSequenceTComparableQCharSequenceQComparable:
        KMockContract.SyncFunProxy<Unit, (Any, Any) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.PlatformMock#_fooWithTCharSequenceTComparableQCharSequenceQComparable",
            collector = collector, freeze = freeze)

    public override fun foo(fuzz: Int, ozz: Any): Any = _fooWithIntAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any?, ozz: Int): Any = _fooWithAnyInt.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: String): Any = _fooWithAnyString.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Any): Any = _fooWithStringAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Abc): Any = _fooWithStringAbc.invoke(fuzz, ozz)

    public override fun foo(fuzz: Function1<Any, Unit>): Any = _fooWithFunction1.invoke(fuzz)

    public override fun <T> foo(fuzz: T): Unit = _fooWithZTAny.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Platform<*>> foo(fuzz: T): Unit = _fooWithTPlatform.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : LPG> foo(fuzz: T): Unit = _fooWithTLPG.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T> foo(fuzz: T?): Any where T : CharSequence?, T : Comparable<T> =
        _fooWithTCharSequenceTComparable.invoke(fuzz)

    public override fun foo(vararg fuzz: Any): Any = _fooWithAnys.invoke(fuzz)

    public override fun foo(arg: Q): Unit = _fooWithQ.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Q> foo(arg0: T, arg1: String): Unit = _fooWithTQString.invoke(arg0, arg1)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Q, Q> foo(arg0: T, arg1: Q): Unit where Q : CharSequence, Q :
    Comparable<Q>? = _fooWithTCharSequenceTComparableQCharSequenceQComparable.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _foo.clear()
        _hashCode.clear()
        _fooWithIntAny.clear()
        _fooWithAnyInt.clear()
        _fooWithAnyString.clear()
        _fooWithStringAny.clear()
        _fooWithStringAbc.clear()
        _fooWithFunction1.clear()
        _fooWithZTAny.clear()
        _fooWithTPlatform.clear()
        _fooWithTLPG.clear()
        _fooWithTCharSequenceTComparable.clear()
        _fooWithAnys.clear()
        _fooWithQ.clear()
        _fooWithTQString.clear()
        _fooWithTCharSequenceTComparableQCharSequenceQComparable.clear()
    }
}
