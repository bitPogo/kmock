package mock.template.typealiaz

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Char
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Function1
import kotlin.Int
import kotlin.IntArray
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.Map
import kotlin.reflect.KProperty
import tech.antibytes.kmock.Hint1
import tech.antibytes.kmock.Hint2
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.SafeJvmName
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class AccessMock<L : Alias623>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Access<L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Access<L> {
    public override val prop: Alias673<String>
        get() = _prop.executeOnGet()

    public val _prop: KMockContract.PropertyProxy<Alias673<String>> =
        ProxyFactory.createPropertyProxy("mock.template.typealiaz.AccessMock#_prop", collector =
        collector, freeze = freeze)

    public val _doSomething: KMockContract.SyncFunProxy<Any, (
        Alias677<Any>,
        Alias623,
        Alias621,
    ) -> Any> = ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_doSomething",
        collector = collector, freeze = freeze)

    public val _doAnythingElseWithAlias621Alias623: KMockContract.SyncFunProxy<Alias623, (Alias621,
        Alias623) -> Alias623> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_doAnythingElseWithAlias621Alias623",
            collector = collector, freeze = freeze)

    public val _doAnythingElseWithAlias677:
        KMockContract.SyncFunProxy<Unit, (Alias677<Alias677<Alias621>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_doAnythingElseWithAlias677",
            collector = collector, freeze = freeze)

    public val _doElse: KMockContract.SyncFunProxy<Unit, (Function1<Any, Unit>,
        Function1<Any, Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_doElse", collector =
        collector, freeze = freeze)

    public val _doOtherThing: KMockContract.SyncFunProxy<Unit, (Alias677<Alias677<Alias621>>,
        Comparable<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_doOtherThing", collector
        = collector, freeze = freeze)

    public val _doSomethingElseWithTAlias621LAlias623: KMockContract.SyncFunProxy<Unit, (Alias621,
        Alias623) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_doSomethingElseWithTAlias621LAlias623",
            collector = collector, freeze = freeze)

    public val _doSomethingElseWithTAlias677:
        KMockContract.SyncFunProxy<Alias677<Any?>, (Alias677<Any?>) -> Alias677<Any?>> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_doSomethingElseWithTAlias677",
            collector = collector, freeze = freeze)

    public val _doMoreElse: KMockContract.SyncFunProxy<Unit, (Function1<Any, Unit>,
        Function1<Any, Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_doMoreElse", collector =
        collector, freeze = freeze)

    public val _fooWithTAlias673: KMockContract.SyncFunProxy<Unit, (Alias673<Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_fooWithTAlias673",
            collector = collector, freeze = freeze)

    public val _fooWithAnyAlias673: KMockContract.SyncFunProxy<Alias673<String>, (Any,
        Alias673<String>) -> Alias673<String>> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_fooWithAnyAlias673",
            collector = collector, freeze = freeze)

    public val _fooWithCharAlias673s: KMockContract.SyncFunProxy<Unit, (Char,
        Array<out Alias673<IntArray>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_fooWithCharAlias673s",
            collector = collector, freeze = freeze)

    public val _fooWithIntAlias673s: KMockContract.SyncFunProxy<Unit, (Int,
        Array<out Alias673<out String>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_fooWithIntAlias673s",
            collector = collector, freeze = freeze)

    public val _fooWithLongTAlias673s: KMockContract.SyncFunProxy<Unit, (Long,
        Array<out Alias673<out Alias677<Alias673<Int>>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_fooWithLongTAlias673s",
            collector = collector, freeze = freeze)

    public val _barWithAlias699: KMockContract.SyncFunProxy<Unit, (Alias699<String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_barWithAlias699",
            collector = collector, freeze = freeze)

    public val _barWithTAlias699s:
        KMockContract.SyncFunProxy<Alias699<String>, (Array<out Alias699<String>>) -> Alias699<String>>
        = ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_barWithTAlias699s",
        collector = collector, freeze = freeze)

    public val _barWithLongTAlias699s:
        KMockContract.SyncFunProxy<Alias699<out Alias677<Alias673<Int>>>, (Long,
            Array<out Alias699<out Alias677<Alias673<Int>>>>) -> Alias699<out Alias677<Alias673<Int>>>> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_barWithLongTAlias699s",
            collector = collector, freeze = freeze)

    public val _run: KMockContract.SyncFunProxy<Alias621, (L) -> Alias621> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_run", collector =
        collector, freeze = freeze)

    public val _rol: KMockContract.SyncFunProxy<Unit, (Alias700<Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_rol", collector =
        collector, freeze = freeze)

    public val _toll: KMockContract.SyncFunProxy<Unit, (Alias677<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.AccessMock#_toll", collector =
        collector, freeze = freeze)

    private val referenceStore: Map<String, KMockContract.Proxy<*, *>> = mapOf(
        "prop|property" to _prop,
        "doSomething|(  mock.template.typealiaz.Generics<kotlin.Any>,  kotlin.Function1<kotlin.Any, kotlin.Any>,  kotlin.Function1<kotlin.Any, kotlin.Unit>,) -> kotlin.Any|[]"
            to _doSomething,
        "doElse|(kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Unit|[]"
            to _doElse,
        "run|(L) -> kotlin.Function1<kotlin.Any, kotlin.Unit>|[]" to _run,
        "doAnythingElse|(kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Function1<kotlin.Any, kotlin.Any>|[]"
            to _doAnythingElseWithAlias621Alias623,
        "doAnythingElse|(mock.template.typealiaz.Generics<mock.template.typealiaz.Generics<kotlin.Function1<kotlin.Any, kotlin.Unit>>>) -> kotlin.Unit|[]"
            to _doAnythingElseWithAlias677,
        "doOtherThing|(mock.template.typealiaz.Generics<mock.template.typealiaz.Generics<kotlin.Function1<kotlin.Any, kotlin.Unit>>>, kotlin.Comparable<kotlin.Any>) -> kotlin.Unit|[[mock.template.typealiaz.Generics<kotlin.Function1<kotlin.Any, kotlin.Unit>>], [kotlin.Comparable<X>]]"
            to _doOtherThing,
        "doSomethingElse|(kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Unit|[[kotlin.Function1<kotlin.Any, kotlin.Unit>], [kotlin.Function1<kotlin.Any, kotlin.Any>]]"
            to _doSomethingElseWithTAlias621LAlias623,
        "doMoreElse|(kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Unit|[[kotlin.Function1<kotlin.Any, kotlin.Unit>], [kotlin.Function1<kotlin.Any, kotlin.Any>]]"
            to _doMoreElse,
        "doSomethingElse|(mock.template.typealiaz.Generics<kotlin.Any?>) -> mock.template.typealiaz.Generics<kotlin.Any?>|[[mock.template.typealiaz.Generics<K>], [kotlin.Any?]]"
            to _doSomethingElseWithTAlias677,
        "foo|(kotlin.collections.Map<kotlin.String, kotlin.Any?>) -> kotlin.Unit|[[kotlin.collections.Map<kotlin.String, K>], [kotlin.Any?]]"
            to _fooWithTAlias673,
        "foo|(kotlin.Any, kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.collections.Map<kotlin.String, kotlin.String>|[]"
            to _fooWithAnyAlias673,
        "foo|(kotlin.Char, kotlin.Array<out kotlin.collections.Map<kotlin.String, kotlin.IntArray>>) -> kotlin.Unit|[]"
            to _fooWithCharAlias673s,
        "foo|(kotlin.Int, kotlin.Array<out kotlin.collections.Map<kotlin.String, out kotlin.String>>) -> kotlin.Unit|[]"
            to _fooWithIntAlias673s,
        "foo|(kotlin.Long, kotlin.Array<out kotlin.collections.Map<kotlin.String, out mock.template.typealiaz.Generics<kotlin.collections.Map<kotlin.String, kotlin.Int>>>>) -> kotlin.Unit|[[kotlin.collections.Map<kotlin.String, out mock.template.typealiaz.Generics<kotlin.collections.Map<kotlin.String, kotlin.Int>>>]]"
            to _fooWithLongTAlias673s,
        "bar|(kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.Unit|[]" to
            _barWithAlias699,
        "bar|(kotlin.Array<out kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.collections.Map<kotlin.String, kotlin.String>|[[kotlin.collections.Map<kotlin.String, kotlin.String>]]"
            to _barWithTAlias699s,
        "bar|(kotlin.Long, kotlin.Array<out kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.collections.Map<kotlin.String, kotlin.String>|[[kotlin.collections.Map<kotlin.String, kotlin.String>]]"
            to _barWithLongTAlias699s,
        "rol|(kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.Unit|[[kotlin.collections.Map<kotlin.String, kotlin.String>], [kotlin.Any?]]"
            to _rol,
        "toll|(mock.template.typealiaz.Generics<kotlin.Any>) -> kotlin.Unit|[[mock.template.typealiaz.Generics<K>], [kotlin.CharSequence & kotlin.Comparable<K>]]"
            to _toll,
    )

    public override fun doSomething(
        arg0: Alias677<Any>,
        arg1: Alias623,
        arg2: Alias621,
    ): Any = _doSomething.invoke(arg0, arg1, arg2)

    public override fun doAnythingElse(arg1: Alias621, arg2: Alias623): Alias623 =
        _doAnythingElseWithAlias621Alias623.invoke(arg1, arg2)

    public override fun doAnythingElse(arg1: Alias677<Alias677<Alias621>>): Unit =
        _doAnythingElseWithAlias677.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun doElse(arg1: Function1<Any, Unit>, arg2: Function1<Any, Any>): Unit =
        _doElse.invoke(arg1, arg2) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias677<Alias621>, X : Comparable<X>> doOtherThing(arg1: Alias677<T>,
        arg0: X): Unit = _doOtherThing.invoke(arg1, arg0) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias621, L : Alias623> doSomethingElse(arg1: T, arg2: L): Unit =
        _doSomethingElseWithTAlias621LAlias623.invoke(arg1, arg2) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Alias677<K>, K> doSomethingElse(arg1: T): T =
        _doSomethingElseWithTAlias677.invoke(arg1) as T

    public override fun <T : Function1<Any, Unit>, L : Function1<Any, Any>> doMoreElse(arg1: T,
        arg2: L): Unit = _doMoreElse.invoke(arg1, arg2) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias673<K>, K> foo(arg1: T): Unit = _fooWithTAlias673.invoke(arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun foo(arg0: Any, arg1: Alias673<String>): Alias673<String> =
        _fooWithAnyAlias673.invoke(arg0, arg1)

    public override fun foo(arg0: Char, vararg arg1: Alias673<IntArray>): Unit =
        _fooWithCharAlias673s.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun foo(arg0: Int, vararg arg1: Alias673<out String>): Unit =
        _fooWithIntAlias673s.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias673<out Alias677<Alias673<Int>>>> foo(arg0: Long, vararg arg1: T):
        Unit = _fooWithLongTAlias673s.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun bar(arg1: Alias699<String>): Unit = _barWithAlias699.invoke(arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Alias699<String>> bar(vararg arg1: T): T =
        _barWithTAlias699s.invoke(arg1) as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Alias699<out Alias677<Alias673<Int>>>> bar(arg0: Long, vararg arg1: T): T
        = _barWithLongTAlias699s.invoke(arg0, arg1) as T

    public override fun run(arg: L): Alias621 = _run.invoke(arg)

    public override fun <T : Alias700<K>, K> rol(arg: T): Unit = _rol.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias677<K>, K> toll(arg: T): Unit where K : CharSequence, K :
    Comparable<K> = _toll.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _prop.clear()
        _doSomething.clear()
        _doAnythingElseWithAlias621Alias623.clear()
        _doAnythingElseWithAlias677.clear()
        _doElse.clear()
        _doOtherThing.clear()
        _doSomethingElseWithTAlias621LAlias623.clear()
        _doSomethingElseWithTAlias677.clear()
        _doMoreElse.clear()
        _fooWithTAlias673.clear()
        _fooWithAnyAlias673.clear()
        _fooWithCharAlias673s.clear()
        _fooWithIntAlias673s.clear()
        _fooWithLongTAlias673s.clear()
        _barWithAlias699.clear()
        _barWithTAlias699s.clear()
        _barWithLongTAlias699s.clear()
        _run.clear()
        _rol.clear()
        _toll.clear()
    }

    @Suppress("UNCHECKED_CAST")
    @KMockExperimental
    public fun <Property> propertyProxyOf(reference: KProperty<Property>):
        KMockContract.PropertyProxy<Property> = (referenceStore["""${reference.name}|property"""] ?:
    throw IllegalStateException("""Unknown property ${reference.name}!""")) as
        tech.antibytes.kmock.KMockContract.PropertyProxy<Property>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf0")
    public fun syncFunProxyOf(reference: (
        Generics<Any>,
        Function1<Any, Any>,
        Function1<Any, Unit>,
    ) -> Any): KMockContract.FunProxy<Any, (
        Generics<Any>,
        Function1<Any, Any>,
        Function1<Any, Unit>,
    ) -> Any> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(  mock.template.typealiaz.Generics<kotlin.Any>,  kotlin.Function1<kotlin.Any, kotlin.Any>,  kotlin.Function1<kotlin.Any, kotlin.Unit>,) -> kotlin.Any|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (  mock.template.typealiaz.Generics<kotlin.Any>,  kotlin.Function1<kotlin.Any, kotlin.Any>,  kotlin.Function1<kotlin.Any, kotlin.Unit>,) -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (
            mock.template.typealiaz.Generics<kotlin.Any>,
            kotlin.Function1<kotlin.Any, kotlin.Any>,
            kotlin.Function1<kotlin.Any, kotlin.Unit>,
        ) -> kotlin.Any>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf1")
    public fun syncFunProxyOf(reference: (Function1<Any, Unit>, Function1<Any, Any>) -> Unit):
        KMockContract.FunProxy<Unit, (Function1<Any, Unit>, Function1<Any, Any>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Function1<kotlin.Any,
            kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf2")
    public fun syncFunProxyOf(reference: (L) -> Function1<Any, Unit>):
        KMockContract.FunProxy<Function1<Any, Unit>, (L) -> Function1<Any, Unit>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(L) -> kotlin.Function1<kotlin.Any, kotlin.Unit>|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (L) -> kotlin.Function1<kotlin.Any, kotlin.Unit>!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Function1<kotlin.Any, kotlin.Unit>,
                (L) -> kotlin.Function1<kotlin.Any, kotlin.Unit>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf3")
    public fun syncFunProxyOf(reference: (Function1<Any, Unit>,
        Function1<Any, Any>) -> Function1<Any, Any>,
        hint: Hint2<Function1<Any, Unit>, Function1<Any, Any>>):
        KMockContract.FunProxy<Function1<Any, Any>, (Function1<Any, Unit>,
            Function1<Any, Any>) -> Function1<Any, Any>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Function1<kotlin.Any, kotlin.Any>|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Function1<kotlin.Any, kotlin.Any>!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Function1<kotlin.Any, kotlin.Any>,
                (kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) ->
        kotlin.Function1<kotlin.Any, kotlin.Any>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf4")
    public fun syncFunProxyOf(reference: (Generics<Generics<Function1<Any, Unit>>>) -> Unit,
        hint: Hint1<Generics<Generics<Function1<Any, Unit>>>>):
        KMockContract.FunProxy<Unit, (Generics<Generics<Function1<Any, Unit>>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Generics<mock.template.typealiaz.Generics<kotlin.Function1<kotlin.Any, kotlin.Unit>>>) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (mock.template.typealiaz.Generics<mock.template.typealiaz.Generics<kotlin.Function1<kotlin.Any, kotlin.Unit>>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (mock.template.typealiaz.Generics<mock.template.typealiaz.Generics<kotlin.Function1<kotlin.Any,
            kotlin.Unit>>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf5")
    public fun <T : Generics<Function1<Any, Unit>>, X : Comparable<X>>
        syncFunProxyOf(reference: (Generics<T>, X) -> Unit, hint: Hint2<Generics<T>, X>):
        KMockContract.FunProxy<Unit, (Generics<Generics<Function1<Any, Unit>>>,
            Comparable<Any>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Generics<mock.template.typealiaz.Generics<kotlin.Function1<kotlin.Any, kotlin.Unit>>>, kotlin.Comparable<kotlin.Any>) -> kotlin.Unit|[[mock.template.typealiaz.Generics<kotlin.Function1<kotlin.Any, kotlin.Unit>>], [kotlin.Comparable<X>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (mock.template.typealiaz.Generics<T>, X) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (mock.template.typealiaz.Generics<mock.template.typealiaz.Generics<kotlin.Function1<kotlin.Any,
            kotlin.Unit>>>, kotlin.Comparable<kotlin.Any>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf6")
    public fun <T : Function1<Any, Unit>, L : Function1<Any, Any>> syncFunProxyOf(reference: (T,
        L) -> Unit, hint: Hint2<T, L>): KMockContract.FunProxy<Unit, (Function1<Any, Unit>,
        Function1<Any, Any>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Unit|[[kotlin.Function1<kotlin.Any, kotlin.Unit>], [kotlin.Function1<kotlin.Any, kotlin.Any>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (T, L) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Function1<kotlin.Any,
            kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf7")
    public fun <T : Generics<K>, K> syncFunProxyOf(reference: (T) -> T, hint: Hint1<T>):
        KMockContract.FunProxy<Generics<Any?>, (Generics<Any?>) -> Generics<Any?>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Generics<kotlin.Any?>) -> mock.template.typealiaz.Generics<kotlin.Any?>|[[mock.template.typealiaz.Generics<K>], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (T) -> T!""")) as
            tech.antibytes.kmock.KMockContract.FunProxy<mock.template.typealiaz.Generics<kotlin.Any?>,
                    (mock.template.typealiaz.Generics<kotlin.Any?>) ->
            mock.template.typealiaz.Generics<kotlin.Any?>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf8")
    public fun <T : Map<String, K>, K> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (Map<String, Any?>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.collections.Map<kotlin.String, kotlin.Any?>) -> kotlin.Unit|[[kotlin.collections.Map<kotlin.String, K>], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (T) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (kotlin.collections.Map<kotlin.String, kotlin.Any?>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf9")
    public fun syncFunProxyOf(reference: (Any, Map<String, String>) -> Map<String, String>,
        hint: Hint2<Any, Map<String, String>>): KMockContract.FunProxy<Map<String, String>, (Any,
        Map<String, String>) -> Map<String, String>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any, kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.collections.Map<kotlin.String, kotlin.String>|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any, kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.collections.Map<kotlin.String, kotlin.String>!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.collections.Map<kotlin.String,
            kotlin.String>, (kotlin.Any, kotlin.collections.Map<kotlin.String, kotlin.String>) ->
        kotlin.collections.Map<kotlin.String, kotlin.String>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf10")
    public fun syncFunProxyOf(reference: (Char, Array<out Map<String, IntArray>>) -> Unit,
        hint: Hint2<Char, Array<out Map<String, IntArray>>>): KMockContract.FunProxy<Unit, (Char,
        Array<out Map<String, IntArray>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Char, kotlin.Array<out kotlin.collections.Map<kotlin.String, kotlin.IntArray>>) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Char, kotlin.Array<out kotlin.collections.Map<kotlin.String, kotlin.IntArray>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Char, kotlin.Array<out
        kotlin.collections.Map<kotlin.String, kotlin.IntArray>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf11")
    public fun syncFunProxyOf(reference: (Int, Array<out Map<String, out String>>) -> Unit,
        hint: Hint2<Int, Array<out Map<String, out String>>>): KMockContract.FunProxy<Unit, (Int,
        Array<out Map<String, out String>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Int, kotlin.Array<out kotlin.collections.Map<kotlin.String, out kotlin.String>>) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Int, kotlin.Array<out kotlin.collections.Map<kotlin.String, out kotlin.String>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Int, kotlin.Array<out
        kotlin.collections.Map<kotlin.String, out kotlin.String>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf12")
    public fun <T : Map<String, out Generics<Map<String, Int>>>> syncFunProxyOf(reference: (Long,
        Array<out T>) -> Unit, hint: Hint2<Long, Array<out T>>): KMockContract.FunProxy<Unit, (Long,
        Array<out Map<String, out Generics<Map<String, Int>>>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Long, kotlin.Array<out kotlin.collections.Map<kotlin.String, out mock.template.typealiaz.Generics<kotlin.collections.Map<kotlin.String, kotlin.Int>>>>) -> kotlin.Unit|[[kotlin.collections.Map<kotlin.String, out mock.template.typealiaz.Generics<kotlin.collections.Map<kotlin.String, kotlin.Int>>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Long, kotlin.Array<out T>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Long, kotlin.Array<out
        kotlin.collections.Map<kotlin.String, out
        mock.template.typealiaz.Generics<kotlin.collections.Map<kotlin.String, kotlin.Int>>>>) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf13")
    public fun syncFunProxyOf(reference: (Map<String, String>) -> Unit,
        hint: Hint1<Map<String, String>>): KMockContract.FunProxy<Unit, (Map<String, String>) -> Unit>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf14")
    public fun <T : Map<String, String>> syncFunProxyOf(reference: (Array<out T>) -> T,
        hint: Hint1<Array<out T>>):
        KMockContract.FunProxy<Map<String, String>, (Array<out Map<String, String>>) -> Map<String, String>>
        =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<out kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.collections.Map<kotlin.String, kotlin.String>|[[kotlin.collections.Map<kotlin.String, kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out T>) -> T!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.collections.Map<kotlin.String,
            kotlin.String>, (kotlin.Array<out kotlin.collections.Map<kotlin.String, kotlin.String>>) ->
        kotlin.collections.Map<kotlin.String, kotlin.String>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf15")
    public fun <T : Map<String, String>> syncFunProxyOf(reference: (Long, Array<out T>) -> T,
        hint: Hint2<Long, Array<out T>>): KMockContract.FunProxy<Map<String, String>, (Long,
        Array<out Map<String, String>>) -> Map<String, String>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Long, kotlin.Array<out kotlin.collections.Map<kotlin.String, kotlin.String>>) -> kotlin.collections.Map<kotlin.String, kotlin.String>|[[kotlin.collections.Map<kotlin.String, kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Long, kotlin.Array<out T>) -> T!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.collections.Map<kotlin.String,
            kotlin.String>, (kotlin.Long, kotlin.Array<out kotlin.collections.Map<kotlin.String,
            kotlin.String>>) -> kotlin.collections.Map<kotlin.String, kotlin.String>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf16")
    public fun <T : Map<String, String>, K> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (Map<String, String>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.Unit|[[kotlin.collections.Map<kotlin.String, kotlin.String>], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (T) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (kotlin.collections.Map<kotlin.String, kotlin.String>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf17")
    public fun <T : Generics<K>, K> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (Generics<Any>) -> Unit> where K : CharSequence, K :
    Comparable<K> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Generics<kotlin.Any>) -> kotlin.Unit|[[mock.template.typealiaz.Generics<K>], [kotlin.CharSequence & kotlin.Comparable<K>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (T) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (mock.template.typealiaz.Generics<kotlin.Any>) -> kotlin.Unit>
}
