package mock.template.`property`

import kotlin.Any
import kotlin.Boolean
import kotlin.Enum
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SharedMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Shared? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Shared {
    public override val foo: String
        get() = _foo.executeOnGet()

    public val _foo: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.property.SharedMock#_foo", collector =
        collector, freeze = freeze)

    public override val bar: Int
        get() = _bar.executeOnGet()

    public val _bar: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.property.SharedMock#_bar", collector =
        collector, freeze = freeze)

    public override val `enum`: Enum<*>
        get() = _enum.executeOnGet()

    public val _enum: KMockContract.PropertyProxy<Enum<*>> =
        ProxyFactory.createPropertyProxy("mock.template.property.SharedMock#_enum", collector =
        collector, freeze = freeze)

    public override var buzz: Any
        get() = _buzz.executeOnGet()
        set(`value`) = _buzz.executeOnSet(value)

    public val _buzz: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.property.SharedMock#_buzz", collector =
        collector, freeze = freeze)

    public fun _clearMock(): Unit {
        _foo.clear()
        _bar.clear()
        _enum.clear()
        _buzz.clear()
    }
}
