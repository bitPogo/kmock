package mock.template.`property`

import kotlin.Any
import kotlin.Boolean
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

internal class PlatformMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: Platform? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Platform {
    private val __spyOn: Platform? = spyOn

    public override val foo: String
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyProxy<String> by lazy(mode =
    LazyThreadSafetyMode.PUBLICATION) {
        if (spyOn == null) {
            PropertyProxy("mock.template.property.PlatformMock#_foo", spyOnGet = null, collector =
            verifier, freeze = freeze, relaxer = null)
        } else {
            PropertyProxy("mock.template.property.PlatformMock#_foo", spyOnGet = { spyOn.foo },
                collector = verifier, freeze = freeze, relaxer = null)
        }
    }

    public override val bar: Int
        get() = _bar.onGet()

    public val _bar: KMockContract.PropertyProxy<Int> by lazy(mode = LazyThreadSafetyMode.PUBLICATION)
    {
        if (spyOn == null) {
            PropertyProxy("mock.template.property.PlatformMock#_bar", spyOnGet = null, collector =
            verifier, freeze = freeze, relaxer = null)
        } else {
            PropertyProxy("mock.template.property.PlatformMock#_bar", spyOnGet = { spyOn.bar },
                collector = verifier, freeze = freeze, relaxer = null)
        }
    }

    public override var buzz: Any
        get() = _buzz.onGet()
        set(`value`) = _buzz.onSet(value)

    public val _buzz: KMockContract.PropertyProxy<Any> by lazy(mode =
    LazyThreadSafetyMode.PUBLICATION) {
        if (spyOn == null) {
            PropertyProxy("mock.template.property.PlatformMock#_buzz", spyOnGet = null, spyOnSet =
            null, collector = verifier, freeze = freeze, relaxer = null)
        } else {
            PropertyProxy("mock.template.property.PlatformMock#_buzz", spyOnGet = { spyOn.buzz },
                spyOnSet = { spyOn.buzz = it; Unit }, collector = verifier, freeze = freeze, relaxer =
                null)
        }
    }

    public fun _clearMock(): Unit {
        _foo.clear()
        _bar.clear()
        _buzz.clear()
    }
}
