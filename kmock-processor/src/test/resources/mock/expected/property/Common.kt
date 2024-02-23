package mock.template.`property`

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Common {
    public override val foo: String
        get() = _foo.executeOnGet()

    public val _foo: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.property.CommonMock#_foo", collector =
        collector, freeze = freeze)

    public override val bar: Int
        get() = _bar.executeOnGet()

    public val _bar: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.property.CommonMock#_bar", collector =
        collector, freeze = freeze)

    public override var buzz: Any
        get() = _buzz.executeOnGet()
        set(`value`) = _buzz.executeOnSet(value)

    public val _buzz: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.property.CommonMock#_buzz", collector =
        collector, freeze = freeze)

    public fun _clearMock() {
        _foo.clear()
        _bar.clear()
        _buzz.clear()
    }
}
