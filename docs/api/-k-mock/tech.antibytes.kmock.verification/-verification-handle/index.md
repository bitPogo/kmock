//[KMock](../../../index.md)/[tech.antibytes.kmock.verification](../index.md)/[VerificationHandle](index.md)



# VerificationHandle
 [common] data class [VerificationHandle](index.md)(**id**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **callIndices**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)>) : [KMockContract.VerificationHandle](../../tech.antibytes.kmock/-k-mock-contract/-verification-handle/index.md)

Handle with the aggregated information of a Proxy invocation. Meant for internal usage only!



#### Author


Matthias Geisler




## Constructors

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification/VerificationHandle/VerificationHandle/#kotlin.String#kotlin.collections.List[kotlin.Int]/PointingToDeclaration/"></a>[VerificationHandle](-verification-handle.md)| <a name="tech.antibytes.kmock.verification/VerificationHandle/VerificationHandle/#kotlin.String#kotlin.collections.List[kotlin.Int]/PointingToDeclaration/"></a> [common] fun [VerificationHandle](-verification-handle.md)(id: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), callIndices: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)>)   <br>|


## Properties

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock.verification/VerificationHandle/callIndices/#/PointingToDeclaration/"></a>[callIndices](call-indices.md)| <a name="tech.antibytes.kmock.verification/VerificationHandle/callIndices/#/PointingToDeclaration/"></a> [common] open override val [callIndices](call-indices.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)>List with aggregated indices of invocation of the refered Proxy.   <br>|
| <a name="tech.antibytes.kmock.verification/VerificationHandle/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="tech.antibytes.kmock.verification/VerificationHandle/id/#/PointingToDeclaration/"></a> [common] open override val [id](id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)Id of the Proxy.   <br>|
