package mock.template.spy

import kotlin.Any
import kotlin.Boolean
import kotlin.Char
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.sequences.Sequence
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal class AllowedRecursiveMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: AllowedRecursive<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false,
) : AllowedRecursive<K, L> where L : Any, L : Comparable<L> {
    private val __spyOn: AllowedRecursive<K, L>? = spyOn

    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.spy.AllowedRecursiveMock#_template", collector
        = verifier, freeze = freeze) {
            useSpyOnGetIf(__spyOn) { __spyOn!!.template }
            useSpyOnSetIf(__spyOn) { value -> __spyOn!!.template = value }
        }

    public val _ossWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AllowedRecursiveMock#_ossWithVoid",
            collector = verifier, freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { @Suppress("UNCHECKED_CAST")
                __spyOn!!.oss() as kotlin.Comparable<Any?> }
            )
        }

    public val _ossWithSequencesSequence: KMockContract.SyncFunProxy<Unit, (kotlin.Any) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AllowedRecursiveMock#_ossWithSequencesSequence",
            collector = verifier, freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { payload ->

                    @Suppress("UNCHECKED_CAST")
                    payload as kotlin.sequences.Sequence<kotlin.Char>

                    @Suppress("UNCHECKED_CAST")
                    payload as kotlin.CharSequence

                    @Suppress("UNCHECKED_CAST")
                    payload as kotlin.Comparable<Any?>
                    __spyOn!!.oss(payload)
                }
            )
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _ossWithSequencesSequences: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AllowedRecursiveMock#_ossWithSequencesSequences",
            collector = verifier, freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { payload ->

                    @Suppress("UNCHECKED_CAST")
                    payload as Array<kotlin.sequences.Sequence<kotlin.Char>>

                    @Suppress("UNCHECKED_CAST")
                    payload as Array<kotlin.CharSequence>

                    @Suppress("UNCHECKED_CAST")
                    payload as Array<kotlin.Comparable<Any?>>
                    __spyOn!!.oss(*payload)
                }
            )
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _brassWithVoid: KMockContract.SyncFunProxy<kotlin.Comparable<Any?>, () ->
    kotlin.Comparable<Any?>> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AllowedRecursiveMock#_brassWithVoid",
            collector = verifier, freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { @Suppress("UNCHECKED_CAST")
                __spyOn!!.brass() as kotlin.Comparable<Any?> }
            )
        }

    public val _brassWithComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<Any?>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AllowedRecursiveMock#_brassWithComparable",
            collector = verifier, freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { payload ->
                    __spyOn!!.brass(payload)
                }
            )
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _brassWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<Any?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AllowedRecursiveMock#_brassWithComparables",
            collector = verifier, freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { payload ->
                    __spyOn!!.brass(*payload)
                }
            )
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _issWithVoid: KMockContract.SyncFunProxy<kotlin.Comparable<Any?>, () ->
    kotlin.Comparable<Any?>> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AllowedRecursiveMock#_issWithVoid",
            collector = verifier, freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { @Suppress("UNCHECKED_CAST")
                __spyOn!!.iss() as kotlin.Comparable<Any?> }
            )
        }

    public val _issWithComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<Any?>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AllowedRecursiveMock#_issWithComparable",
            collector = verifier, freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { payload ->
                    __spyOn!!.iss(payload)
                }
            )
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _issWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<Any?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AllowedRecursiveMock#_issWithComparables",
            collector = verifier, freeze = freeze) {
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { payload ->
                    __spyOn!!.iss(*payload)
                }
            )
            useUnitFunRelaxerIf(relaxUnitFun || relaxed)
        }

    public val _toString: KMockContract.SyncFunProxy<String, () -> kotlin.String> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AllowedRecursiveMock#_toString", collector
        = verifier, freeze = freeze, ignorableForVerification = true) {
            useToStringRelaxer { super.toString() }
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { __spyOn!!.toString() }
            )
        }

    public val _equals: KMockContract.SyncFunProxy<Boolean, (kotlin.Any?) -> kotlin.Boolean> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AllowedRecursiveMock#_equals", collector =
        verifier, freeze = freeze, ignorableForVerification = true) {
            useEqualsRelaxer { other ->
                super.equals(other)
            }
            useSpyOnEqualsIf(
               spyTarget  = __spyOn,
               equals = { other ->
                    super.equals(other)
                },
                mockKlass = AllowedRecursiveMock::class
            )
        }

    public val _hashCode: KMockContract.SyncFunProxy<Int, () -> kotlin.Int> =
        ProxyFactory.createSyncFunProxy("mock.template.spy.AllowedRecursiveMock#_hashCode", collector
        = verifier, freeze = freeze, ignorableForVerification = true) {
            useHashCodeRelaxer { super.hashCode() }
            useSpyIf(
                spyTarget = __spyOn,
                spyOn = { __spyOn!!.hashCode() }
            )
        }

    @Suppress("UNCHECKED_CAST")
    public override fun <T> oss(): T where T : Sequence<Char>, T : CharSequence, T :
    Comparable<List<T>> = _ossWithVoid.invoke() as T

    public override fun <T> oss(payload: T): Unit where T : Sequence<Char>, T : CharSequence, T :
    Comparable<List<T>> = _ossWithSequencesSequence.invoke(payload)

    public override fun <T> oss(vararg payload: T): Unit where T : Sequence<Char>, T : CharSequence, T
    : Comparable<List<T>> = _ossWithSequencesSequences.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<T>>> brass(): T = _brassWithVoid.invoke() as T

    public override fun <T : Comparable<List<T>>> brass(payload: T): Unit =
        _brassWithComparable.invoke(payload)

    public override fun <T : Comparable<List<T>>> brass(vararg payload: T): Unit =
        _brassWithComparables.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<T>> iss(): T = _issWithVoid.invoke() as T

    public override fun <T : Comparable<T>> iss(payload: T): Unit = _issWithComparable.invoke(payload)

    public override fun <T : Comparable<T>> iss(vararg payload: T): Unit =
        _issWithComparables.invoke(payload)

    public override fun toString(): String = _toString.invoke()

    public override fun equals(other: Any?): Boolean = _equals.invoke(other)

    public override fun hashCode(): Int = _hashCode.invoke()

    public fun _clearMock(): Unit {
        _template.clear()
        _ossWithVoid.clear()
        _ossWithSequencesSequence.clear()
        _ossWithSequencesSequences.clear()
        _brassWithVoid.clear()
        _brassWithComparable.clear()
        _brassWithComparables.clear()
        _issWithVoid.clear()
        _issWithComparable.clear()
        _issWithComparables.clear()
        _toString.clear()
        _equals.clear()
        _hashCode.clear()
    }
}
