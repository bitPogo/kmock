package mock.template.overloaded

import kotlin.Any
import kotlin.Boolean
import kotlin.Function1
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Common {
    public override val foo: Any
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.CommonMock#_foo", collector =
        collector, freeze = freeze)

    public override var hashCode: Int
        get() = _hashCode.onGet()
        set(`value`) = _hashCode.onSet(value)

    public val _hashCode: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.CommonMock#_hashCode", collector =
        collector, freeze = freeze)

    public val _fooWithIntAny: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any>
        = ProxyFactory.createSyncFunProxy("mock.template.overloaded.CommonMock#_fooWithIntAny",
        collector = collector, freeze = freeze)

    public val _fooWithAnyInt: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.Int) -> kotlin.Any>
        = ProxyFactory.createSyncFunProxy("mock.template.overloaded.CommonMock#_fooWithAnyInt",
        collector = collector, freeze = freeze)

    public val _fooWithAnyString: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.String) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CommonMock#_fooWithAnyString",
            collector = collector, freeze = freeze)

    public val _fooWithStringAny: KMockContract.SyncFunProxy<Any, (kotlin.String, kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CommonMock#_fooWithStringAny",
            collector = collector, freeze = freeze)

    public val _fooWithStringAbc: KMockContract.SyncFunProxy<Any, (kotlin.String,
        mock.template.overloaded.Abc) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CommonMock#_fooWithStringAbc",
            collector = collector, freeze = freeze)

    public val _fooWithFunction1: KMockContract.SyncFunProxy<Any, (kotlin.Function1<kotlin.Any,
        kotlin.Unit>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CommonMock#_fooWithFunction1",
            collector = collector, freeze = freeze)

    public val _fooWithZTAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CommonMock#_fooWithZTAny", collector
        = collector, freeze = freeze)

    public val _fooWithTCommon: KMockContract.SyncFunProxy<Unit, (mock.template.overloaded.Common) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CommonMock#_fooWithTCommon",
            collector = collector, freeze = freeze)

    public val _fooWithTLPG: KMockContract.SyncFunProxy<Unit, (mock.template.overloaded.LPG) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CommonMock#_fooWithTLPG", collector
        = collector, freeze = freeze)

    public val _fooWithAnys: KMockContract.SyncFunProxy<Any, (kotlin.Array<out kotlin.Any>) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CommonMock#_fooWithAnys", collector
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

    public override fun <T : Common> foo(fuzz: T): Unit = _fooWithTCommon.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : LPG> foo(fuzz: T): Unit = _fooWithTLPG.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun foo(vararg fuzz: Any): Any = _fooWithAnys.invoke(fuzz)

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
        _fooWithTCommon.clear()
        _fooWithTLPG.clear()
        _fooWithAnys.clear()
    }
}
