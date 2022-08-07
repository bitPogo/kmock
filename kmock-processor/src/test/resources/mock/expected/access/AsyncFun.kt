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

internal class AsyncFunMock<L, T>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: AsyncFun<L, T>? = null,
    freeze: Boolean = false,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : AsyncFun<L, T> where T : CharSequence, T : Comparable<T> {
    public val _foo: KMockContract.AsyncFunProxy<Any, suspend (Int, Any) -> Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.AsyncFunMock#_foo", collector =
        collector, freeze = freeze)

    public val _bar: KMockContract.AsyncFunProxy<Any, suspend (Int, Any) -> Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.AsyncFunMock#_bar", collector =
        collector, freeze = freeze)

    public val _ozz: KMockContract.AsyncFunProxy<Any, suspend (IntArray) -> Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.AsyncFunMock#_ozz", collector =
        collector, freeze = freeze)

    public val _izz: KMockContract.AsyncFunProxy<Any, suspend (Array<out L>) -> Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.AsyncFunMock#_izz", collector =
        collector, freeze = freeze)

    public val _uzz: KMockContract.AsyncFunProxy<Unit, suspend () -> Unit> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.AsyncFunMock#_uzz", collector =
        collector, freeze = freeze)

    public val _tuz: KMockContract.AsyncFunProxy<Int, suspend () -> Int> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.AsyncFunMock#_tuz", collector =
        collector, freeze = freeze)

    public val _uz: KMockContract.AsyncFunProxy<L, suspend () -> L> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.AsyncFunMock#_uz", collector =
        collector, freeze = freeze)

    public val _tzz: KMockContract.AsyncFunProxy<T, suspend () -> T> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.AsyncFunMock#_tzz", collector =
        collector, freeze = freeze)

    public val _lol: KMockContract.AsyncFunProxy<Any?, suspend (Any?) -> Any?> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.AsyncFunMock#_lol", collector =
        collector, freeze = freeze)

    public val _fol: KMockContract.AsyncFunProxy<Any, suspend () -> Any> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.AsyncFunMock#_fol", collector =
        collector, freeze = freeze)

    public val _veryLongMethodNameWithABunchOfVariables: KMockContract.AsyncFunProxy<Unit, suspend (
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        L,
        Int,
        T,
    ) -> Unit> =
        ProxyFactory.createAsyncFunProxy("mock.template.access.AsyncFunMock#_veryLongMethodNameWithABunchOfVariables",
            collector = collector, freeze = freeze)

    private val referenceStore: Map<String, KMockContract.Proxy<*, *>> = mapOf(
        "foo|suspend (kotlin.Int, kotlin.Any) -> kotlin.Any|[]" to _foo,
        "bar|suspend (kotlin.Int, kotlin.Any) -> kotlin.Any|[]" to _bar,
        "ozz|suspend (kotlin.IntArray) -> kotlin.Any|[]" to _ozz,
        "izz|suspend (kotlin.Array<L>) -> kotlin.Any|[]" to _izz,
        "uzz|suspend () -> kotlin.Unit|[]" to _uzz,
        "tuz|suspend () -> kotlin.Int|[]" to _tuz,
        "uz|suspend () -> L|[]" to _uz,
        "tzz|suspend () -> T|[]" to _tzz,
        "veryLongMethodNameWithABunchOfVariables|suspend (  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  L,  kotlin.Int,  T,) -> kotlin.Unit|[]"
            to _veryLongMethodNameWithABunchOfVariables,
        "lol|suspend (kotlin.Any?) -> kotlin.Any?|[[kotlin.Any?]]" to _lol,
        "fol|suspend () -> kotlin.Any|[[kotlin.Any]]" to _fol,
    )

    public override suspend fun foo(fuzz: Int, ozz: Any): Any = _foo.invoke(fuzz, ozz)

    public override suspend fun bar(buzz: Int, bozz: Any): Any = _bar.invoke(buzz, bozz)

    public override suspend fun ozz(vararg buzz: Int): Any = _ozz.invoke(buzz)

    public override suspend fun izz(vararg buzz: L): Any = _izz.invoke(buzz)

    public override suspend fun uzz(): Unit = _uzz.invoke() {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override suspend fun tuz(): Int = _tuz.invoke()

    public override suspend fun uz(): L = _uz.invoke()

    public override suspend fun tzz(): T = _tzz.invoke()

    @Suppress("UNCHECKED_CAST")
    public override suspend fun <T> lol(arg: T): T = _lol.invoke(arg) as T

    @Suppress("UNCHECKED_CAST")
    public override suspend fun <T : Any> fol(): T = _fol.invoke() as T

    public override suspend fun veryLongMethodNameWithABunchOfVariables(
        arg0: Int,
        arg1: Int,
        arg2: Int,
        arg3: Int,
        arg4: Int,
        arg5: Int,
        arg6: Int,
        arg7: L,
        arg8: Int,
        arg9: T,
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
        _fol.clear()
        _veryLongMethodNameWithABunchOfVariables.clear()
    }

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf0")
    public fun asyncFunProxyOf(reference: suspend (Int, Any) -> Any):
        KMockContract.FunProxy<Any, suspend (Int, Any) -> Any> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.Int, kotlin.Any) -> kotlin.Any|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (kotlin.Int, kotlin.Any) -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, suspend (kotlin.Int, kotlin.Any) ->
        kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf1")
    public fun asyncFunProxyOf(reference: suspend (Array<out Int>) -> Any):
        KMockContract.FunProxy<Any, suspend (IntArray) -> Any> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.IntArray) -> kotlin.Any|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (kotlin.Array<out kotlin.Int>) -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, suspend (kotlin.IntArray) ->
        kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf2")
    public fun asyncFunProxyOf(reference: suspend (Array<out L>) -> Any):
        KMockContract.FunProxy<Any, suspend (Array<L>) -> Any> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.Array<L>) -> kotlin.Any|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (kotlin.Array<out L>) -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, suspend (kotlin.Array<L>) ->
        kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf3")
    public fun asyncFunProxyOf(reference: suspend () -> Unit):
        KMockContract.FunProxy<Unit, suspend () -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend () -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend () -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, suspend () -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf4")
    public fun asyncFunProxyOf(reference: suspend () -> Int):
        KMockContract.FunProxy<Int, suspend () -> Int> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend () -> kotlin.Int|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend () -> kotlin.Int!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Int, suspend () -> kotlin.Int>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf5")
    public fun asyncFunProxyOf(reference: suspend () -> L): KMockContract.FunProxy<L, suspend () -> L>
        = (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend () -> L|[]"""]
        ?: throw
        IllegalStateException("""Unknown method ${reference.name} with signature suspend () -> L!"""))
        as tech.antibytes.kmock.KMockContract.FunProxy<L, suspend () -> L>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf6")
    public fun asyncFunProxyOf(reference: suspend () -> T): KMockContract.FunProxy<T, suspend () -> T>
        = (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend () -> T|[]"""]
        ?: throw
        IllegalStateException("""Unknown method ${reference.name} with signature suspend () -> T!"""))
        as tech.antibytes.kmock.KMockContract.FunProxy<T, suspend () -> T>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf7")
    public fun asyncFunProxyOf(reference: suspend (
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        L,
        Int,
        T,
    ) -> Unit): KMockContract.FunProxy<Unit, suspend (
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        L,
        Int,
        T,
    ) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  L,  kotlin.Int,  T,) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  kotlin.Int,  L,  kotlin.Int,  T,) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, suspend (
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
            kotlin.Int,
            L,
            kotlin.Int,
            T,
        ) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf8")
    public fun <T> asyncFunProxyOf(reference: suspend (T) -> T, hint: Hint1<Any>):
        KMockContract.FunProxy<Any?, suspend (Any?) -> Any?> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend (kotlin.Any?) -> kotlin.Any?|[[kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend (T) -> T!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any?, suspend (kotlin.Any?) ->
        kotlin.Any?>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("asyncFunProxyOf9")
    public fun <T : Any> asyncFunProxyOf(reference: suspend () -> T, hint: Hint0):
        KMockContract.FunProxy<Any, suspend () -> Any> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|suspend () -> kotlin.Any|[[kotlin.Any]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature suspend () -> T!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, suspend () -> kotlin.Any>
}
