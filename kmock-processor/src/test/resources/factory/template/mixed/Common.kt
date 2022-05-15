/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package factory.template.mixed

interface Common<L> where L: Comparable<L>, L: CharSequence {
    val bar: L
}
