package mock.template.spy

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class PlatformMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Platform {
    public override val uzz: Int
        get() = _uzz.onGet()

    public val _uzz: KMockContract.PropertyProxy<Int> =
        PropertyProxy("mock.template.spy.PlatformMock#_uzz", collector = verifier, freeze = freeze,
            relaxer = null)

    public override var fzz: Int
        get() = _fzz.onGet()
        set(`value`) = _fzz.onSet(value)

    public val _fzz: KMockContract.PropertyProxy<Int> =
        PropertyProxy("mock.template.spy.PlatformMock#_fzz", collector = verifier, freeze = freeze,
            relaxer = null)

    public val _foo: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunProxy("mock.template.spy.PlatformMock#_foo", spyOn = null, collector = verifier, freeze
        = freeze, relaxer = null)

    public val _bar: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunProxy("mock.template.spy.PlatformMock#_bar", spyOn = null, collector = verifier, freeze
        = freeze, relaxer = null)

    public override fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public fun _clearMock(): Unit {
        _uzz.clear()
        _fzz.clear()
        _foo.clear()
        _bar.clear()
    }
}
