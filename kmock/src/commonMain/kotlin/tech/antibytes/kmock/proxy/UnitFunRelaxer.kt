/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract.Relaxer

internal val kmockUnitFunRelaxer: Relaxer<Unit> = Relaxer { /* Do nothing */ }
