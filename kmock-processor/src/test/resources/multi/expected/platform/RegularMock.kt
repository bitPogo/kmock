package multi

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import multi.template.platform.PlatformContractRegular
import multi.template.platform.Regular1
import multi.template.platform.nested.Regular3
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformMultiMock<MultiMock>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: MultiMock? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Regular1, PlatformContractRegular.Regular2, Regular3 where MultiMock : Regular1, MultiMock :
PlatformContractRegular.Regular2, MultiMock : Regular3 {
    public override val something: Int
        get() = _something.onGet()

    public val _something: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("multi.PlatformMultiMock#_something", collector = verifier,
            freeze = freeze)

    public override val anything: Any
        get() = _anything.onGet()

    public val _anything: KMockContract.PropertyProxy<Any> =
        ProxyFactory.createPropertyProxy("multi.PlatformMultiMock#_anything", collector = verifier,
            freeze = freeze)

    public override val somethingElse: String
        get() = _somethingElse.onGet()

    public val _somethingElse: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("multi.PlatformMultiMock#_somethingElse", collector =
        verifier, freeze = freeze)

    public val _doSomething: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("multi.PlatformMultiMock#_doSomething", collector = verifier,
            freeze = freeze)

    public val _doAnything: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("multi.PlatformMultiMock#_doAnything", collector = verifier,
            freeze = freeze)

    public val _doSomethingElse: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("multi.PlatformMultiMock#_doSomethingElse", collector =
        verifier, freeze = freeze)

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
