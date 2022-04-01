package mock.template.`property`

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformMock(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Platform {
    public override val foo: String
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.property.PlatformMock#_foo", collector =
        verifier, freeze = freeze)

    public override val bar: Int
        get() = _bar.onGet()

    public val _bar: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.property.PlatformMock#_bar", collector =
        verifier, freeze = freeze)

    public override var buzz: Any
        get() = _buzz.onGet()
        set(`value`) = _buzz.onSet(value)

    public val _buzz: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.property.PlatformMock#_buzz", collector =
        verifier, freeze = freeze)

    public fun _clearMock(): Unit {
        _foo.clear()
        _bar.clear()
        _buzz.clear()
    }
}
