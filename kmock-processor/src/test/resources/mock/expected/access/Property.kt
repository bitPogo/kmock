package mock.template.access

import kotlin.Any
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.reflect.KProperty
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PropertyMock<L, T>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Property<L, T>? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Property<L, T> where T : CharSequence, T : Comparable<T> {
    public override val foo: String
        get() = _foo.executeOnGet()

    public val _foo: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.access.PropertyMock#_foo", collector =
        collector, freeze = freeze)

    public override val bar: Int
        get() = _bar.executeOnGet()

    public val _bar: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.access.PropertyMock#_bar", collector =
        collector, freeze = freeze)

    public override val uzz: String
        get() = _uzz.executeOnGet()

    public val _uzz: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.access.PropertyMock#_uzz", collector =
        collector, freeze = freeze)

    public override val izz: L
        get() = _izz.executeOnGet()

    public val _izz: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.access.PropertyMock#_izz", collector =
        collector, freeze = freeze)

    public override val tuz: T
        get() = _tuz.executeOnGet()

    public val _tuz: KMockContract.PropertyProxy<T> =
        ProxyFactory.createPropertyProxy("mock.template.access.PropertyMock#_tuz", collector =
        collector, freeze = freeze)

    public override var buzz: Any
        get() = _buzz.executeOnGet()
        set(`value`) = _buzz.executeOnSet(value)

    public val _buzz: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.access.PropertyMock#_buzz", collector =
        collector, freeze = freeze)

    private val referenceStore: Map<String, KMockContract.Proxy<*, *>> = mapOf(
        "foo|property" to _foo,
        "bar|property" to _bar,
        "uzz|property" to _uzz,
        "izz|property" to _izz,
        "tuz|property" to _tuz,
        "buzz|property" to _buzz,
    )

    public fun _clearMock() {
        _foo.clear()
        _bar.clear()
        _uzz.clear()
        _izz.clear()
        _tuz.clear()
        _buzz.clear()
    }

    @Suppress("UNCHECKED_CAST")
    @KMockExperimental
    public fun <Property> propertyProxyOf(reference: KProperty<Property>):
        KMockContract.PropertyProxy<Property> = (referenceStore["""${reference.name}|property"""] ?:
    throw IllegalStateException("""Unknown property ${reference.name}!""")) as
        tech.antibytes.kmock.KMockContract.PropertyProxy<Property>
}
