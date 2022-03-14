package generatorTest

import generatorTest.relaxed
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class RelaxedMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: Relaxed? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : Relaxed {
    public override val buzz: String
        get() = _buzz.onGet()

    public val _buzz: KMockContract.PropertyProxy<String> = if (spyOn == null) {
        PropertyProxy("generatorTest.Relaxed#_buzz", spyOnGet = null, collector = verifier, freeze
        = freeze, relaxer = if (relaxed) { { mockId -> relaxed(mockId) } } else { null })
    } else {
        PropertyProxy("generatorTest.Relaxed#_buzz", spyOnGet = { spyOn.buzz }, collector =
        verifier, freeze = freeze, relaxer = if (relaxed) { { mockId -> relaxed(mockId) } } else {
            null })
    }


    public val _foo: KMockContract.SyncFunProxy<String, (kotlin.Any) -> kotlin.String> =
        SyncFunProxy("generatorTest.Relaxed#_foo", spyOn = if (spyOn != null) { { payload ->
            foo(payload) } } else { null }, collector = verifier, freeze = freeze, relaxer = if (relaxed)
        { { mockId -> relaxed(mockId) } } else { null })

    public val _bar: KMockContract.AsyncFunProxy<String, suspend (kotlin.Any) -> kotlin.String> =
        AsyncFunProxy("generatorTest.Relaxed#_bar", spyOn = if (spyOn != null) { { payload ->
            bar(payload) } } else { null }, collector = verifier, freeze = freeze, relaxer = if (relaxed)
        { { mockId -> relaxed(mockId) } } else { null })

    public val _buzzWithVoid: KMockContract.SyncFunProxy<Unit, () -> kotlin.Unit> =
        SyncFunProxy("generatorTest.Relaxed#_buzzWithVoid", spyOn = if (spyOn != null) { { buzz() } }
        else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
            relaxVoidFunction() } } else { null }, relaxer = if (relaxed) { { mockId -> relaxed(mockId) }
        } else { null }, buildInRelaxer = null)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        SyncFunProxy("generatorTest.Relaxed#_toString", spyOn = if (spyOn != null) { {
            spyOn.toString() } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer =
        null, relaxer = null, buildInRelaxer = { super.toString() }, ignorableForVerification = true)

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        SyncFunProxy("generatorTest.Relaxed#_equals", spyOn = if (spyOn != null) { { other ->
            spyOn.equals(other) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer =
        null, relaxer = null, buildInRelaxer = { other -> super.equals(other) },
            ignorableForVerification = true)

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        SyncFunProxy("generatorTest.Relaxed#_hashCode", spyOn = if (spyOn != null) { {
            spyOn.hashCode() } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer =
        null, relaxer = null, buildInRelaxer = { super.hashCode() }, ignorableForVerification = true)

    public override fun foo(payload: Any): String = _foo.invoke(payload)

    public override suspend fun bar(payload: Any): String = _bar.invoke(payload)

    public override fun buzz(): Unit = _buzzWithVoid.invoke()

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean = _equals.invoke(other)

    public override fun hashCode(): Int = _hashCode.invoke()

    public fun _clearMock(): Unit {
        _buzz.clear()
        _foo.clear()
        _bar.clear()
        _buzzWithVoid.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
