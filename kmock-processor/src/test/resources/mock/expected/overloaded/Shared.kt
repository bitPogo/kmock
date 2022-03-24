package mock.template.overloaded

import kotlin.Any
import kotlin.Boolean
import kotlin.Function1
import kotlin.Int
import kotlin.LazyThreadSafetyMode
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class SharedMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: Shared? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Shared {
    private val __spyOn: Shared? = spyOn

    public override val foo: Any
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyProxy<Any> by lazy(mode = LazyThreadSafetyMode.PUBLICATION)
    {
        if (spyOn == null) {
            PropertyProxy("mock.template.overloaded.SharedMock#_foo", spyOnGet = null, collector =
            verifier, freeze = freeze, relaxer = null)
        } else {
            PropertyProxy("mock.template.overloaded.SharedMock#_foo", spyOnGet = { spyOn.foo },
                collector = verifier, freeze = freeze, relaxer = null)
        }
    }

    public override var hashCode: Int
        get() = _hashCode.onGet()
        set(`value`) = _hashCode.onSet(value)

    public val _hashCode: KMockContract.PropertyProxy<Int> by lazy(mode =
    LazyThreadSafetyMode.PUBLICATION) {
        if (spyOn == null) {
            PropertyProxy("mock.template.overloaded.SharedMock#_hashCode", spyOnGet = null,
                spyOnSet = null, collector = verifier, freeze = freeze, relaxer = null)
        } else {
            PropertyProxy("mock.template.overloaded.SharedMock#_hashCode", spyOnGet = {
                spyOn.hashCode }, spyOnSet = { spyOn.hashCode = it; Unit }, collector = verifier, freeze =
            freeze, relaxer = null)
        }
    }

    public val _fooWithIntAny: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any>
        by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
            SyncFunProxy("mock.template.overloaded.SharedMock#_fooWithIntAny", spyOn = if (spyOn !=
                null) { { fuzz, ozz ->
                foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)
        }

    public val _fooWithAnyInt: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.Int) -> kotlin.Any>
        by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
            SyncFunProxy("mock.template.overloaded.SharedMock#_fooWithAnyInt", spyOn = if (spyOn !=
                null) { { fuzz, ozz ->
                foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)
        }

    public val _fooWithAnyString: KMockContract.SyncFunProxy<Any, (kotlin.Any, kotlin.String) ->
    kotlin.Any> by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
        SyncFunProxy("mock.template.overloaded.SharedMock#_fooWithAnyString", spyOn = if (spyOn !=
            null) { { fuzz, ozz ->
            foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)
    }

    public val _fooWithStringAny: KMockContract.SyncFunProxy<Any, (kotlin.String, kotlin.Any) ->
    kotlin.Any> by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
        SyncFunProxy("mock.template.overloaded.SharedMock#_fooWithStringAny", spyOn = if (spyOn !=
            null) { { fuzz, ozz ->
            foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)
    }

    public val _fooWithStringMockTemplateOverloadedAbc:
        KMockContract.SyncFunProxy<Any, (kotlin.String, mock.template.overloaded.Abc) -> kotlin.Any>
        by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
            SyncFunProxy("mock.template.overloaded.SharedMock#_fooWithStringMockTemplateOverloadedAbc",
                spyOn = if (spyOn != null) { { fuzz, ozz ->
                    foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)
        }

    public val _fooWithFunction1: KMockContract.SyncFunProxy<Any, (kotlin.Function1<kotlin.Any,
        kotlin.Unit>) -> kotlin.Any> by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
        SyncFunProxy("mock.template.overloaded.SharedMock#_fooWithFunction1", spyOn = if (spyOn !=
            null) { { fuzz ->
            foo(fuzz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)
    }

    public val _fooWithAny: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> by
    lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
        SyncFunProxy("mock.template.overloaded.SharedMock#_fooWithAny", spyOn = if (spyOn != null)
        { { fuzz ->
            foo(fuzz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)
    }

    public val _fooWithMockTemplateOverloadedShared:
        KMockContract.SyncFunProxy<Unit, (mock.template.overloaded.Shared) -> kotlin.Unit> by
    lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
        SyncFunProxy("mock.template.overloaded.SharedMock#_fooWithMockTemplateOverloadedShared",
            spyOn = if (spyOn != null) { { fuzz ->
                foo(fuzz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                         (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
            null)
    }

    public val _fooWithMockTemplateOverloadedLPG:
        KMockContract.SyncFunProxy<Unit, (mock.template.overloaded.LPG) -> kotlin.Unit> by lazy(mode =
    LazyThreadSafetyMode.PUBLICATION) {
        SyncFunProxy("mock.template.overloaded.SharedMock#_fooWithMockTemplateOverloadedLPG", spyOn
        = if (spyOn != null) { { fuzz ->
            foo(fuzz) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                     (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)
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
