package multi

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import multi.template.shared.Regular1
import multi.template.shared.SharedContractRegular
import multi.template.shared.nested.Regular3
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SharedMultiMock<MultiMock>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: MultiMock? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Regular1, SharedContractRegular.Regular2, Regular3 where MultiMock : Regular1, MultiMock :
SharedContractRegular.Regular2, MultiMock : Regular3 {
    public override val something: Int
        get() = _something.executeOnGet()

    public val _something: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("multi.SharedMultiMock#_something", collector = collector,
            freeze = freeze)

    public override val anything: Any
        get() = _anything.executeOnGet()

    public val _anything: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("multi.SharedMultiMock#_anything", collector = collector,
            freeze = freeze)

    public override val somethingElse: String
        get() = _somethingElse.executeOnGet()

    public val _somethingElse: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("multi.SharedMultiMock#_somethingElse", collector =
        collector, freeze = freeze)

    public val _doSomething: KMockContract.SyncFunProxy<Int, () -> Int> =
        ProxyFactory.createSyncFunProxy("multi.SharedMultiMock#_doSomething", collector = collector,
            freeze = freeze)

    public val _doAnything: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("multi.SharedMultiMock#_doAnything", collector = collector,
            freeze = freeze)

    public val _doSomethingElse: KMockContract.SyncFunProxy<String, () -> String> =
        ProxyFactory.createSyncFunProxy("multi.SharedMultiMock#_doSomethingElse", collector =
        collector, freeze = freeze)

    public override fun doSomething(): Int = _doSomething.invoke()

    public override fun doAnything(): Any = _doAnything.invoke()

    public override fun doSomethingElse(): String = _doSomethingElse.invoke()

    public fun _clearMock(): Unit {
        _something.clear()
        _anything.clear()
        _somethingElse.clear()
        _doSomething.clear()
        _doAnything.clear()
        _doSomethingElse.clear()
    }
}
