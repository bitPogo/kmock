package mock.template.overloaded

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Function1
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SharedMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Shared? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Shared {
    public override val foo: Any
        get() = _foo.executeOnGet()

    public val _foo: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.SharedMock#_foo", collector =
        collector, freeze = freeze)

    public override var hashCode: Int
        get() = _hashCode.executeOnGet()
        set(`value`) = _hashCode.executeOnSet(value)

    public val _hashCode: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.SharedMock#_hashCode", collector =
        collector, freeze = freeze)

    public val _fooWithIntAny: KMockContract.SyncFunProxy<Any, (Int, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithIntAny",
            collector = collector, freeze = freeze)

    public val _fooWithAnyInt: KMockContract.SyncFunProxy<Any, (Any, Int) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithAnyInt",
            collector = collector, freeze = freeze)

    public val _fooWithAnyString: KMockContract.SyncFunProxy<Any, (Any, String) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithAnyString",
            collector = collector, freeze = freeze)

    public val _fooWithStringAny: KMockContract.SyncFunProxy<Any, (String, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithStringAny",
            collector = collector, freeze = freeze)

    public val _fooWithStringAbc: KMockContract.SyncFunProxy<Any, (String, Abc) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithStringAbc",
            collector = collector, freeze = freeze)

    public val _fooWithFunction1: KMockContract.SyncFunProxy<Any, (Function1<Any, Unit>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithFunction1",
            collector = collector, freeze = freeze)

    public val _fooWithZTAny: KMockContract.SyncFunProxy<Unit, (Any?) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithZTAny", collector
        = collector, freeze = freeze)

    public val _fooWithTShared: KMockContract.SyncFunProxy<Unit, (Shared) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithTShared",
            collector = collector, freeze = freeze)

    public val _fooWithTLPG: KMockContract.SyncFunProxy<Unit, (LPG) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithTLPG", collector
        = collector, freeze = freeze)

    public val _fooWithTAbc: KMockContract.SyncFunProxy<Unit, (Abc) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithTAbc", collector
        = collector, freeze = freeze)

    public val _fooWithArray: KMockContract.SyncFunProxy<Unit, (Array<in Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithArray", collector
        = collector, freeze = freeze)

    public override fun foo(fuzz: Int, ozz: Any): Any = _fooWithIntAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: Int): Any = _fooWithAnyInt.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: String): Any = _fooWithAnyString.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Any): Any = _fooWithStringAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Abc): Any = _fooWithStringAbc.invoke(fuzz, ozz)

    public override fun foo(fuzz: Function1<Any, Unit>): Any = _fooWithFunction1.invoke(fuzz)

    public override fun <T> foo(fuzz: T): Unit = _fooWithZTAny.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Shared> foo(fuzz: T): Unit = _fooWithTShared.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : LPG> foo(fuzz: T): Unit = _fooWithTLPG.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : R, R : Abc> foo(fuzz: T): Unit = _fooWithTAbc.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun foo(fuzz: Array<in Any>): Unit = _fooWithArray.invoke(fuzz) {
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
        _fooWithTShared.clear()
        _fooWithTLPG.clear()
        _fooWithTAbc.clear()
        _fooWithArray.clear()
    }
}
