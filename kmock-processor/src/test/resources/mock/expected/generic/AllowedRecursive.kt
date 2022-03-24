package mock.template.generic

import kotlin.Any
import kotlin.Boolean
import kotlin.Char
import kotlin.CharSequence
import kotlin.Comparable
import kotlin.LazyThreadSafetyMode
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.sequences.Sequence
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy
import tech.antibytes.kmock.proxy.relaxVoidFunction

internal class AllowedRecursiveMock<K : Any, L>(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: AllowedRecursive<K, L>? = null,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    relaxUnitFun: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
    relaxed: Boolean = false
) : AllowedRecursive<K, L> where L : Any, L : Comparable<L> {
    private val __spyOn: AllowedRecursive<K, L>? = spyOn

    public override var template: L
        get() = _template.onGet()
        set(`value`) = _template.onSet(value)

    public val _template: KMockContract.PropertyProxy<L> by lazy(mode =
    LazyThreadSafetyMode.PUBLICATION) {
        if (spyOn == null) {
            PropertyProxy("mock.template.generic.AllowedRecursiveMock#_template", spyOnGet = null,
                spyOnSet = null, collector = verifier, freeze = freeze, relaxer = null)
        } else {
            PropertyProxy("mock.template.generic.AllowedRecursiveMock#_template", spyOnGet = {
                spyOn.template }, spyOnSet = { spyOn.template = it; Unit }, collector = verifier, freeze =
            freeze, relaxer = null)
        }
    }

    public val _ossWithSequencesSequence: KMockContract.SyncFunProxy<Unit, (kotlin.Any) ->
    kotlin.Unit> by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
        SyncFunProxy("mock.template.generic.AllowedRecursiveMock#_ossWithSequencesSequence", spyOn
        = if (spyOn != null) { { payload ->
            @Suppress("UNCHECKED_CAST")
            payload as kotlin.sequences.Sequence<kotlin.Char>
            @Suppress("UNCHECKED_CAST")
            payload as kotlin.CharSequence
            @Suppress("UNCHECKED_CAST")
            payload as kotlin.Comparable<Any?>
            oss(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)
    }

    public val _ossWithVoid: KMockContract.SyncFunProxy<Any, () -> kotlin.Any> by lazy(mode =
    LazyThreadSafetyMode.PUBLICATION) {
        SyncFunProxy("mock.template.generic.AllowedRecursiveMock#_ossWithVoid", spyOn = if (spyOn
            != null) { { @Suppress("UNCHECKED_CAST")
        oss() as kotlin.Comparable<Any?> } } else { null }, collector = verifier, freeze = freeze,
            relaxer = null)
    }

    public val _brassWithComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<Any?>) ->
    kotlin.Unit> by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
        SyncFunProxy("mock.template.generic.AllowedRecursiveMock#_brassWithComparable", spyOn = if
                                                                                                    (spyOn != null) { { payload ->
            brass(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                          (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)
    }

    public val _brassWithVoid: KMockContract.SyncFunProxy<kotlin.Comparable<Any?>, () ->
    kotlin.Comparable<Any?>> by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
        SyncFunProxy("mock.template.generic.AllowedRecursiveMock#_brassWithVoid", spyOn = if (spyOn
            != null) { { @Suppress("UNCHECKED_CAST")
        brass() as kotlin.Comparable<Any?> } } else { null }, collector = verifier, freeze = freeze,
            relaxer = null)
    }

    public val _issWithComparable: KMockContract.SyncFunProxy<Unit, (kotlin.Comparable<Any?>) ->
    kotlin.Unit> by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
        SyncFunProxy("mock.template.generic.AllowedRecursiveMock#_issWithComparable", spyOn = if
                                                                                                  (spyOn != null) { { payload ->
            iss(payload) } } else { null }, collector = verifier, freeze = freeze, unitFunRelaxer = if
                                                                                                        (relaxUnitFun) { { relaxVoidFunction() } } else { null }, relaxer = null, buildInRelaxer =
        null)
    }

    public val _issWithVoid: KMockContract.SyncFunProxy<kotlin.Comparable<Any?>, () ->
    kotlin.Comparable<Any?>> by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
        SyncFunProxy("mock.template.generic.AllowedRecursiveMock#_issWithVoid", spyOn = if (spyOn
            != null) { { @Suppress("UNCHECKED_CAST")
        iss() as kotlin.Comparable<Any?> } } else { null }, collector = verifier, freeze = freeze,
            relaxer = null)
    }

    public override fun <T> oss(payload: T): Unit where T : Sequence<Char>, T : CharSequence, T :
    Comparable<List<T>> = _ossWithSequencesSequence.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T> oss(): T where T : Sequence<Char>, T : CharSequence, T :
    Comparable<List<T>> = _ossWithVoid.invoke() as T

    public override fun <T : Comparable<List<T>>> brass(payload: T): Unit =
        _brassWithComparable.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<List<T>>> brass(): T = _brassWithVoid.invoke() as T

    public override fun <T : Comparable<T>> iss(payload: T): Unit = _issWithComparable.invoke(payload)

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Comparable<T>> iss(): T = _issWithVoid.invoke() as T

    public fun _clearMock(): Unit {
        _template.clear()
        _ossWithSequencesSequence.clear()
        _ossWithVoid.clear()
        _brassWithComparable.clear()
        _brassWithVoid.clear()
        _issWithComparable.clear()
        _issWithVoid.clear()
    }
}
