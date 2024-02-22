package mock.template.`property`

import kotlin.Any
import kotlin.Boolean
import kotlin.Enum
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Platform {
    public override val foo: String
        get() = _foo.executeOnGet()

    public val _foo: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.property.PlatformMock#_foo", collector =
        collector, freeze = freeze)

    public override val bar: Int
        get() = _bar.executeOnGet()

    public val _bar: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.property.PlatformMock#_bar", collector =
        collector, freeze = freeze)

    public override var buzz: Any
        get() = _buzz.executeOnGet()
        set(`value`) = _buzz.executeOnSet(value)

    public val _buzz: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.property.PlatformMock#_buzz", collector =
        collector, freeze = freeze)

    public override val boo: Enum<*>
        get() = _boo.executeOnGet()

    public val _boo: KMockContract.PropertyProxy<Enum<*>> =
        ProxyFactory.createPropertyProxy("mock.template.property.PlatformMock#_boo", collector =
        collector, freeze = freeze)

    public fun _clearMock() {
        _foo.clear()
        _bar.clear()
        _buzz.clear()
        _boo.clear()
    }
}
