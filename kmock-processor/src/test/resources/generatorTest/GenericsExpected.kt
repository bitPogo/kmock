package generatorTest

import kotlin.Boolean
import kotlin.Unit
import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.SyncFunMockery

internal class GenericsMock<K : Any, L>(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: Generics<K, L>? = null,
    freeze: Boolean = true
) : Generics<K, L> {
    public override var template: K
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyMockery<K> =
        PropertyMockery("generatorTest.Generics#template", spyOnGet = if (spyOn != null) { {
            spyOn.template } } else { null }, spyOnSet = if (spyOn != null) { { spyOn.template = it } }
        else { null }, collector = verifier, freeze = freeze, )

    public val _foo: KMockContract.SyncFunMockery<Unit, (Any?) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.Generics#_foo", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, freeze = freeze, )

    public val _fooWithInt: KMockContract.SyncFunMockery<Unit, (Int) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.Generics#_fooWithInt", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, freeze = freeze, )

    public override fun <T> foo(payload: T): Unit = _foo.invoke(payload)

    public override fun <K : Int> foo(payload: K): Unit = _fooWithInt.invoke(payload)

    public fun _clearMock(): Unit {
        _template.clear()
        _foo.clear()
        _fooWithInt.clear()
    }
}
