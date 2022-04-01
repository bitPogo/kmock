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

internal class SharedMock(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Shared? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Shared {
    public override val foo: Any
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.SharedMock#_foo", collector =
        verifier, freeze = freeze)

    public override var hashCode: Int
        get() = _hashCode.onGet()
        set(`value`) = _hashCode.onSet(value)

    public val _hashCode: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.overloaded.SharedMock#_hashCode", collector =
        verifier, freeze = freeze)

    public val _fooWithIntAny: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any>
        = ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithIntAny",
        collector = verifier, freeze = freeze)

    public val _fooWithAnyInt: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.Int) -> kotlin.Any>
        = ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithAnyInt",
        collector = verifier, freeze = freeze)

    public val _fooWithAnyString: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.String) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithAnyString",
            collector = verifier, freeze = freeze)

    public val _fooWithStringAny: KMockContract.SyncFunProxy<Any, (kotlin.String, kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithStringAny",
            collector = verifier, freeze = freeze)

    public val _fooWithStringMockTemplateOverloadedAbc:
        KMockContract.SyncFunProxy<Any, (kotlin.String, mock.template.overloaded.Abc) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithStringMockTemplateOverloadedAbc",
            collector = verifier, freeze = freeze)

    public val _fooWithFunction1: KMockContract.SyncFunProxy<Any, (kotlin.Function1<kotlin.Any,
        kotlin.Unit>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithFunction1",
            collector = verifier, freeze = freeze)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithAny", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _fooWithMockTemplateOverloadedShared:
        KMockContract.SyncFunProxy<Unit, (mock.template.overloaded.Shared) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithMockTemplateOverloadedShared",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _fooWithMockTemplateOverloadedLPG:
        KMockContract.SyncFunProxy<Unit, (mock.template.overloaded.LPG) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.overloaded.SharedMock#_fooWithMockTemplateOverloadedLPG",
            collector = verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun foo(fuzz: Int, ozz: Any): Any = _fooWithIntAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: Int): Any = _fooWithAnyInt.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: String): Any = _fooWithAnyString.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Any): Any = _fooWithStringAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Abc): Any =
        _fooWithStringMockTemplateOverloadedAbc.invoke(fuzz, ozz)

    public override fun foo(fuzz: Function1<Any, Unit>): Any = _fooWithFunction1.invoke(fuzz)

    public override fun <T> foo(fuzz: T): Unit = _fooWithAny.invoke(fuzz)

    public override fun <T : Shared> foo(fuzz: T): Unit =
        _fooWithMockTemplateOverloadedShared.invoke(fuzz)

    public override fun <T : LPG> foo(fuzz: T): Unit = _fooWithMockTemplateOverloadedLPG.invoke(fuzz)

    public fun _clearMock(): Unit {
        _foo.clear()
        _hashCode.clear()
        _fooWithIntAny.clear()
        _fooWithAnyInt.clear()
        _fooWithAnyString.clear()
        _fooWithStringAny.clear()
        _fooWithStringMockTemplateOverloadedAbc.clear()
        _fooWithFunction1.clear()
        _fooWithAny.clear()
        _fooWithMockTemplateOverloadedShared.clear()
        _fooWithMockTemplateOverloadedLPG.clear()
    }
}
