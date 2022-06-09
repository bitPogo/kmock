package mock.template.typealiaz

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Char
import kotlin.Comparable
import kotlin.Function1
import kotlin.Int
import kotlin.IntArray
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.Map
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class InheritedMock<R : Alias33>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Inherited<R>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Inherited<R> {
    public override val prop: Alias93<String, String>
        get() = _prop.onGet()

    public val _prop: KMockContract.PropertyProxy<Alias93<String, String>> =
        ProxyFactory.createPropertyProxy("mock.template.typealiaz.InheritedMock#_prop", collector =
        collector, freeze = freeze)

    public val _doSomethingWithAlias97Alias33Alias31: KMockContract.SyncFunProxy<Any, (
        Alias97<Any>,
        Alias33<Any, Any>,
        Alias31<Any, Unit>,
    ) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_doSomethingWithAlias97Alias33Alias31",
            collector = collector, freeze = freeze)

    public val _doSomethingWithAlias97: KMockContract.SyncFunProxy<Any, (Alias97<String>) -> Any> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_doSomethingWithAlias97",
            collector = collector, freeze = freeze)

    public val _doAnythingElseWithAlias31Alias33:
        KMockContract.SyncFunProxy<Unit, (Alias31<Any, Unit>, Alias33<Any, Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_doAnythingElseWithAlias31Alias33",
            collector = collector, freeze = freeze)

    public val _doAnythingElseWithAlias97:
        KMockContract.SyncFunProxy<Unit, (Alias97<GenericSuper<Function1<Any, Unit>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_doAnythingElseWithAlias97",
            collector = collector, freeze = freeze)

    public val _doOtherThing:
        KMockContract.SyncFunProxy<Unit, (Alias97<Alias97<Function1<Any, Unit>>>,
            Comparable<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_doOtherThing",
            collector = collector, freeze = freeze)

    public val _doSomethingElseWithTAlias31LAlias33:
        KMockContract.SyncFunProxy<Unit, (Alias31<Any, Unit>, Alias33<Any, Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_doSomethingElseWithTAlias31LAlias33",
            collector = collector, freeze = freeze)

    public val _doSomethingElseWithTAlias97: KMockContract.SyncFunProxy<Unit, (Alias97<Any>) -> Unit>
        =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_doSomethingElseWithTAlias97",
            collector = collector, freeze = freeze)

    public val _fooWithTAlias93: KMockContract.SyncFunProxy<Unit, (Alias93<String, Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_fooWithTAlias93",
            collector = collector, freeze = freeze)

    public val _fooWithAnyAlias93: KMockContract.SyncFunProxy<Unit, (Any,
        Alias93<String, String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_fooWithAnyAlias93",
            collector = collector, freeze = freeze)

    public val _fooWithCharAlias93s: KMockContract.SyncFunProxy<Unit, (Char,
        Array<out Alias93<String, IntArray>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_fooWithCharAlias93s",
            collector = collector, freeze = freeze)

    public val _fooWithIntAlias93s: KMockContract.SyncFunProxy<Unit, (Int,
        Array<out Alias93<String, String>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_fooWithIntAlias93s",
            collector = collector, freeze = freeze)

    public val _fooWithLongTAlias93s: KMockContract.SyncFunProxy<Unit, (Long,
        Array<out Alias93<String, GenericSuper<Map<String, Int>>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_fooWithLongTAlias93s",
            collector = collector, freeze = freeze)

    public val _barWithAlias199: KMockContract.SyncFunProxy<Unit, (Alias199<String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_barWithAlias199",
            collector = collector, freeze = freeze)

    public val _barWithTAlias199s:
        KMockContract.SyncFunProxy<Unit, (Array<out Alias199<String>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_barWithTAlias199s",
            collector = collector, freeze = freeze)

    public val _barWithLongTAlias199s:
        KMockContract.SyncFunProxy<Alias199<out GenericSuper<Map<String, Int>>>, (Long,
            Array<out Alias199<out GenericSuper<Map<String, Int>>>>) -> Alias199<out GenericSuper<Map<String, Int>>>>
        =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_barWithLongTAlias199s",
            collector = collector, freeze = freeze)

    public val _barWithTAlias200s:
        KMockContract.SyncFunProxy<Unit, (Array<out Alias200<Alias105<Function1<Any, Any>>, Alias199<out GenericSuper<Map<String, Int>>>>>) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_barWithTAlias200s",
        collector = collector, freeze = freeze)

    public val _run: KMockContract.SyncFunProxy<Alias31<Any, Unit>, (R) -> Alias31<Any, Unit>> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.InheritedMock#_run", collector =
        collector, freeze = freeze)

    public override fun doSomething(
        arg0: Alias97<Any>,
        arg1: Alias33<Any, Any>,
        arg2: Alias31<Any, Unit>,
    ): Any = _doSomethingWithAlias97Alias33Alias31.invoke(arg0, arg1, arg2)

    public override fun doSomething(arg0: Alias97<String>): Any = _doSomethingWithAlias97.invoke(arg0)

    public override fun doAnythingElse(arg1: Alias31<Any, Unit>, arg2: Alias33<Any, Any>): Unit =
        _doAnythingElseWithAlias31Alias33.invoke(arg1, arg2) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun doAnythingElse(arg1: Alias97<GenericSuper<Function1<Any, Unit>>>): Unit =
        _doAnythingElseWithAlias97.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias97<Function1<Any, Unit>>, X : Comparable<X>>
        doOtherThing(arg1: Alias97<T>, arg0: X): Unit = _doOtherThing.invoke(arg1, arg0) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias31<Any, Unit>, L : Alias33<Any, Any>> doSomethingElse(arg1: T,
        arg2: L): Unit = _doSomethingElseWithTAlias31LAlias33.invoke(arg1, arg2) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias97<K>, K> doSomethingElse(arg1: T): Unit =
        _doSomethingElseWithTAlias97.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias93<String, K>, K> foo(arg1: T): Unit = _fooWithTAlias93.invoke(arg1)
    {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun foo(arg0: Any, arg1: Alias93<String, String>): Unit =
        _fooWithAnyAlias93.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun foo(arg0: Char, vararg arg1: Alias93<String, IntArray>): Unit =
        _fooWithCharAlias93s.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun foo(arg0: Int, vararg arg1: Alias93<String, String>): Unit =
        _fooWithIntAlias93s.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias93<String, GenericSuper<Map<String, Int>>>> foo(arg0: Long, vararg
    arg1: T): Unit = _fooWithLongTAlias93s.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun bar(arg1: Alias199<String>): Unit = _barWithAlias199.invoke(arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias199<String>> bar(vararg arg1: T): Unit =
        _barWithTAlias199s.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Alias199<out GenericSuper<Map<String, Int>>>> bar(arg0: Long, vararg
    arg1: T): T = _barWithLongTAlias199s.invoke(arg0, arg1) as T

    public override fun <T :
    Alias200<Alias105<Function1<Any, Any>>, Alias199<out GenericSuper<Map<String, Int>>>>>
        bar(vararg arg1: T): Unit = _barWithTAlias200s.invoke(arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun run(arg: R): Alias31<Any, Unit> = _run.invoke(arg)

    public fun _clearMock(): Unit {
        _prop.clear()
        _doSomethingWithAlias97Alias33Alias31.clear()
        _doSomethingWithAlias97.clear()
        _doAnythingElseWithAlias31Alias33.clear()
        _doAnythingElseWithAlias97.clear()
        _doOtherThing.clear()
        _doSomethingElseWithTAlias31LAlias33.clear()
        _doSomethingElseWithTAlias97.clear()
        _fooWithTAlias93.clear()
        _fooWithAnyAlias93.clear()
        _fooWithCharAlias93s.clear()
        _fooWithIntAlias93s.clear()
        _fooWithLongTAlias93s.clear()
        _barWithAlias199.clear()
        _barWithTAlias199s.clear()
        _barWithLongTAlias199s.clear()
        _barWithTAlias200s.clear()
        _run.clear()
    }
}
