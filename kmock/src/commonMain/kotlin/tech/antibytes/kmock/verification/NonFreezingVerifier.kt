/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.Reference

/**
 * Container which collects and holds actual references of proxy calls in a non freezing manner.
 * The references are ordered by their invocation.
 * @param coverAllInvocations flag to enable/disable capturing including explicit ignorable Proxies. Default is false.
 * @see Verifier
 * @author Matthias Geisler
 */
class NonFreezingVerifier(coverAllInvocations: Boolean = false) : VerifierBase(coverAllInvocations) {
    override val _references: MutableList<Reference> = mutableListOf()
}