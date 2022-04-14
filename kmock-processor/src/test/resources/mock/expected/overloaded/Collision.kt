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

internal class CollisionMock(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Collision? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Collision {
    public override val foo: Any
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.CollisionMock#_foo", collector =
        verifier, freeze = freeze)

    public override var hashCode: Int
        get() = _hashCode.onGet()
        set(`value`) = _hashCode.onSet(value)

    public val _hashCode: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.CollisionMock#_hashCode", collector
        = verifier, freeze = freeze)

    public val _fooWithIntAny: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any>
        = ProxyFactory.createSyncFunProxy("mock.template.overloaded.CollisionMock#_fooWithIntAny",
        collector = verifier, freeze = freeze)

    public val _fooWithAnyInt: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.Int) -> kotlin.Any>
        = ProxyFactory.createSyncFunProxy("mock.template.overloaded.CollisionMock#_fooWithAnyInt",
        collector = verifier, freeze = freeze)

    public val _fooWithAnyString: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.String) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CollisionMock#_fooWithAnyString",
            collector = verifier, freeze = freeze)

    public val _fooWithStringAny: KMockContract.SyncFunProxy<Any, (kotlin.String, kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CollisionMock#_fooWithStringAny",
            collector = verifier, freeze = freeze)

    public val _fooWithStringAbc: KMockContract.SyncFunProxy<Any, (kotlin.String,
        mock.template.overloaded.Abc) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CollisionMock#_fooWithStringAbc",
            collector = verifier, freeze = freeze)

    public val _fooWithStringScopedAbc: KMockContract.SyncFunProxy<Any, (kotlin.String,
        mock.template.overloaded.Scope.Abc) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CollisionMock#_fooWithStringScopedAbc",
            collector = verifier, freeze = freeze)

    public val _fooWithFunction1: KMockContract.SyncFunProxy<Any, (kotlin.Function1<kotlin.Any,
        kotlin.Unit>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CollisionMock#_fooWithFunction1",
            collector = verifier, freeze = freeze)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CollisionMock#_fooWithAny",
            collector = verifier, freeze = freeze)

    public val _fooWithCollision:
        KMockContract.SyncFunProxy<Unit, (mock.template.overloaded.Collision) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CollisionMock#_fooWithCollision",
            collector = verifier, freeze = freeze)

    public val _fooWithLPG: KMockContract.SyncFunProxy<Unit, (mock.template.overloaded.LPG) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CollisionMock#_fooWithLPG",
            collector = verifier, freeze = freeze)

    public val _fooWithAnys: KMockContract.SyncFunProxy<Any, (Array<out kotlin.Any>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.CollisionMock#_fooWithAnys",
            collector = verifier, freeze = freeze)

    public override fun foo(fuzz: Int, ozz: Any): Any = _fooWithIntAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: Int): Any = _fooWithAnyInt.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: String): Any = _fooWithAnyString.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Any): Any = _fooWithStringAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Abc): Any = _fooWithStringAbc.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Scope.Abc): Any = _fooWithStringScopedAbc.invoke(fuzz,
        ozz)

    public override fun foo(fuzz: Function1<Any, Unit>): Any = _fooWithFunction1.invoke(fuzz)

    public override fun <T> foo(fuzz: T): Unit = _fooWithAny.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Collision> foo(fuzz: T): Unit = _fooWithCollision.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : LPG> foo(fuzz: T): Unit = _fooWithLPG.invoke(fuzz) {
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
        _fooWithStringScopedAbc.clear()
        _fooWithFunction1.clear()
        _fooWithAny.clear()
        _fooWithCollision.clear()
        _fooWithLPG.clear()
        _fooWithAnys.clear()
    }
}
