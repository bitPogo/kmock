package mock.template.access

import kotlin.Any
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.Map
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.SafeJvmName
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SyncFunMock<L, T>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: SyncFun<L, T>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : SyncFun<L, T> where T : CharSequence, T : Comparable<T> {
    public val _foo: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_foo", collector = verifier,
            freeze = freeze)

    public val _bar: KMockContract.SyncFunProxy<Any, (kotlin.Int, kotlin.Any) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_bar", collector = verifier,
            freeze = freeze)

    public val _ozz: KMockContract.SyncFunProxy<Any, (kotlin.IntArray) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_ozz", collector = verifier,
            freeze = freeze)

    public val _izz: KMockContract.SyncFunProxy<Any, (Array<out kotlin.Any>) -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_izz", collector = verifier,
            freeze = freeze)

    public val _uzz: KMockContract.SyncFunProxy<Unit, () -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_uzz", collector = verifier,
            freeze = freeze)

    public val _tuz: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_tuz", collector = verifier,
            freeze = freeze)

    public val _uz: KMockContract.SyncFunProxy<L, () -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_uz", collector = verifier,
            freeze = freeze)

    public val _tzz: KMockContract.SyncFunProxy<T, () -> T> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_tzz", collector = verifier,
            freeze = freeze)

    public val _lol: KMockContract.SyncFunProxy<Any?, (kotlin.Any?) -> kotlin.Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_lol", collector = verifier,
            freeze = freeze)

    public val _veryLongMethodNameWithABunchOfVariables: KMockContract.SyncFunProxy<Unit, (kotlin.Int,
        kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int,
        kotlin.Int, kotlin.Int) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_veryLongMethodNameWithABunchOfVariables",
            collector = verifier, freeze = freeze)

    private val referenceStore: Map<String, KMockContract.Proxy<*, *>> = mapOf(
        "foo|(kotlin.Int, kotlin.Any) -> kotlin.Any" to _foo,
        "bar|(kotlin.Int, kotlin.Any) -> kotlin.Any" to _bar,
        "ozz|(kotlin.IntArray) -> kotlin.Any" to _ozz,
        "izz|(Array<out kotlin.Any>) -> kotlin.Any" to _izz,
        "uzz|() -> kotlin.Unit" to _uzz,
        "tuz|() -> kotlin.Int" to _tuz,
        "uz|() -> L" to _uz,
        "tzz|() -> T" to _tzz,
        "lol|(kotlin.Any?) -> kotlin.Any?" to _lol,
        "veryLongMethodNameWithABunchOfVariables|(kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int) -> kotlin.Unit"
            to _veryLongMethodNameWithABunchOfVariables,
    )

    public override fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public override fun ozz(vararg buzz: Int): Any = _ozz.invoke(buzz)

    public override fun izz(vararg buzz: Any): Any = _izz.invoke(buzz)

    public override fun uzz(): Unit = _uzz.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun tuz(): Int = _tuz.invoke()

    public override fun uz(): L = _uz.invoke()

    public override fun tzz(): T = _tzz.invoke()

    @Suppress("UNCHECKED_CAST")
    public override fun <T> lol(arg: T): T = _lol.invoke(arg) as T

    public override fun veryLongMethodNameWithABunchOfVariables(
        arg0: Int,
        arg1: Int,
        arg2: Int,
        arg3: Int,
        arg4: Int,
        arg5: Int,
        arg6: Int,
        arg7: Int,
        arg8: Int,
        arg9: Int,
    ): Unit = _veryLongMethodNameWithABunchOfVariables.invoke(arg0, arg1, arg2, arg3, arg4, arg5,
        arg6, arg7, arg8, arg9) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _foo.clear()
        _bar.clear()
        _ozz.clear()
        _izz.clear()
        _uzz.clear()
        _tuz.clear()
        _uz.clear()
        _tzz.clear()
        _lol.clear()
        _veryLongMethodNameWithABunchOfVariables.clear()
    }

    @Suppress("UNCHECKED_CAST")
    @SafeJvmName("syncFunProxyOf0")
    public fun syncFunProxyOf(reference: (kotlin.Int, kotlin.Any) -> kotlin.Any):
        tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (kotlin.Int, kotlin.Any) ->
        kotlin.Any> =
        referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Int, kotlin.Any) -> kotlin.Any"""]
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (kotlin.Int, kotlin.Any) ->
        kotlin.Any>

    @Suppress("UNCHECKED_CAST")
    @SafeJvmName("syncFunProxyOf1")
    public fun syncFunProxyOf(reference: (kotlin.IntArray) -> kotlin.Any):
        tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (kotlin.IntArray) -> kotlin.Any> =
        referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.IntArray) -> kotlin.Any"""]
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (kotlin.IntArray) -> kotlin.Any>

    @Suppress("UNCHECKED_CAST")
    @SafeJvmName("syncFunProxyOf2")
    public fun syncFunProxyOf(reference: (Array<out kotlin.Any>) -> kotlin.Any):
        tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (Array<out kotlin.Any>) -> kotlin.Any>
        =
        referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(Array<out kotlin.Any>) -> kotlin.Any"""]
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (Array<out kotlin.Any>) ->
        kotlin.Any>

    @Suppress("UNCHECKED_CAST")
    @SafeJvmName("syncFunProxyOf3")
    public fun syncFunProxyOf(reference: () -> kotlin.Unit):
        tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, () -> kotlin.Unit> =
        referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Unit"""] as
            tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, () -> kotlin.Unit>

    @Suppress("UNCHECKED_CAST")
    @SafeJvmName("syncFunProxyOf4")
    public fun syncFunProxyOf(reference: () -> kotlin.Int):
        tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Int, () -> kotlin.Int> =
        referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Int"""] as
            tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Int, () -> kotlin.Int>

    @Suppress("UNCHECKED_CAST")
    @SafeJvmName("syncFunProxyOf5")
    public fun syncFunProxyOf(reference: () -> L): tech.antibytes.kmock.KMockContract.FunProxy<L,
            () -> L> = referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> L"""] as
        tech.antibytes.kmock.KMockContract.FunProxy<L, () -> L>

    @Suppress("UNCHECKED_CAST")
    @SafeJvmName("syncFunProxyOf6")
    public fun syncFunProxyOf(reference: () -> T): tech.antibytes.kmock.KMockContract.FunProxy<T,
            () -> T> = referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> T"""] as
        tech.antibytes.kmock.KMockContract.FunProxy<T, () -> T>

    @Suppress("UNCHECKED_CAST")
    @SafeJvmName("syncFunProxyOf7")
    public fun syncFunProxyOf(reference: (kotlin.Any?) -> kotlin.Any?):
        tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any?, (kotlin.Any?) -> kotlin.Any?> =
        referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any?) -> kotlin.Any?"""]
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any?, (kotlin.Any?) -> kotlin.Any?>

    @Suppress("UNCHECKED_CAST")
    @SafeJvmName("syncFunProxyOf8")
    public fun syncFunProxyOf(reference: (kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int,
        kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int) -> kotlin.Unit):
        tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Int, kotlin.Int, kotlin.Int,
            kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int) ->
        kotlin.Unit> =
        referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int) -> kotlin.Unit"""]
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Int, kotlin.Int,
            kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int, kotlin.Int,
            kotlin.Int) -> kotlin.Unit>
}
