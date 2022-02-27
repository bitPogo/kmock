package generatorTest

import kotlin.Boolean
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.mock.AsyncFunMockery
import tech.antibytes.kmock.mock.PropertyMockery
import tech.antibytes.kmock.mock.SyncFunMockery
import tech.antibytes.kmock.mock.relaxVoidFunction

internal class GenericsMock<K : Any, L>(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: Generics<K, L>? = null,
    freeze: Boolean = true,
    relaxUnitFun: Boolean = false
) : Generics<K, L> {
    public override var template: K
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyMockery<K> = if (spyOn == null) {
        PropertyMockery("generatorTest.Generics#_template", spyOnGet = null, spyOnSet = null,
            collector = verifier, freeze = freeze, relaxer = null)
    } else {
        PropertyMockery("generatorTest.Generics#_template", spyOnGet = { spyOn.template }, spyOnSet
        = { spyOn.template = it; Unit }, collector = verifier, freeze = freeze, relaxer = null)
    }


    public val _foo: KMockContract.SyncFunMockery<Unit, (Any?) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.Generics#_foo", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public val _fooWithInt: KMockContract.SyncFunMockery<Unit, (Int) -> kotlin.Unit> =
        SyncFunMockery("generatorTest.Generics#_fooWithInt", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null)

    public override fun <T> foo(payload: T): Unit = _foo.invoke(payload)

    public override fun <K : Int> foo(payload: K): Unit = _fooWithInt.invoke(payload)

    public fun _clearMock(): Unit {
        _template.clear()
        _foo.clear()
        _fooWithInt.clear()
    }
}
