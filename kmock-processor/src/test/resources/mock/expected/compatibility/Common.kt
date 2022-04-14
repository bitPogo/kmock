package mock.template.compatibility

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
    verifier: KMockContract.Collector = NoopCollector,
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
        ProxyFactory.createPropertyProxy("mock.template.compatibility.CommonMock#_foo", collector =
        verifier, freeze = freeze)

    public override var hashCode: Int
        get() = _hashCode.onGet()
        set(`value`) = _hashCode.onSet(value)

    public val _hashCode: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.compatibility.CommonMock#_hashCode", collector
        = verifier, freeze = freeze)

    public val _fooWithIntAny: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any>
        = ProxyFactory.createSyncFunProxy("mock.template.compatibility.CommonMock#_fooWithIntAny",
        collector = verifier, freeze = freeze)

    public val _fooWithAnyInt: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.Int) -> kotlin.Any>
        = ProxyFactory.createSyncFunProxy("mock.template.compatibility.CommonMock#_fooWithAnyInt",
        collector = verifier, freeze = freeze)

    public val _fooWithAnyString: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.String) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.CommonMock#_fooWithAnyString",
            collector = verifier, freeze = freeze)

    public val _fooWithStringAny: KMockContract.SyncFunProxy<Any, (kotlin.String, kotlin.Any) ->
    kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.CommonMock#_fooWithStringAny",
            collector = verifier, freeze = freeze)

    public val _fooWithStringMockTemplateCompatibilityAbc:
        KMockContract.SyncFunProxy<Any, (kotlin.String, mock.template.compatibility.Abc) ->
        kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.CommonMock#_fooWithStringMockTemplateCompatibilityAbc",
            collector = verifier, freeze = freeze)

    public val _fooWithFunction1: KMockContract.SyncFunProxy<Any, (kotlin.Function1<kotlin.Any,
        kotlin.Unit>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.CommonMock#_fooWithFunction1",
            collector = verifier, freeze = freeze)

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.CommonMock#_fooWithAny",
            collector = verifier, freeze = freeze)

    public val _fooWithMockTemplateCompatibilityCommon:
        KMockContract.SyncFunProxy<Unit, (mock.template.compatibility.Common) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.CommonMock#_fooWithMockTemplateCompatibilityCommon",
            collector = verifier, freeze = freeze)

    public val _fooWithMockTemplateCompatibilityLPG:
        KMockContract.SyncFunProxy<Unit, (mock.template.compatibility.LPG) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.CommonMock#_fooWithMockTemplateCompatibilityLPG",
            collector = verifier, freeze = freeze)

    public val _fooWithAnys: KMockContract.SyncFunProxy<Any, (Array<out kotlin.Any>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.compatibility.CommonMock#_fooWithAnys",
            collector = verifier, freeze = freeze)

    public override fun foo(fuzz: Int, ozz: Any): Any = _fooWithIntAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: Int): Any = _fooWithAnyInt.invoke(fuzz, ozz)

    public override fun foo(fuzz: Any, ozz: String): Any = _fooWithAnyString.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Any): Any = _fooWithStringAny.invoke(fuzz, ozz)

    public override fun foo(fuzz: String, ozz: Abc): Any =
        _fooWithStringMockTemplateCompatibilityAbc.invoke(fuzz, ozz)

    public override fun foo(fuzz: Function1<Any, Unit>): Any = _fooWithFunction1.invoke(fuzz)

    public override fun <T> foo(fuzz: T): Unit = _fooWithAny.invoke(fuzz) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Common> foo(fuzz: T): Unit =
        _fooWithMockTemplateCompatibilityCommon.invoke(fuzz) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : LPG> foo(fuzz: T): Unit =
        _fooWithMockTemplateCompatibilityLPG.invoke(fuzz) {
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
        _fooWithStringMockTemplateCompatibilityAbc.clear()
        _fooWithFunction1.clear()
        _fooWithAny.clear()
        _fooWithMockTemplateCompatibilityCommon.clear()
        _fooWithMockTemplateCompatibilityLPG.clear()
        _fooWithAnys.clear()
    }
}
