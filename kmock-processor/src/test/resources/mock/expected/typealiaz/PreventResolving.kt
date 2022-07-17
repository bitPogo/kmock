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

internal class PreventResolvingMock<L : Alias923>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: PreventResolving<L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : PreventResolving<L> {
    public override val prop: Alias973<String>
        get() = _prop.executeOnGet()

    public val _prop: KMockContract.PropertyProxy<Alias973<String>> =
        ProxyFactory.createPropertyProxy("mock.template.typealiaz.PreventResolvingMock#_prop",
            collector = collector, freeze = freeze)

    public val _doSomething: KMockContract.SyncFunProxy<Any, (
        Alias977<Any>,
        Alias923,
        Alias921,
    ) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_doSomething",
            collector = collector, freeze = freeze)

    public val _doAnythingElseWithAlias921Alias923: KMockContract.SyncFunProxy<Alias923, (Alias921,
        Alias923) -> Alias923> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_doAnythingElseWithAlias921Alias923",
            collector = collector, freeze = freeze)

    public val _doAnythingElseWithAlias977:
        KMockContract.SyncFunProxy<Unit, (Alias977<Alias977<Alias921>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_doAnythingElseWithAlias977",
            collector = collector, freeze = freeze)

    public val _doElse: KMockContract.SyncFunProxy<Unit, (Function1<Any, Unit>,
        Function1<Any, Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_doElse",
            collector = collector, freeze = freeze)

    public val _doOtherThing: KMockContract.SyncFunProxy<Unit, (Alias977<Alias977<Alias921>>,
        Comparable<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_doOtherThing",
            collector = collector, freeze = freeze)

    public val _doSomethingElseWithTAlias921LAlias923: KMockContract.SyncFunProxy<Unit, (Alias921,
        Alias923) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_doSomethingElseWithTAlias921LAlias923",
            collector = collector, freeze = freeze)

    public val _doSomethingElseWithTAlias977:
        KMockContract.SyncFunProxy<Alias977<Any?>, (Alias977<Any?>) -> Alias977<Any?>> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_doSomethingElseWithTAlias977",
            collector = collector, freeze = freeze)

    public val _doMoreElse: KMockContract.SyncFunProxy<Unit, (Function1<Any, Unit>,
        Function1<Any, Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_doMoreElse",
            collector = collector, freeze = freeze)

    public val _fooWithTAlias973: KMockContract.SyncFunProxy<Unit, (Alias973<Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_fooWithTAlias973",
            collector = collector, freeze = freeze)

    public val _fooWithAnyAlias973: KMockContract.SyncFunProxy<Alias973<String>, (Any,
        Alias973<String>?) -> Alias973<String>> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_fooWithAnyAlias973",
            collector = collector, freeze = freeze)

    public val _fooWithCharAlias973s: KMockContract.SyncFunProxy<Unit, (Char,
        Array<out Alias973<IntArray>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_fooWithCharAlias973s",
            collector = collector, freeze = freeze)

    public val _fooWithIntAlias973s: KMockContract.SyncFunProxy<Unit, (Int,
        Array<out Alias973<out String>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_fooWithIntAlias973s",
            collector = collector, freeze = freeze)

    public val _fooWithLongTAlias973s: KMockContract.SyncFunProxy<Unit, (Long,
        Array<out Alias973<out Alias977<Alias973<Int>>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_fooWithLongTAlias973s",
            collector = collector, freeze = freeze)

    public val _barWithAlias999: KMockContract.SyncFunProxy<Unit, (Alias999<String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_barWithAlias999",
            collector = collector, freeze = freeze)

    public val _barWithTAlias999s:
        KMockContract.SyncFunProxy<Alias999<String>, (Array<out Alias999<String>>) -> Alias999<String>>
        =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_barWithTAlias999s",
            collector = collector, freeze = freeze)

    public val _barWithLongTAlias999s:
        KMockContract.SyncFunProxy<Alias999<out Alias977<Alias973<Int>>>, (Long,
            Array<out Alias999<out Alias977<Alias973<Int>>>>) -> Alias999<out Alias977<Alias973<Int>>>> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_barWithLongTAlias999s",
            collector = collector, freeze = freeze)

    public val _run: KMockContract.SyncFunProxy<Alias921, (L) -> Alias921> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_run", collector
        = collector, freeze = freeze)

    public val _rol: KMockContract.SyncFunProxy<Unit, (Alias1000<Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_rol", collector
        = collector, freeze = freeze)

    public val _toll: KMockContract.SyncFunProxy<Unit, (Alias977<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PreventResolvingMock#_toll",
            collector = collector, freeze = freeze)

    private val referenceStore: Map<String, KMockContract.Proxy<*, *>> = mapOf(
        "prop|property" to _prop,
        "doSomething|(  mock.template.typealiaz.Alias977<kotlin.Any>,  mock.template.typealiaz.Alias923,  mock.template.typealiaz.Alias921,) -> kotlin.Any|[]"
            to _doSomething,
        "doElse|(kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Unit|[]"
            to _doElse,
        "run|(L) -> mock.template.typealiaz.Alias921|[]" to _run,
        "doAnythingElse|(mock.template.typealiaz.Alias921, mock.template.typealiaz.Alias923) -> mock.template.typealiaz.Alias923|[]"
            to _doAnythingElseWithAlias921Alias923,
        "doAnythingElse|(mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias921>>) -> kotlin.Unit|[]"
            to _doAnythingElseWithAlias977,
        "doOtherThing|(mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias921>>, kotlin.Comparable<kotlin.Any>) -> kotlin.Unit|[[mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias921>], [kotlin.Comparable<X>]]"
            to _doOtherThing,
        "doSomethingElse|(mock.template.typealiaz.Alias921, mock.template.typealiaz.Alias923) -> kotlin.Unit|[[mock.template.typealiaz.Alias921], [mock.template.typealiaz.Alias923]]"
            to _doSomethingElseWithTAlias921LAlias923,
        "doSomethingElse|(mock.template.typealiaz.Alias977<kotlin.Any?>) -> mock.template.typealiaz.Alias977<kotlin.Any?>|[[mock.template.typealiaz.Alias977<X>], [kotlin.Any?]]"
            to _doSomethingElseWithTAlias977,
        "doMoreElse|(kotlin.Function1<kotlin.Any, kotlin.Unit>, kotlin.Function1<kotlin.Any, kotlin.Any>) -> kotlin.Unit|[[kotlin.Function1<kotlin.Any, kotlin.Unit>], [kotlin.Function1<kotlin.Any, kotlin.Any>]]"
            to _doMoreElse,
        "foo|(mock.template.typealiaz.Alias973<kotlin.Any?>) -> kotlin.Unit|[[mock.template.typealiaz.Alias973<X>], [kotlin.Any?]]"
            to _fooWithTAlias973,
        "foo|(kotlin.Any, mock.template.typealiaz.Alias973<kotlin.String>?) -> mock.template.typealiaz.Alias973<kotlin.String>|[]"
            to _fooWithAnyAlias973,
        "foo|(kotlin.Char, kotlin.Array<mock.template.typealiaz.Alias973<kotlin.IntArray>>) -> kotlin.Unit|[]"
            to _fooWithCharAlias973s,
        "foo|(kotlin.Int, kotlin.Array<mock.template.typealiaz.Alias973<kotlin.String>>) -> kotlin.Unit|[]"
            to _fooWithIntAlias973s,
        "foo|(kotlin.Long, kotlin.Array<mock.template.typealiaz.Alias973<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>>) -> kotlin.Unit|[[mock.template.typealiaz.Alias973<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>]]"
            to _fooWithLongTAlias973s,
        "bar|(mock.template.typealiaz.Alias999<kotlin.String>) -> kotlin.Unit|[]" to _barWithAlias999,
        "bar|(kotlin.Array<mock.template.typealiaz.Alias999<kotlin.String>>) -> mock.template.typealiaz.Alias999<kotlin.String>|[[mock.template.typealiaz.Alias999<kotlin.String>]]"
            to _barWithTAlias999s,
        "bar|(kotlin.Long, kotlin.Array<mock.template.typealiaz.Alias999<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>>) -> mock.template.typealiaz.Alias999<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>|[[mock.template.typealiaz.Alias999<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>]]"
            to _barWithLongTAlias999s,
        "rol|(mock.template.typealiaz.Alias1000<kotlin.Any?>) -> kotlin.Unit|[[mock.template.typealiaz.Alias1000<X>], [kotlin.Any?]]"
            to _rol,
        "toll|(mock.template.typealiaz.Alias977<kotlin.Any>) -> kotlin.Unit|[[mock.template.typealiaz.Alias977<X>], [kotlin.CharSequence & kotlin.Comparable<X>]]"
            to _toll,
    )

    public override fun doSomething(
        arg0: Alias977<Any>,
        arg1: Alias923,
        arg2: Alias921,
    ): Any = _doSomething.invoke(arg0, arg1, arg2)

    public override fun doAnythingElse(arg1: Alias921, arg2: Alias923): Alias923 =
        _doAnythingElseWithAlias921Alias923.invoke(arg1, arg2)

    public override fun doAnythingElse(arg1: Alias977<Alias977<Alias921>>): Unit =
        _doAnythingElseWithAlias977.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun doElse(arg1: Function1<Any, Unit>, arg2: Function1<Any, Any>): Unit =
        _doElse.invoke(arg1, arg2) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias977<Alias921>, X : Comparable<X>> doOtherThing(arg1: Alias977<T>,
        arg0: X): Unit = _doOtherThing.invoke(arg1, arg0) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias921, L : Alias923> doSomethingElse(arg1: T, arg2: L): Unit =
        _doSomethingElseWithTAlias921LAlias923.invoke(arg1, arg2) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Alias977<K>, K> doSomethingElse(arg1: T): T =
        _doSomethingElseWithTAlias977.invoke(arg1) as T

    public override fun <T : Function1<Any, Unit>, L : Function1<Any, Any>> doMoreElse(arg1: T,
        arg2: L): Unit = _doMoreElse.invoke(arg1, arg2) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias973<K>, K> foo(arg1: T): Unit = _fooWithTAlias973.invoke(arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun foo(arg0: Any, arg1: Alias973<String>?): Alias973<String> =
        _fooWithAnyAlias973.invoke(arg0, arg1)

    public override fun foo(arg0: Char, vararg arg1: Alias973<IntArray>): Unit =
        _fooWithCharAlias973s.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun foo(arg0: Int, vararg arg1: Alias973<out String>): Unit =
        _fooWithIntAlias973s.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias973<out Alias977<Alias973<Int>>>> foo(arg0: Long, vararg arg1: T):
        Unit = _fooWithLongTAlias973s.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun bar(arg1: Alias999<String>): Unit = _barWithAlias999.invoke(arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Alias999<String>> bar(vararg arg1: T): T =
        _barWithTAlias999s.invoke(arg1) as T

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Alias999<out Alias977<Alias973<Int>>>> bar(arg0: Long, vararg arg1: T): T
        = _barWithLongTAlias999s.invoke(arg0, arg1) as T

    public override fun run(arg: L): Alias921 = _run.invoke(arg)

    public override fun <T : Alias1000<K>, K> rol(arg: T): Unit = _rol.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias977<K>, K> toll(arg: T): Unit where K : CharSequence, K :
    Comparable<K> = _toll.invoke(arg) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _prop.clear()
        _doSomething.clear()
        _doAnythingElseWithAlias921Alias923.clear()
        _doAnythingElseWithAlias977.clear()
        _doElse.clear()
        _doOtherThing.clear()
        _doSomethingElseWithTAlias921LAlias923.clear()
        _doSomethingElseWithTAlias977.clear()
        _doMoreElse.clear()
        _fooWithTAlias973.clear()
        _fooWithAnyAlias973.clear()
        _fooWithCharAlias973s.clear()
        _fooWithIntAlias973s.clear()
        _fooWithLongTAlias973s.clear()
        _barWithAlias999.clear()
        _barWithTAlias999s.clear()
        _barWithLongTAlias999s.clear()
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
        Alias977<Any>,
        Alias923,
        Alias921,
    ) -> Any): KMockContract.FunProxy<Any, (
        Alias977<Any>,
        Alias923,
        Alias921,
    ) -> Any> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(  mock.template.typealiaz.Alias977<kotlin.Any>,  mock.template.typealiaz.Alias923,  mock.template.typealiaz.Alias921,) -> kotlin.Any|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (  mock.template.typealiaz.Alias977<kotlin.Any>,  mock.template.typealiaz.Alias923,  mock.template.typealiaz.Alias921,) -> kotlin.Any!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Any, (
            mock.template.typealiaz.Alias977<kotlin.Any>,
            mock.template.typealiaz.Alias923,
            mock.template.typealiaz.Alias921,
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
    public fun syncFunProxyOf(reference: (L) -> Alias921):
        KMockContract.FunProxy<Alias921, (L) -> Alias921> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(L) -> mock.template.typealiaz.Alias921|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (L) -> mock.template.typealiaz.Alias921!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<mock.template.typealiaz.Alias921, (L) ->
        mock.template.typealiaz.Alias921>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf3")
    public fun syncFunProxyOf(reference: (Alias921, Alias923) -> Alias923,
        hint: Hint2<Alias921, Alias923>): KMockContract.FunProxy<Alias923, (Alias921,
        Alias923) -> Alias923> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Alias921, mock.template.typealiaz.Alias923) -> mock.template.typealiaz.Alias923|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (mock.template.typealiaz.Alias921, mock.template.typealiaz.Alias923) -> mock.template.typealiaz.Alias923!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<mock.template.typealiaz.Alias923,
                (mock.template.typealiaz.Alias921, mock.template.typealiaz.Alias923) ->
        mock.template.typealiaz.Alias923>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf4")
    public fun syncFunProxyOf(reference: (Alias977<Alias977<Alias921>>) -> Unit,
        hint: Hint1<Alias977<Alias977<Alias921>>>):
        KMockContract.FunProxy<Unit, (Alias977<Alias977<Alias921>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias921>>) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias921>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias921>>) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf5")
    public fun <T : Alias977<Alias921>, X : Comparable<X>> syncFunProxyOf(reference: (Alias977<T>,
        X) -> Unit, hint: Hint2<Alias977<T>, X>):
        KMockContract.FunProxy<Unit, (Alias977<Alias977<Alias921>>, Comparable<Any>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias921>>, kotlin.Comparable<kotlin.Any>) -> kotlin.Unit|[[mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias921>], [kotlin.Comparable<X>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (mock.template.typealiaz.Alias977<T>, X) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias921>>,
            kotlin.Comparable<kotlin.Any>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf6")
    public fun <T : Alias921, L : Alias923> syncFunProxyOf(reference: (T, L) -> Unit,
        hint: Hint2<T, L>): KMockContract.FunProxy<Unit, (Alias921, Alias923) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Alias921, mock.template.typealiaz.Alias923) -> kotlin.Unit|[[mock.template.typealiaz.Alias921], [mock.template.typealiaz.Alias923]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (T, L) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (mock.template.typealiaz.Alias921,
            mock.template.typealiaz.Alias923) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf7")
    public fun <T : Alias977<K>, K> syncFunProxyOf(reference: (T) -> T, hint: Hint1<T>):
        KMockContract.FunProxy<Alias977<Any?>, (Alias977<Any?>) -> Alias977<Any?>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Alias977<kotlin.Any?>) -> mock.template.typealiaz.Alias977<kotlin.Any?>|[[mock.template.typealiaz.Alias977<X>], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (T) -> T!""")) as
            tech.antibytes.kmock.KMockContract.FunProxy<mock.template.typealiaz.Alias977<kotlin.Any?>,
                    (mock.template.typealiaz.Alias977<kotlin.Any?>) ->
            mock.template.typealiaz.Alias977<kotlin.Any?>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf8")
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
    @SafeJvmName("syncFunProxyOf9")
    public fun <T : Alias973<K>, K> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (Alias973<Any?>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Alias973<kotlin.Any?>) -> kotlin.Unit|[[mock.template.typealiaz.Alias973<X>], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (T) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (mock.template.typealiaz.Alias973<kotlin.Any?>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf10")
    public fun syncFunProxyOf(reference: (Any, Alias973<String>?) -> Alias973<String>,
        hint: Hint2<Any, Alias973<String>>): KMockContract.FunProxy<Alias973<String>, (Any,
        Alias973<String>?) -> Alias973<String>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Any, mock.template.typealiaz.Alias973<kotlin.String>?) -> mock.template.typealiaz.Alias973<kotlin.String>|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Any, mock.template.typealiaz.Alias973<kotlin.String>?) -> mock.template.typealiaz.Alias973<kotlin.String>!"""))
            as
            tech.antibytes.kmock.KMockContract.FunProxy<mock.template.typealiaz.Alias973<kotlin.String>,
                    (kotlin.Any, mock.template.typealiaz.Alias973<kotlin.String>?) ->
            mock.template.typealiaz.Alias973<kotlin.String>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf11")
    public fun syncFunProxyOf(reference: (Char, Array<out Alias973<IntArray>>) -> Unit,
        hint: Hint2<Char, Array<Alias973<IntArray>>>): KMockContract.FunProxy<Unit, (Char,
        Array<Alias973<IntArray>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Char, kotlin.Array<mock.template.typealiaz.Alias973<kotlin.IntArray>>) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Char, kotlin.Array<out mock.template.typealiaz.Alias973<kotlin.IntArray>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Char,
            kotlin.Array<mock.template.typealiaz.Alias973<kotlin.IntArray>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf12")
    public fun syncFunProxyOf(reference: (Int, Array<out Alias973<String>>) -> Unit,
        hint: Hint2<Int, Array<Alias973<String>>>): KMockContract.FunProxy<Unit, (Int,
        Array<Alias973<String>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Int, kotlin.Array<mock.template.typealiaz.Alias973<kotlin.String>>) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Int, kotlin.Array<out mock.template.typealiaz.Alias973<kotlin.String>>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Int,
            kotlin.Array<mock.template.typealiaz.Alias973<kotlin.String>>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf13")
    public fun <T : Alias973<Alias977<Alias973<Int>>>> syncFunProxyOf(reference: (Long,
        Array<out T>) -> Unit, hint: Hint2<Long, Array<T>>): KMockContract.FunProxy<Unit, (Long,
        Array<Alias973<Alias977<Alias973<Int>>>>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Long, kotlin.Array<mock.template.typealiaz.Alias973<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>>) -> kotlin.Unit|[[mock.template.typealiaz.Alias973<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Long, kotlin.Array<out T>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit, (kotlin.Long,
            kotlin.Array<mock.template.typealiaz.Alias973<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>>) ->
        kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf14")
    public fun syncFunProxyOf(reference: (Alias999<String>) -> Unit, hint: Hint1<Alias999<String>>):
        KMockContract.FunProxy<Unit, (Alias999<String>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Alias999<kotlin.String>) -> kotlin.Unit|[]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (mock.template.typealiaz.Alias999<kotlin.String>) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (mock.template.typealiaz.Alias999<kotlin.String>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf15")
    public fun <T : Alias999<String>> syncFunProxyOf(reference: (Array<out T>) -> T,
        hint: Hint1<Array<T>>):
        KMockContract.FunProxy<Alias999<String>, (Array<Alias999<String>>) -> Alias999<String>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Array<mock.template.typealiaz.Alias999<kotlin.String>>) -> mock.template.typealiaz.Alias999<kotlin.String>|[[mock.template.typealiaz.Alias999<kotlin.String>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Array<out T>) -> T!"""))
            as
            tech.antibytes.kmock.KMockContract.FunProxy<mock.template.typealiaz.Alias999<kotlin.String>,
                    (kotlin.Array<mock.template.typealiaz.Alias999<kotlin.String>>) ->
            mock.template.typealiaz.Alias999<kotlin.String>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf16")
    public fun <T : Alias999<Alias977<Alias973<Int>>>> syncFunProxyOf(reference: (Long,
        Array<out T>) -> T, hint: Hint2<Long, Array<T>>):
        KMockContract.FunProxy<Alias999<Alias977<Alias973<Int>>>, (Long,
            Array<Alias999<Alias977<Alias973<Int>>>>) -> Alias999<Alias977<Alias973<Int>>>> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(kotlin.Long, kotlin.Array<mock.template.typealiaz.Alias999<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>>) -> mock.template.typealiaz.Alias999<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>|[[mock.template.typealiaz.Alias999<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (kotlin.Long, kotlin.Array<out T>) -> T!"""))
            as
            tech.antibytes.kmock.KMockContract.FunProxy<mock.template.typealiaz.Alias999<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>,
                    (kotlin.Long,
                kotlin.Array<mock.template.typealiaz.Alias999<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>>) ->
            mock.template.typealiaz.Alias999<mock.template.typealiaz.Alias977<mock.template.typealiaz.Alias973<kotlin.Int>>>>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf17")
    public fun <T : Alias1000<K>, K> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (Alias1000<Any?>) -> Unit> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Alias1000<kotlin.Any?>) -> kotlin.Unit|[[mock.template.typealiaz.Alias1000<X>], [kotlin.Any?]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (T) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (mock.template.typealiaz.Alias1000<kotlin.Any?>) -> kotlin.Unit>

    @Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION", "UNCHECKED_CAST")
    @KMockExperimental
    @SafeJvmName("syncFunProxyOf18")
    public fun <T : Alias977<K>, K> syncFunProxyOf(reference: (T) -> Unit, hint: Hint1<T>):
        KMockContract.FunProxy<Unit, (Alias977<Any>) -> Unit> where K : CharSequence, K :
    Comparable<K> =
        (referenceStore["""${(reference as kotlin.reflect.KFunction<*>).name}|(mock.template.typealiaz.Alias977<kotlin.Any>) -> kotlin.Unit|[[mock.template.typealiaz.Alias977<X>], [kotlin.CharSequence & kotlin.Comparable<X>]]"""]
            ?: throw
            IllegalStateException("""Unknown method ${reference.name} with signature (T) -> kotlin.Unit!"""))
            as tech.antibytes.kmock.KMockContract.FunProxy<kotlin.Unit,
                (mock.template.typealiaz.Alias977<kotlin.Any>) -> kotlin.Unit>
}
