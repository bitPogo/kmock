/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import co.touchlab.stately.collections.IsoMutableList
import co.touchlab.stately.collections.sharedMutableListOf
import tech.antibytes.kmock.KMockContract.Reference

/**
 * Container which collects and holds actual references of proxy calls in a freezing manner.
 * The references are ordered by their invocation.
 * This is intended as default mode for Verification.
 * @param coverAllInvocations flag to enable/disable capturing including explicit ignorable Proxies. Default is false.
 * @author Matthias Geisler
 */
class Verifier(coverAllInvocations: Boolean = false) : VerifierBase(coverAllInvocations) {
    override val _references: IsoMutableList<Reference> = sharedMutableListOf()
}
