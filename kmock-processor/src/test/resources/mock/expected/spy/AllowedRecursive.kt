package mock.template.generic

import kotlin.Any
import kotlin.Boolean
import kotlin.Char
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.sequences.Sequence
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.AsyncFunProxy
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.SyncFunProxy
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class AllowedRecursiveMock<K : Any, L>(
    verifier: KMockContract.Collector = NoopCollector,
    @Suppress("UNUSED_PARAMETER")
    spyOn: AllowedRecursive<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : AllowedRecursive<K, L> where L : Any, L : Comparable<L> {
    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> =
        ProxyFactory.createPropertyProxy("mock.template.generic.AllowedRecursiveMock#_template",
            spyOnGet = null, spyOnSet = null, collector = verifier, freeze = freeze, relaxer = null)

    public val _ossWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.AllowedRecursiveMock#_ossWithVoid",
            spyOn = null, collector = verifier, freeze = freeze, relaxer = null)

    public val _ossWithSequencesSequence: KMockContract.SyncFunProxy<Unit, (kotlin.Any) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.AllowedRecursiveMock#_ossWithSequencesSequence",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _ossWithSequencesSequences: KMockContract.SyncFunProxy<Unit, (Array<out kotlin.Any>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.AllowedRecursiveMock#_ossWithSequencesSequences",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _brassWithVoid: KMockContract.SyncFunProxy<kotlin.Comparable<Any?>, () ->
    kotlin.Comparable<Any?>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.AllowedRecursiveMock#_brassWithVoid",
            spyOn = null, collector = verifier, freeze = freeze, relaxer = null)

    public val _brassWithComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<Any?>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.AllowedRecursiveMock#_brassWithComparable",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _brassWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<Any?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.AllowedRecursiveMock#_brassWithComparables",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _issWithVoid: KMockContract.SyncFunProxy<kotlin.Comparable<Any?>, () ->
    kotlin.Comparable<Any?>> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.AllowedRecursiveMock#_issWithVoid",
            spyOn = null, collector = verifier, freeze = freeze, relaxer = null)

    public val _issWithComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<Any?>) ->
    kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.AllowedRecursiveMock#_issWithComparable",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

    public val _issWithComparables: KMockContract.SyncFunProxy<Unit, (Array<out
    kotlin.Comparable<Any?>>) -> kotlin.Unit> =
        ProxyFactory.createSyncFunProxy("mock.template.generic.AllowedRecursiveMock#_issWithComparables",
            spyOn = null, collector = verifier, freeze = freeze, unitFunRelaxer = if (relaxUnitFun) { {
                relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer = null)

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
    }
}
