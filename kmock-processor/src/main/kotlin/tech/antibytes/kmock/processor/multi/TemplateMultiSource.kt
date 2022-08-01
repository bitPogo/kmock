/* ktlint-disable filename */
/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.multi

import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource

internal fun TemplateMultiSource.hasGenerics(): Boolean = this.generics.any { item -> item != null }
