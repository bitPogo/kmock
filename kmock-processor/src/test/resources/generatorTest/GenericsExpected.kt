package generatorTest

import kotlin.Unit
import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.SyncFunMockery

internal class GenericsStub<K: Any, L>(
    verifier: KMockContract.Collector = KMockContract.Collector { _, _ -> Unit }
) : Generics<K, L> {
    public override val template: K
        get() = templateProp.onGet()

    public val templateProp: KMockContract.PropertyMockery<K> =
        PropertyMockery("generatorTest.Generics#template", verifier)

    public val fooFun: KMockContract.SyncFunMockery<Unit, (Any?) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.Generics#fooFun", verifier)

    public val fooWithKFun: KMockContract.SyncFunMockery<Unit, (Int) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.Generics#fooWithKFun", verifier)

    public override fun <T> foo(payload: T): Unit = fooFun.invoke(payload)

    public override fun <K : Int> foo(payload: K): Unit = fooWithKFun.invoke(payload)

    public fun clear(): Unit {
        templateProp.clear()
        fooFun.clear()
        fooWithKFun.clear()
    }
}
