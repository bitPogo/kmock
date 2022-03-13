// COMMONTEST
package generatorTest

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

internal class AsyncFunctionCommonMock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: AsyncFunctionCommon? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : AsyncFunctionCommon {
    public val _foo: KMockContract.AsyncFunProxy<Any, suspend (kotlin.Int, kotlin.Any) -> kotlin.Any>
        = AsyncFunProxy("generatorTest.AsyncFunctionCommon#_foo", spyOn = if (spyOn != null) { { fuzz,
        ozz ->
        foo(fuzz, ozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _bar: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        SyncFunProxy("generatorTest.AsyncFunctionCommon#_bar", spyOn = if (spyOn != null) { { buzz,
            bozz ->
            bar(buzz, bozz) } } else { null }, collector = verifier, freeze = freeze, relaxer = null)

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        SyncFunProxy("generatorTest.AsyncFunctionCommon#_toString", spyOn = if (spyOn != null) { {
            toString() } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = null,
            relaxer = null, buildInRelaxer = { super.toString() })

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        SyncFunProxy("generatorTest.AsyncFunctionCommon#_equals", spyOn = if (spyOn != null) { {
                other ->
            equals(other) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = null,
            relaxer = null, buildInRelaxer = { other -> super.equals(other) })

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        SyncFunProxy("generatorTest.AsyncFunctionCommon#_hashCode", spyOn = if (spyOn != null) { {
            hashCode() } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = null,
            relaxer = null, buildInRelaxer = { super.hashCode() })

    public override suspend fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean = _equals.invoke(other)

    public override fun hashCode(): Int = _hashCode.invoke()

    public fun _clearMock(): Unit {
        _foo.clear()
        _bar.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
