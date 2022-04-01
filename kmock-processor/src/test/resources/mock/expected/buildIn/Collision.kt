package mock.template.buildIn

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

internal class CollisionMock(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Collision? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Collision {
    public override val foo: String
        get() = _foo.onGet()

    public val _foo: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.buildIn.CollisionMock#_foo", collector =
        verifier, freeze = freeze)

    public override val bar: Int
        get() = _bar.onGet()

    public val _bar: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.buildIn.CollisionMock#_bar", collector =
        verifier, freeze = freeze)

    public override val hashCode: String
        get() = _hashCode.onGet()

    public val _hashCode: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.buildIn.CollisionMock#_hashCode", collector =
        verifier, freeze = freeze)

    public override var buzz: Any
        get() = _buzz.onGet()
        set(`value`) = _buzz.onSet(value)

    public val _buzz: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("mock.template.buildIn.CollisionMock#_buzz", collector =
        verifier, freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.buildIn.CollisionMock#_toString", collector =
        verifier, freeze = freeze, ignorableForVerification = true) {
            useToStringRelaxer { super.toString() }
        }

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.buildIn.CollisionMock#_equals", collector =
        verifier, freeze = freeze, ignorableForVerification = true) {
            useEqualsRelaxer { other ->
                super.equals(other)
            }
        }

    public val _hashCodeWithVoid: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.buildIn.CollisionMock#_hashCodeWithVoid",
            collector = verifier, freeze = freeze, ignorableForVerification = true) {
            useHashCodeRelaxer { super.hashCode() }
        }

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean = _equals.invoke(other)

    public override fun hashCode(): Int = _hashCodeWithVoid.invoke()

    public fun _clearMock(): Unit {
        _foo.clear()
        _bar.clear()
        _hashCode.clear()
        _buzz.clear()
        _toString.clear()
        _equals.clear()
        _hashCodeWithVoid.clear()
    }
}
