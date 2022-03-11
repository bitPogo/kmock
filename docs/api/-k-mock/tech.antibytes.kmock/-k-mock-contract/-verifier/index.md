//[KMock](../../../../index.md)/[tech.antibytes.kmock](../../index.md)/[KMockContract](../index.md)/[Verifier](index.md)



# Verifier
 [common] interface [Verifier](index.md)

Container which holds actual references of proxy calls. The references are ordered by their invocation.



#### Author


Matthias Geisler




## Functions

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.Verifier/clear/#/PointingToDeclaration/"></a>[clear](clear.md)| <a name="tech.antibytes.kmock/KMockContract.Verifier/clear/#/PointingToDeclaration/"></a>[common]  <br>Content  <br>abstract fun [clear](clear.md)()  <br>More info  <br>Clears the Container  <br><br><br>|


## Properties

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.Verifier/references/#/PointingToDeclaration/"></a>[references](references.md)| <a name="tech.antibytes.kmock/KMockContract.Verifier/references/#/PointingToDeclaration/"></a> [common] abstract val [references](references.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[KMockContract.Reference](../-reference/index.md)>Holds the actual references   <br>|


## Inheritors

|  Name |
|---|
| <a name="tech.antibytes.kmock.verification/NonfreezingVerifier///PointingToDeclaration/"></a>[NonfreezingVerifier](../../../tech.antibytes.kmock.verification/-nonfreezing-verifier/index.md)|
| <a name="tech.antibytes.kmock.verification/Verifier///PointingToDeclaration/"></a>[Verifier](../../../tech.antibytes.kmock.verification/-verifier/index.md)|


## Extensions

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock.verification//verifyOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>[verifyOrder](../../../tech.antibytes.kmock.verification/verify-order.md)| <a name="tech.antibytes.kmock.verification//verifyOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>[common]  <br>Content  <br>fun [KMockContract.Verifier](index.md).[verifyOrder](../../../tech.antibytes.kmock.verification/verify-order.md)(scope: [KMockContract.VerificationInsurance](../-verification-insurance/index.md).() -> [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))  <br>More info  <br>Verifies a chain VerificationHandles.  <br><br><br>|
| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>[verifyStrictOrder](../../../tech.antibytes.kmock.verification/verify-strict-order.md)| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>[common]  <br>Content  <br>fun [KMockContract.Verifier](index.md).[verifyStrictOrder](../../../tech.antibytes.kmock.verification/verify-strict-order.md)(scope: [KMockContract.VerificationInsurance](../-verification-insurance/index.md).() -> [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))  <br>More info  <br>Verifies a chain VerificationHandles.  <br><br><br>|
