package mock.template.spy

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class BuildInMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : BuildIn {
    public override val uzz: Int
        get() = _uzz.onGet()

    public val _uzz: KMockContract.PropertyProxy<Int> =
        PropertyProxy("mock.template.spy.BuildInMock#_uzz", collector = verifier, freeze = freeze,
            relaxer = null)

    public override var fzz: Int
        get() = _fzz.onGet()
        set(`value`) = _fzz.onSet(value)

    public val _fzz: KMockContract.PropertyProxy<Int> =
        PropertyProxy("mock.template.spy.BuildInMock#_fzz", collector = verifier, freeze = freeze,
            relaxer = null)

    public val _foo: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunProxy("mock.template.spy.BuildInMock#_foo", spyOn = null, collector = verifier, freeze
        = freeze, relaxer = null)

    public val _bar: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunProxy("mock.template.spy.BuildInMock#_bar", spyOn = null, collector = verifier, freeze
        = freeze, relaxer = null)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        SyncFunProxy("mock.template.spy.BuildInMock#_toString", spyOn = null, collector = verifier,
            freeze = freeze, unitFunRelaxer = null, relaxer = null, buildInRelaxer = { super.toString() },
            ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        SyncFunProxy("mock.template.spy.BuildInMock#_equals", spyOn = null, collector = verifier,
            freeze = freeze, unitFunRelaxer = null, relaxer = null, buildInRelaxer = { other ->
                super.equals(other) }, ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        SyncFunProxy("mock.template.spy.BuildInMock#_hashCode", spyOn = null, collector = verifier,
            freeze = freeze, unitFunRelaxer = null, relaxer = null, buildInRelaxer = { super.hashCode() },
            ignorableForVerification = true)

    public override fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean = _equals.invoke(other)

    public override fun hashCode(): Int = _hashCode.invoke()

    public fun _clearMock(): Unit {
        _uzz.clear()
        _fzz.clear()
        _foo.clear()
        _bar.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
