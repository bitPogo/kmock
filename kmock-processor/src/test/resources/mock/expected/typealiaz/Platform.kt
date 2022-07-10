package mock.template.typealiaz

import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Char
import kotlin.CharSequence
import kotlin.Comparable
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

internal class PlatformMock<L : Alias23>(
    collector: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: Platform<L>? = null,
    freeze: Boolean = true,
    @Suppress("unused")
    private val relaxUnitFun: Boolean = false,
    @Suppress("unused")
    private val relaxed: Boolean = false,
) : Platform<L> {
    public override val prop: Alias73<String>
        get() = _prop.executeOnGet()

    public val _prop: KMockContract.PropertyProxy<Alias73<String>> =
        ProxyFactory.createPropertyProxy("mock.template.typealiaz.PlatformMock#_prop", collector =
        collector, freeze = freeze)

    public val _doSomething: KMockContract.SyncFunProxy<Any, (
        Alias77<Any>,
        Alias23,
        Alias21,
    ) -> Any> = ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_doSomething",
        collector = collector, freeze = freeze)

    public val _doAnythingElseWithAlias21Alias23: KMockContract.SyncFunProxy<Unit, (Alias21,
        Alias23) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_doAnythingElseWithAlias21Alias23",
            collector = collector, freeze = freeze)

    public val _doAnythingElseWithAlias77:
        KMockContract.SyncFunProxy<Unit, (Alias77<Alias77<Alias21>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_doAnythingElseWithAlias77",
            collector = collector, freeze = freeze)

    public val _doOtherThing: KMockContract.SyncFunProxy<Unit, (Alias77<Alias77<Alias21>>,
        Comparable<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_doOtherThing",
            collector = collector, freeze = freeze)

    public val _doSomethingElseWithTAlias21LAlias23: KMockContract.SyncFunProxy<Unit, (Alias21,
        Alias23) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_doSomethingElseWithTAlias21LAlias23",
            collector = collector, freeze = freeze)

    public val _doSomethingElseWithTAlias77: KMockContract.SyncFunProxy<Unit, (Alias77<Any?>) -> Unit>
        =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_doSomethingElseWithTAlias77",
            collector = collector, freeze = freeze)

    public val _fooWithTAlias73: KMockContract.SyncFunProxy<Unit, (Alias73<Any?>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_fooWithTAlias73",
            collector = collector, freeze = freeze)

    public val _fooWithAnyAlias73: KMockContract.SyncFunProxy<Unit, (Any, Alias73<String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_fooWithAnyAlias73",
            collector = collector, freeze = freeze)

    public val _fooWithCharAlias73s: KMockContract.SyncFunProxy<Unit, (Char,
        Array<out Alias73<IntArray>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_fooWithCharAlias73s",
            collector = collector, freeze = freeze)

    public val _fooWithIntAlias73s: KMockContract.SyncFunProxy<Unit, (Int,
        Array<out Alias73<out String>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_fooWithIntAlias73s",
            collector = collector, freeze = freeze)

    public val _fooWithLongTAlias73s: KMockContract.SyncFunProxy<Unit, (Long,
        Array<out Alias73<out Alias77<Alias73<Int>>>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_fooWithLongTAlias73s",
            collector = collector, freeze = freeze)

    public val _barWithAlias99: KMockContract.SyncFunProxy<Unit, (Alias99<String>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_barWithAlias99",
            collector = collector, freeze = freeze)

    public val _barWithTAlias99s:
        KMockContract.SyncFunProxy<Unit, (Array<out Alias99<String>>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_barWithTAlias99s",
            collector = collector, freeze = freeze)

    public val _barWithLongTAlias99s:
        KMockContract.SyncFunProxy<Alias99<out Alias77<Alias73<Int>>>, (Long,
            Array<out Alias99<out Alias77<Alias73<Int>>>>) -> Alias99<out Alias77<Alias73<Int>>>> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_barWithLongTAlias99s",
            collector = collector, freeze = freeze)

    public val _barWithTAlias41s:
        KMockContract.SyncFunProxy<Unit, (Array<out Alias41<Alias23, out Alias77<Alias73<Int>>>>) -> Unit>
        = ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_barWithTAlias41s",
        collector = collector, freeze = freeze)

    public val _run: KMockContract.SyncFunProxy<Alias21, (L) -> Alias21> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_run", collector =
        collector, freeze = freeze)

    public val _rol: KMockContract.SyncFunProxy<Unit, (Alias73<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_rol", collector =
        collector, freeze = freeze)

    public val _lol: KMockContract.SyncFunProxy<Unit, (Alias73<Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_lol", collector =
        collector, freeze = freeze)

    public val _fol: KMockContract.SyncFunProxy<Unit, (Map<String, Any>) -> Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.typealiaz.PlatformMock#_fol", collector =
        collector, freeze = freeze)

    public override fun doSomething(
        arg0: Alias77<Any>,
        arg1: Alias23,
        arg2: Alias21,
    ): Any = _doSomething.invoke(arg0, arg1, arg2)

    public override fun doAnythingElse(arg1: Alias21, arg2: Alias23): Unit =
        _doAnythingElseWithAlias21Alias23.invoke(arg1, arg2) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun doAnythingElse(arg1: Alias77<Alias77<Alias21>>): Unit =
        _doAnythingElseWithAlias77.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias77<Alias21>, X : Comparable<X>> doOtherThing(arg1: Alias77<T>,
        arg0: X): Unit = _doOtherThing.invoke(arg1, arg0) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias21, L : Alias23> doSomethingElse(arg1: T, arg2: L): Unit =
        _doSomethingElseWithTAlias21LAlias23.invoke(arg1, arg2) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias77<K>, K> doSomethingElse(arg1: T): Unit =
        _doSomethingElseWithTAlias77.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias73<K>, K> foo(arg1: T): Unit = _fooWithTAlias73.invoke(arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun foo(arg0: Any, arg1: Alias73<String>): Unit = _fooWithAnyAlias73.invoke(arg0,
        arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun foo(arg0: Char, vararg arg1: Alias73<IntArray>): Unit =
        _fooWithCharAlias73s.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun foo(arg0: Int, vararg arg1: Alias73<out String>): Unit =
        _fooWithIntAlias73s.invoke(arg0, arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun <T : Alias73<out Alias77<Alias73<Int>>>> foo(arg0: Long, vararg arg1: T): Unit
        = _fooWithLongTAlias73s.invoke(arg0, arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun bar(arg1: Alias99<String>): Unit = _barWithAlias99.invoke(arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias99<String>> bar(vararg arg1: T): Unit =
        _barWithTAlias99s.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Alias99<out Alias77<Alias73<Int>>>> bar(arg0: Long, vararg arg1: T): T =
        _barWithLongTAlias99s.invoke(arg0, arg1) as T

    public override fun <T : Alias41<Alias23, out Alias77<Alias73<Int>>>> bar(vararg arg1: T): Unit =
        _barWithTAlias41s.invoke(arg1) {
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public override fun run(arg: L): Alias21 = _run.invoke(arg)

    public override fun <T : Alias73<K>, K> rol(arg1: T): Unit where K : CharSequence, K :
    Comparable<K> = _rol.invoke(arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Alias73<K>, K> lol(arg1: T): Unit where K : CharSequence, K :
    Comparable<T> = _lol.invoke(arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public override fun <T : Map<String, K>, K> fol(arg1: T): Unit where K : CharSequence, K :
    Comparable<K> = _fol.invoke(arg1) {
        useUnitFunRelaxerIf(relaxUnitFun || relaxed)
    }

    public fun _clearMock(): Unit {
        _prop.clear()
        _doSomething.clear()
        _doAnythingElseWithAlias21Alias23.clear()
        _doAnythingElseWithAlias77.clear()
        _doOtherThing.clear()
        _doSomethingElseWithTAlias21LAlias23.clear()
        _doSomethingElseWithTAlias77.clear()
        _fooWithTAlias73.clear()
        _fooWithAnyAlias73.clear()
        _fooWithCharAlias73s.clear()
        _fooWithIntAlias73s.clear()
        _fooWithLongTAlias73s.clear()
        _barWithAlias99.clear()
        _barWithTAlias99s.clear()
        _barWithLongTAlias99s.clear()
        _barWithTAlias41s.clear()
        _run.clear()
        _rol.clear()
        _lol.clear()
        _fol.clear()
    }
}
