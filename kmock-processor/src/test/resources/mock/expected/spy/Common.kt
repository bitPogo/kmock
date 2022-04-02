package mock.template.spy

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class CommonMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Common<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false,
) : Common<K, L> where L : Any, L : Comparable<L> {
    private val __spyOn: Common<K, L>? = spyOn

    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.spy.CommonMock#_template", collector =
        verifier, freeze = freeze) {
            useSpyOnGetIf(__spyOn) { __spyOn!!.template }
            useSpyOnSetIf(__spyOn) { value -> __spyOn!!.template = value }
        }

    public override val ozz: Int
        get() = _ozz.onGet()

    public val _ozz: KMockContract.PropertyProxy<Int> =
        ProxyFactory.createPropertyProxy("mock.template.spy.CommonMock#_ozz", collector = verifier,
            freeze = freeze) {
            useSpyOnGetIf(__spyOn) { __spyOn!!.ozz }
        }

    public val _foo: KMockContract.SyncFunProxy<Unit, (kotlin.Any?) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_foo", collector = verifier,
            freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { payload ->
                    __spyOn!!.foo(payload)
                }
            )
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _oo: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any?>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_oo", collector = verifier,
            freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { payload ->
                    __spyOn!!.oo(*payload)
                }
            )
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _bar: KMockContract.SyncFunProxy<Any, (kotlin.Int) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_bar", collector = verifier,
            freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { arg0 ->
                    __spyOn!!.bar(arg0)
                }
            )
        }

    public val _ar: KMockContract.SyncFunProxy<Any, (kotlin.IntArray) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_ar", collector = verifier,
            freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { arg0 ->
                    __spyOn!!.ar(*arg0)
                }
            )
        }

    public val _buzz: KMockContract.AsyncFunProxy<L, suspend (kotlin.String) -> L> =
        ProxyFactory.createAsyncFunProxy("mock.template.spy.CommonMock#_buzz", collector = verifier,
            freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { arg0 ->
                    __spyOn!!.buzz(arg0)
                }
            )
        }

    public val _uzz: KMockContract.AsyncFunProxy<L, suspend (Array<out kotlin.String>) -> L> =
        ProxyFactory.createAsyncFunProxy("mock.template.spy.CommonMock#_uzz", collector = verifier,
            freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { arg0 ->
                    __spyOn!!.uzz(*arg0)
                }
            )
        }

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_toString", collector =
        verifier, freeze = freeze, ignorableForVerification = true) {
            useToStringRelaxer { super.toString() }
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { __spyOn!!.toString() }
            )
        }

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_equals", collector = verifier,
            freeze = freeze, ignorableForVerification = true) {
            useEqualsRelaxer { other ->
                super.equals(other)
            }
            useSpyOnEqualsIf(
               spyTarget  = __spyOn,
               equals = { other ->
                    super.equals(other)
                },
                mockKlass = CommonMock::class
            )
        }

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.CommonMock#_hashCode", collector =
        verifier, freeze = freeze, ignorableForVerification = true) {
            useHashCodeRelaxer { super.hashCode() }
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { __spyOn!!.hashCode() }
            )
        }

    public override fun <T> foo(payload: T): Unit = _foo.invoke(payload)

    public override fun <T> oo(vararg payload: T): Unit = _oo.invoke(payload)

    public override fun bar(arg0: Int): Any = _bar.invoke(arg0)

    public override fun ar(vararg arg0: Int): Any = _ar.invoke(arg0)

    public override suspend fun buzz(arg0: String): L = _buzz.invoke(arg0)

    public override suspend fun uzz(vararg arg0: String): L = _uzz.invoke(arg0)

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean = _equals.invoke(other)

    public override fun hashCode(): Int = _hashCode.invoke()

    public fun _clearMock(): Unit {
        _template.clear()
        _ozz.clear()
        _foo.clear()
        _oo.clear()
        _bar.clear()
        _ar.clear()
        _buzz.clear()
        _uzz.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
