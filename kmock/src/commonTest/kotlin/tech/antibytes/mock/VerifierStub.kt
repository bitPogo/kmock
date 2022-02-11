/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.mock

import tech.antibytes.kmock.KMockContract

class VerifierStub(
    override val references: List<KMockContract.Reference>
) : KMockContract.Verifier
