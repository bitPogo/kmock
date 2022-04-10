package mock.template.scoped

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class PlatformMock(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false,
) : Platform {
    public override var Something.thing: Int
        get() = throw IllegalStateException("This action is not callable.")
        set(`value`) = throw IllegalStateException("This action is not callable.")

    public override val SomethingElse<Any>.things: List<Any>
        get() = throw IllegalStateException("This action is not callable.")

    public override var Platform.extension: Int
        get() = throw IllegalStateException("This action is not callable.")
        set(`value`) = throw IllegalStateException("This action is not callable.")

    public override val <T> T.nothing: Unit where T : Something, T : Comparable<T>
        get() = throw IllegalStateException("This action is not callable.")

    public override val myThing: String
        get() = _myThing.onGet()

    public val _myThing: KMockContract.PropertyProxy<String> =
        ProxyFactory.createPropertyProxy("mock.template.scoped.PlatformMock#_myThing", collector =
        verifier, freeze = freeze)

    public override val AnythingElse.SomethingInside.inside: Int
        get() = throw IllegalStateException("This action is not callable.")

    public val _iDo: KMockContract.SyncFunProxy<Unit, () -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.scoped.PlatformMock#_iDo", collector =
        verifier, freeze = freeze) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun Something.equals(): Int = throw IllegalStateException(
        "This action is not callable."
    )

    public override fun Something.doSomething(): Int = throw IllegalStateException(
        "This action is not callable."
    )

    public override fun SomethingElse<Any>.doSomethingElse(): List<Any> = throw IllegalStateException(
        "This action is not callable."
    )

    public override fun Platform.mutabor(): Int = throw IllegalStateException(
        "This action is not callable."
    )

    public override fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T> = throw
    IllegalStateException(
        "This action is not callable."
    )

    public override fun <T, R : Any> T.doNothingElse(a: R): Unit where T : Something, T :
    Comparable<T> = throw IllegalStateException(
        "This action is not callable."
    )

    public override fun AnythingElse.SomethingInside.doInside(): Int = throw IllegalStateException(
        "This action is not callable."
    )

    public override fun iDo(): Unit = _iDo.invoke()

    public fun _clearMock(): Unit {
        _myThing.clear()
        _iDo.clear()
    }
}
