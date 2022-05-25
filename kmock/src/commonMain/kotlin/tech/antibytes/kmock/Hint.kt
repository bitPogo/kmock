/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import kotlin.reflect.KClass

/**
 * Indicator for overloaded methods which have no arguments
 * @author Matthias Geisler
 */
public data class Hint0(val type: Unit = Unit)

/**
 * IndicatorFactory for overloaded methods which have no arguments
 * @author Matthias Geisler
 */
public fun hint(): Hint0 = Hint0()

/**
 * Indicator for overloaded methods which an argument
 * @author Matthias Geisler
 */
public data class Hint1<Type0 : Any>(val type: KClass<Type0>)

/**
 * IndicatorFactory for overloaded methods which have an argument
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any> hint(
    type0: KClass<Type0> = Type0::class,
): Hint1<Type0> = Hint1(type0)

/**
 * Indicator for overloaded methods which have 2 arguments
 * @author Matthias Geisler
 */
public data class Hint2<Type0 : Any, Type1 : Any>(
    val type0: KClass<Type0>,
    val type1: KClass<Type1>,
)

/**
 * IndicatorFactory for overloaded methods which have 2 arguments
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any, reified Type1 : Any> hint(
    type0: KClass<Type0> = Type0::class,
    type1: KClass<Type1> = Type1::class
): Hint2<Type0, Type1> = Hint2(type0, type1)

/**
 * Indicator for overloaded methods which have 3 arguments
 * @author Matthias Geisler
 */
public data class Hint3<Type0 : Any, Type1 : Any, Type2 : Any>(
    val type0: KClass<Type0>,
    val type1: KClass<Type1>,
    val type2: KClass<Type2>,
)

/**
 * IndicatorFactory for overloaded methods which have 3 arguments
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any, reified Type1 : Any, reified Type2 : Any> hint(
    type0: KClass<Type0> = Type0::class,
    type1: KClass<Type1> = Type1::class,
    type2: KClass<Type2> = Type2::class,
): Hint3<Type0, Type1, Type2> = Hint3(type0, type1, type2)

/**
 * Indicator for overloaded methods which have 4 arguments
 * @author Matthias Geisler
 */
public data class Hint4<Type0 : Any, Type1 : Any, Type2 : Any, Type3 : Any>(
    val type0: KClass<Type0>,
    val type1: KClass<Type1>,
    val type2: KClass<Type2>,
    val type3: KClass<Type3>,
)

/**
 * IndicatorFactory for overloaded methods which have 4 arguments
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any, reified Type1 : Any, reified Type2 : Any, reified Type3 : Any> hint(
    type0: KClass<Type0> = Type0::class,
    type1: KClass<Type1> = Type1::class,
    type2: KClass<Type2> = Type2::class,
    type3: KClass<Type3> = Type3::class,
): Hint4<Type0, Type1, Type2, Type3> = Hint4(type0, type1, type2, type3)

/**
 * Indicator for overloaded methods which have 5 arguments
 * @author Matthias Geisler
 */
public data class Hint5<Type0 : Any, Type1 : Any, Type2 : Any, Type3 : Any, Type4 : Any>(
    val type0: KClass<Type0>,
    val type1: KClass<Type1>,
    val type2: KClass<Type2>,
    val type3: KClass<Type3>,
    val type4: KClass<Type4>,
)

/**
 * IndicatorFactory for overloaded methods which have 5 arguments
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any, reified Type1 : Any, reified Type2 : Any, reified Type3 : Any, reified Type4 : Any> hint(
    type0: KClass<Type0> = Type0::class,
    type1: KClass<Type1> = Type1::class,
    type2: KClass<Type2> = Type2::class,
    type3: KClass<Type3> = Type3::class,
    type4: KClass<Type4> = Type4::class,
): Hint5<Type0, Type1, Type2, Type3, Type4> = Hint5(type0, type1, type2, type3, type4)

/**
 * Indicator for overloaded methods which have 6 arguments
 * @author Matthias Geisler
 */
public data class Hint6<Type0 : Any, Type1 : Any, Type2 : Any, Type3 : Any, Type4 : Any, Type5 : Any>(
    val type0: KClass<Type0>,
    val type1: KClass<Type1>,
    val type2: KClass<Type2>,
    val type3: KClass<Type3>,
    val type4: KClass<Type4>,
    val type5: KClass<Type5>,
)

/**
 * IndicatorFactory for overloaded methods which have 6 arguments
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any, reified Type1 : Any, reified Type2 : Any, reified Type3 : Any, reified Type4 : Any, reified Type5 : Any> hint(
    type0: KClass<Type0> = Type0::class,
    type1: KClass<Type1> = Type1::class,
    type2: KClass<Type2> = Type2::class,
    type3: KClass<Type3> = Type3::class,
    type4: KClass<Type4> = Type4::class,
    type5: KClass<Type5> = Type5::class,
): Hint6<Type0, Type1, Type2, Type3, Type4, Type5> = Hint6(type0, type1, type2, type3, type4, type5)

/**
 * Indicator for overloaded methods which have 7 arguments
 * @author Matthias Geisler
 */
public data class Hint7<Type0 : Any, Type1 : Any, Type2 : Any, Type3 : Any, Type4 : Any, Type5 : Any, Type6 : Any>(
    val type0: KClass<Type0>,
    val type1: KClass<Type1>,
    val type2: KClass<Type2>,
    val type3: KClass<Type3>,
    val type4: KClass<Type4>,
    val type5: KClass<Type5>,
    val type6: KClass<Type6>,
)

/**
 * IndicatorFactory for overloaded methods which have 7 arguments
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any, reified Type1 : Any, reified Type2 : Any, reified Type3 : Any, reified Type4 : Any, reified Type5 : Any, reified Type6 : Any> hint(
    type0: KClass<Type0> = Type0::class,
    type1: KClass<Type1> = Type1::class,
    type2: KClass<Type2> = Type2::class,
    type3: KClass<Type3> = Type3::class,
    type4: KClass<Type4> = Type4::class,
    type5: KClass<Type5> = Type5::class,
    type6: KClass<Type6> = Type6::class,
): Hint7<Type0, Type1, Type2, Type3, Type4, Type5, Type6> = Hint7(type0, type1, type2, type3, type4, type5, type6)

/**
 * Indicator for overloaded methods which have 8 arguments
 * @author Matthias Geisler
 */
public data class Hint8<Type0 : Any, Type1 : Any, Type2 : Any, Type3 : Any, Type4 : Any, Type5 : Any, Type6 : Any, Type7 : Any>(
    val type0: KClass<Type0>,
    val type1: KClass<Type1>,
    val type2: KClass<Type2>,
    val type3: KClass<Type3>,
    val type4: KClass<Type4>,
    val type5: KClass<Type5>,
    val type6: KClass<Type6>,
    val type7: KClass<Type7>,
)

/**
 * IndicatorFactory for overloaded methods which have 8 arguments
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any, reified Type1 : Any, reified Type2 : Any, reified Type3 : Any, reified Type4 : Any, reified Type5 : Any, reified Type6 : Any, reified Type7 : Any> hint(
    type0: KClass<Type0> = Type0::class,
    type1: KClass<Type1> = Type1::class,
    type2: KClass<Type2> = Type2::class,
    type3: KClass<Type3> = Type3::class,
    type4: KClass<Type4> = Type4::class,
    type5: KClass<Type5> = Type5::class,
    type6: KClass<Type6> = Type6::class,
    type7: KClass<Type7> = Type7::class,
): Hint8<Type0, Type1, Type2, Type3, Type4, Type5, Type6, Type7> = Hint8(
    type0,
    type1,
    type2,
    type3,
    type4,
    type5,
    type6,
    type7,
)

/**
 * Indicator for overloaded methods which have 9 arguments
 * @author Matthias Geisler
 */
public data class Hint9<Type0 : Any, Type1 : Any, Type2 : Any, Type3 : Any, Type4 : Any, Type5 : Any, Type6 : Any, Type7 : Any, Type8 : Any>(
    val type0: KClass<Type0>,
    val type1: KClass<Type1>,
    val type2: KClass<Type2>,
    val type3: KClass<Type3>,
    val type4: KClass<Type4>,
    val type5: KClass<Type5>,
    val type6: KClass<Type6>,
    val type7: KClass<Type7>,
    val type8: KClass<Type8>,
)

/**
 * IndicatorFactory for overloaded methods which have 9 arguments
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any, reified Type1 : Any, reified Type2 : Any, reified Type3 : Any, reified Type4 : Any, reified Type5 : Any, reified Type6 : Any, reified Type7 : Any, reified Type8 : Any> hint(
    type0: KClass<Type0> = Type0::class,
    type1: KClass<Type1> = Type1::class,
    type2: KClass<Type2> = Type2::class,
    type3: KClass<Type3> = Type3::class,
    type4: KClass<Type4> = Type4::class,
    type5: KClass<Type5> = Type5::class,
    type6: KClass<Type6> = Type6::class,
    type7: KClass<Type7> = Type7::class,
    type8: KClass<Type8> = Type8::class,
): Hint9<Type0, Type1, Type2, Type3, Type4, Type5, Type6, Type7, Type8> = Hint9(
    type0,
    type1,
    type2,
    type3,
    type4,
    type5,
    type6,
    type7,
    type8,
)

/**
 * Indicator for overloaded methods which have 10 arguments
 * @author Matthias Geisler
 */
public data class Hint10<Type0 : Any, Type1 : Any, Type2 : Any, Type3 : Any, Type4 : Any, Type5 : Any, Type6 : Any, Type7 : Any, Type8 : Any, Type9 : Any>(
    val type0: KClass<Type0>,
    val type1: KClass<Type1>,
    val type2: KClass<Type2>,
    val type3: KClass<Type3>,
    val type4: KClass<Type4>,
    val type5: KClass<Type5>,
    val type6: KClass<Type6>,
    val type7: KClass<Type7>,
    val type8: KClass<Type8>,
    val type9: KClass<Type9>,
)

/**
 * IndicatorFactory for overloaded methods which have 10 arguments
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any, reified Type1 : Any, reified Type2 : Any, reified Type3 : Any, reified Type4 : Any, reified Type5 : Any, reified Type6 : Any, reified Type7 : Any, reified Type8 : Any, reified Type9 : Any> hint(
    type0: KClass<Type0> = Type0::class,
    type1: KClass<Type1> = Type1::class,
    type2: KClass<Type2> = Type2::class,
    type3: KClass<Type3> = Type3::class,
    type4: KClass<Type4> = Type4::class,
    type5: KClass<Type5> = Type5::class,
    type6: KClass<Type6> = Type6::class,
    type7: KClass<Type7> = Type7::class,
    type8: KClass<Type8> = Type8::class,
    type9: KClass<Type9> = Type9::class,
): Hint10<Type0, Type1, Type2, Type3, Type4, Type5, Type6, Type7, Type8, Type9> = Hint10(
    type0,
    type1,
    type2,
    type3,
    type4,
    type5,
    type6,
    type7,
    type8,
    type9,
)

/**
 * Indicator for overloaded methods which have 11 arguments
 * @author Matthias Geisler
 */
public data class Hint11<Type0 : Any, Type1 : Any, Type2 : Any, Type3 : Any, Type4 : Any, Type5 : Any, Type6 : Any, Type7 : Any, Type8 : Any, Type9 : Any, Type10 : Any>(
    val type0: KClass<Type0>,
    val type1: KClass<Type1>,
    val type2: KClass<Type2>,
    val type3: KClass<Type3>,
    val type4: KClass<Type4>,
    val type5: KClass<Type5>,
    val type6: KClass<Type6>,
    val type7: KClass<Type7>,
    val type8: KClass<Type8>,
    val type9: KClass<Type9>,
    val type10: KClass<Type10>,
)

/**
 * IndicatorFactory for overloaded methods which have 11 arguments
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any, reified Type1 : Any, reified Type2 : Any, reified Type3 : Any, reified Type4 : Any, reified Type5 : Any, reified Type6 : Any, reified Type7 : Any, reified Type8 : Any, reified Type9 : Any, reified Type10 : Any> hint(
    type0: KClass<Type0> = Type0::class,
    type1: KClass<Type1> = Type1::class,
    type2: KClass<Type2> = Type2::class,
    type3: KClass<Type3> = Type3::class,
    type4: KClass<Type4> = Type4::class,
    type5: KClass<Type5> = Type5::class,
    type6: KClass<Type6> = Type6::class,
    type7: KClass<Type7> = Type7::class,
    type8: KClass<Type8> = Type8::class,
    type9: KClass<Type9> = Type9::class,
    type10: KClass<Type10> = Type10::class,
): Hint11<Type0, Type1, Type2, Type3, Type4, Type5, Type6, Type7, Type8, Type9, Type10> = Hint11(
    type0,
    type1,
    type2,
    type3,
    type4,
    type5,
    type6,
    type7,
    type8,
    type9,
    type10,
)

/**
 * Indicator for overloaded methods which have 12 arguments
 * @author Matthias Geisler
 */
public data class Hint12<Type0 : Any, Type1 : Any, Type2 : Any, Type3 : Any, Type4 : Any, Type5 : Any, Type6 : Any, Type7 : Any, Type8 : Any, Type9 : Any, Type10 : Any, Type11 : Any>(
    val type0: KClass<Type0>,
    val type1: KClass<Type1>,
    val type2: KClass<Type2>,
    val type3: KClass<Type3>,
    val type4: KClass<Type4>,
    val type5: KClass<Type5>,
    val type6: KClass<Type6>,
    val type7: KClass<Type7>,
    val type8: KClass<Type8>,
    val type9: KClass<Type9>,
    val type10: KClass<Type10>,
    val type11: KClass<Type11>,
)

/**
 * IndicatorFactory for overloaded methods which have 12 arguments
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any, reified Type1 : Any, reified Type2 : Any, reified Type3 : Any, reified Type4 : Any, reified Type5 : Any, reified Type6 : Any, reified Type7 : Any, reified Type8 : Any, reified Type9 : Any, reified Type10 : Any, reified Type11 : Any> hint(
    type0: KClass<Type0> = Type0::class,
    type1: KClass<Type1> = Type1::class,
    type2: KClass<Type2> = Type2::class,
    type3: KClass<Type3> = Type3::class,
    type4: KClass<Type4> = Type4::class,
    type5: KClass<Type5> = Type5::class,
    type6: KClass<Type6> = Type6::class,
    type7: KClass<Type7> = Type7::class,
    type8: KClass<Type8> = Type8::class,
    type9: KClass<Type9> = Type9::class,
    type10: KClass<Type10> = Type10::class,
    type11: KClass<Type11> = Type11::class,
): Hint12<Type0, Type1, Type2, Type3, Type4, Type5, Type6, Type7, Type8, Type9, Type10, Type11> = Hint12(
    type0,
    type1,
    type2,
    type3,
    type4,
    type5,
    type6,
    type7,
    type8,
    type9,
    type10,
    type11,
)

/**
 * Indicator for overloaded methods which have 13 arguments
 * @author Matthias Geisler
 */
public data class Hint13<Type0 : Any, Type1 : Any, Type2 : Any, Type3 : Any, Type4 : Any, Type5 : Any, Type6 : Any, Type7 : Any, Type8 : Any, Type9 : Any, Type10 : Any, Type11 : Any, Type12 : Any>(
    val type0: KClass<Type0>,
    val type1: KClass<Type1>,
    val type2: KClass<Type2>,
    val type3: KClass<Type3>,
    val type4: KClass<Type4>,
    val type5: KClass<Type5>,
    val type6: KClass<Type6>,
    val type7: KClass<Type7>,
    val type8: KClass<Type8>,
    val type9: KClass<Type9>,
    val type10: KClass<Type10>,
    val type11: KClass<Type11>,
    val type12: KClass<Type12>,
)

/**
 * IndicatorFactory for overloaded methods which have 13 arguments
 * @author Matthias Geisler
 */
public inline fun <reified Type0 : Any, reified Type1 : Any, reified Type2 : Any, reified Type3 : Any, reified Type4 : Any, reified Type5 : Any, reified Type6 : Any, reified Type7 : Any, reified Type8 : Any, reified Type9 : Any, reified Type10 : Any, reified Type11 : Any, reified Type12 : Any> hint(
    type0: KClass<Type0> = Type0::class,
    type1: KClass<Type1> = Type1::class,
    type2: KClass<Type2> = Type2::class,
    type3: KClass<Type3> = Type3::class,
    type4: KClass<Type4> = Type4::class,
    type5: KClass<Type5> = Type5::class,
    type6: KClass<Type6> = Type6::class,
    type7: KClass<Type7> = Type7::class,
    type8: KClass<Type8> = Type8::class,
    type9: KClass<Type9> = Type9::class,
    type10: KClass<Type10> = Type10::class,
    type11: KClass<Type11> = Type11::class,
    type12: KClass<Type12> = Type12::class,
): Hint13<Type0, Type1, Type2, Type3, Type4, Type5, Type6, Type7, Type8, Type9, Type10, Type11, Type12> = Hint13(
    type0,
    type1,
    type2,
    type3,
    type4,
    type5,
    type6,
    type7,
    type8,
    type9,
    type10,
    type11,
    type12,
)
