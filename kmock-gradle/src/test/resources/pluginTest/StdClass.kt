/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package pluginTest

class StdClass {
    fun doNothing() = Unit

    class Nested()
}

class Child : StdClass()
