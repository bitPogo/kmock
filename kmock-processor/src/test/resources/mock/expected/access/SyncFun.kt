package mock.template.access

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Int
import kotlin.IntArray
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.Map
import tech.antibytes.kmock.Hint0
import tech.antibytes.kmock.Hint1
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.SafeJvmName
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class SyncFunMock<L, T>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: SyncFun<L, T>? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : SyncFun<L, T> where T : CharSequence, T : Comparable<T> {
    public val _foo: KMockContract.SyncFunProxy<Any, (Int, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_foo", collector =
        collector, freeze = freeze)

    public val _bar: KMockContract.SyncFunProxy<Any, (Int, Any) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_bar", collector =
        collector, freeze = freeze)

    public val _ozz: KMockContract.SyncFunProxy<Any, (IntArray) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_ozz", collector =
        collector, freeze = freeze)

    public val _izz: KMockContract.SyncFunProxy<Any, (Array<out Any>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_izz", collector =
        collector, freeze = freeze)

    public val _uzz: KMockContract.SyncFunProxy<Unit, () -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_uzz", collector =
        collector, freeze = freeze)

    public val _tuz: KMockContract.SyncFunProxy<Int, () -> Int> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_tuz", collector =
        collector, freeze = freeze)

    public val _uz: KMockContract.SyncFunProxy<L, () -> L> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_uz", collector = collector,
            freeze = freeze)

    public val _tzz: KMockContract.SyncFunProxy<T, () -> T> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_tzz", collector =
        collector, freeze = freeze)

    public val _lol: KMockContract.SyncFunProxy<Any?, (Any?) -> Any?> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_lol", collector =
        collector, freeze = freeze)

    public val _fol: KMockContract.SyncFunProxy<Any, () -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_fol", collector =
        collector, freeze = freeze)

    public val _veryLongMethodNameWithABunchOfVariables: KMockContract.SyncFunProxy<Unit, (
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
    ) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.access.SyncFunMock#_veryLongMethodNameWithABunchOfVariables",
            collector = collector, freeze = freeze)

    private val referenceStore: Map<String, KMockContract.Proxy<*, *>> = mapOf(
        "foo|(kotlin.Int, kotlin.Any) -> kotlin.Any|[]" to _foo,
        "bar|(kotlin.Int, kotlin.Any) -> kotlin.Any|[]" to _bar,
        "ozz|(kotlin.IntArray) -> kotlin.Any|[]" to _ozz,
        "izz|(kotlin.Array<kotlin.Any>) -> kotlin.Any|[]" to _izz,
        "uzz|() -> kotlin.Unit|[]" to _uzz,
        "tuz|() -> kotlin.Int|[]" to _tuz,
        "uz|() -> L|[]" to _uz,
        "tzz|() -> T|[]" to _tzz,
        "veryLongMethodNameWithABunchOfVariables|(  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,) -> kotlin.Unit|[]"
            to _veryLongMethodNameWithABunchOfVariables,
        "lol|(kotlin.Any?) -> kotlin.Any?|[[kotlin.Any?]]" to _lol,
        "fol|() -> kotlin.Any|[[kotlin.Any]]" to _fol,
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

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Any> fol(): T = _fol.invoke() as T

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

    public fun _clearMock() {
        _foo.clear()
        _bar.clear()
        _ozz.clear()
        _izz.clear()
        _uzz.clear()
        _tuz.clear()
        _uz.clear()
        _tzz.clear()
        _lol.clear()
        _fol.clear()
        _veryLongMethodNameWithABunchOfVariables.clear()
    }

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf0")
    public fun syncFunProxyOf(reference: (Int, Any) -> Any): KMockContract.FunProxy<Any, (Int,
        Any) -> Any> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Int, kotlin.Any) -> kotlin.Any|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Int, kotlin.Any) -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (kotlin.Int, kotlin.Any) ->
        kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf1")
    public fun syncFunProxyOf(reference: (Array<out Int>) -> Any):
        KMockContract.FunProxy<Any, (IntArray) -> Any> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.IntArray) -> kotlin.Any|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.Int>) -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (kotlin.IntArray) -> kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf2")
    public fun syncFunProxyOf(reference: (Array<out Any>) -> Any):
        KMockContract.FunProxy<Any, (Array<Any>) -> Any> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<kotlin.Any>) -> kotlin.Any|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out kotlin.Any>) -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (kotlin.Array<kotlin.Any>) ->
        kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf3")
    public fun syncFunProxyOf(reference: () -> Unit): KMockContract.FunProxy<Unit, () -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, () -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf4")
    public fun syncFunProxyOf(reference: () -> Int): KMockContract.FunProxy<Int, () -> Int> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Int|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> kotlin.Int!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Int, () -> kotlin.Int>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf5")
    public fun syncFunProxyOf(reference: () -> L): KMockContract.FunProxy<L, () -> L> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> L|[]"""] ?: throw
        IllegalStateException("""Unknown method ${reference.name} with signature () -> L!""")) as
            tech.antibytes.kmock.KMockContract.FunProxy<L, () -> L>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf6")
    public fun syncFunProxyOf(reference: () -> T): KMockContract.FunProxy<T, () -> T> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> T|[]"""] ?: throw
        IllegalStateException("""Unknown method ${reference.name} with signature () -> T!""")) as
            tech.antibytes.kmock.KMockContract.FunProxy<T, () -> T>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf7")
    public fun syncFunProxyOf(reference: (
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
    ) -> Unit): KMockContract.FunProxy<Unit, (
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
    ) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
        ) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf8")
    public fun <T> syncFunProxyOf(reference: (T) -> T, hint: Hint1<Any>):
        KMockContract.FunProxy<Any?, (Any?) -> Any?> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any?) -> kotlin.Any?|[[kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (T) -> T!""")) as
            tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any?, (kotlin.Any?) -> kotlin.Any?>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf9")
    public fun <T : Any> syncFunProxyOf(reference: () -> T, hint: Hint0):
        KMockContract.FunProxy<Any, () -> Any> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|() -> kotlin.Any|[[kotlin.Any]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature () -> T!""")) as
            tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, () -> kotlin.Any>
}
