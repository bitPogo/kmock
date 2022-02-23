package generatorTest

import kotlin.Unit
import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.SyncFunMockery

internal class GenericsMock<K : Any, L>(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: Generics<K, L>? = null
) : Generics<K, L> {
    public override var template: K
        get() = templateProp.onGet()
        set(`value`) = templateProp.onSet(value)

    public val templateProp: KMockContract.PropertyMockery<K> =
        PropertyMockery("generatorTest.Generics#templateProp", spyOnGet = if (spyOn != null) {
            spyOn::template::get } else { null }, spyOnSet = if (spyOn != null) { spyOn::template::set }
        else { null }, collector = verifier, )

    public val fooFun: KMockContract.SyncFunMockery<Unit, (Any?) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.Generics#fooFun", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, )

    public val fooWithKFun: KMockContract.SyncFunMockery<Unit, (Int) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.Generics#fooWithKFun", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, )

    public override fun <T> foo(payload: T): Unit = fooFun.invoke(payload)

    public override fun <K : Int> foo(payload: K): Unit = fooWithKFun.invoke(payload)

    public fun clearMock(): Unit {
        templateProp.clear()
        fooFun.clear()
        fooWithKFun.clear()
    }
}
