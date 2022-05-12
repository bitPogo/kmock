/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package multi.template.commonGeneric

interface GenericCommonContract {
    interface Generic3<K, L> : Comparable<Generic3<L, K>> {
        fun doSomething(arg: K): L
    }
}
