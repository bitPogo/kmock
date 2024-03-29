package mock.template.access

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.Map
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.SafeJvmName
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class BuildInMock(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: BuildIn? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : BuildIn {
    public val _foo: KMockContract.SyncFunProxy<Any, (Int, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.BuildInMock#_foo", collector =
        collector, freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> String> =
        ProxyFactory.createSyncFunProxy("mock.template.access.BuildInMock#_toString", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (Any?) -> Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.access.BuildInMock#_equals", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.access.BuildInMock#_hashCode", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    private val referenceStore: Map<String, KMockContract.Proxy<*, *>> = mapOf(
        "foo|(kotlin.Int, kotlin.Any) -> kotlin.Any|[]" to _foo,
        "toString|() -> kotlin.String|[]" to _toString,
        "equals|(kotlin.Any?) -> kotlin.Boolean|[]" to _equals,
        "hashCode|() -> kotlin.Int|[]" to _hashCode,
    )

    public override fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override fun toString(): String = _toString.invoke() {
        useRelaxerIf(true) { super.toString() }
    }

    public override fun equals(other: Any?): Boolean = _equals.invoke(other) {
        useRelaxerIf(true) { super.equals(other) }
    }

    public override fun hashCode(): Int = _hashCode.invoke() {
        useRelaxerIf(true) { super.hashCode() }
    }

    public fun _clearMock() {
        _foo.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf0")
    public fun syncFunProxyOf(reference: (Int, Any) -> Any): KMockContract.FunProxy<Any, (Int,
        Any) -> Any> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Int, kotlin.Any) -> kotlin.Any|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Int, kotlin.Any) -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (kotlin.Int, kotlin.Any) ->
        kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf1")
    public fun syncFunProxyOf(reference: () -> String): KMockContract.FunProxy<String, () -> String> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.String|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.String!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.String, () -> kotlin.String>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf2")
    public fun syncFunProxyOf(reference: (Any?) -> Boolean):
        KMockContract.FunProxy<Boolean, (Any?) -> Boolean> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any?) -> kotlin.Boolean|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any?) -> kotlin.Boolean!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Boolean, (kotlin.Any?) ->
        kotlin.Boolean>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf3")
    public fun syncFunProxyOf(reference: () -> Int): KMockContract.FunProxy<Int, () -> Int> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Int|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.Int!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Int, () -> kotlin.Int>
}
