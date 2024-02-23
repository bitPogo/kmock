package mock.template.renamed

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonMock<K : Any, L>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common<K, L>? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Common<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.executeOnGet()
        set(`value`) = _template.executeOnSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.renamed.CommonMock#_template", collector =
        collector, freeze = freeze)

    public override val ozz: Int
        get() = _ozz.executeOnGet()

    public val _ozz: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.renamed.CommonMock#_ozz", collector =
        collector, freeze = freeze)

    public val _bar: KMockContract.SyncFunProxy<Any, (Int) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.renamed.CommonMock#_bar", collector =
        collector, freeze = freeze)

    public val customName: KMockContract.AsyncFunProxy<L, suspend (String) -> L> =
        ProxyFactory.createAsyncFunProxy("mock.template.renamed.CommonMock#customName", collector =
        collector, freeze = freeze)

    public val _toString: KMockContract.SyncFunProxy<String, () -> String> =
        ProxyFactory.createSyncFunProxy("mock.template.renamed.CommonMock#_toString", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (Any?) -> Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.renamed.CommonMock#_equals", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    public val noHash: KMockContract.SyncFunProxy<Int, () -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.renamed.CommonMock#noHash", collector =
        collector, freeze = freeze, ignorableForVerification = true)

    public override fun bar(arg0: Int): Any = _bar.invoke(arg0)

    public override suspend fun buzz(arg0: String): L = customName.invoke(arg0)

    public override fun toString(): String = _toString.invoke() {
        useRelaxerIf(true) { super.toString() }
    }

    public override fun equals(other: Any?): Boolean = _equals.invoke(other) {
        useRelaxerIf(true) { super.equals(other) }
    }

    public override fun hashCode(): Int = noHash.invoke() {
        useRelaxerIf(true) { super.hashCode() }
    }

    public fun _clearMock() {
        _template.clear()
        _ozz.clear()
        _bar.clear()
        customName.clear()
        _toString.clear()
        _equals.clear()
        noHash.clear()
    }
}
