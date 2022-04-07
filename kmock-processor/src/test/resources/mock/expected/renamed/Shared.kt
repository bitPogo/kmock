package mock.template.renamed

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SharedMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Shared<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false,
) : Shared<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.renamed.SharedMock#_template", collector =
        verifier, freeze = freeze)

    public override val ozz: Int
        get() = _ozz.onGet()

    public val _ozz: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.renamed.SharedMock#_ozz", collector =
        verifier, freeze = freeze)

    public val _bar: KMockContract.SyncFunProxy<Any, (kotlin.Int) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.renamed.SharedMock#_bar", collector = verifier,
            freeze = freeze)

    public val customName: KMockContract.AsyncFunProxy<L, suspend (kotlin.String) -> L> =
        ProxyFactory.createAsyncFunProxy("mock.template.renamed.SharedMock#customName", collector =
        verifier, freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.renamed.SharedMock#_toString", collector =
        verifier, freeze = freeze, ignorableForVerification = true) {
            useToStringRelaxer { super.toString() }
        }

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.renamed.SharedMock#_equals", collector =
        verifier, freeze = freeze, ignorableForVerification = true) {
            useEqualsRelaxer { other ->
                super.equals(other)
            }
        }

    public val noHash: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.renamed.SharedMock#noHash", collector =
        verifier, freeze = freeze, ignorableForVerification = true) {
            useHashCodeRelaxer { super.hashCode() }
        }

    public override fun bar(arg0: Int): Any = _bar.invoke(arg0)

    public override suspend fun buzz(arg0: String): L = customName.invoke(arg0)

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean = _equals.invoke(other)

    public override fun hashCode(): Int = noHash.invoke()

    public fun _clearMock(): Unit {
        _template.clear()
        _ozz.clear()
        _bar.clear()
        customName.clear()
        _toString.clear()
        _equals.clear()
        noHash.clear()
    }
}
