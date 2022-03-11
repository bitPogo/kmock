//[KMock](../../index.md)/[tech.antibytes.kmock.verification](index.md)/[verifyStrictOrder](verify-strict-order.md)



# verifyStrictOrder
[common]
Content
fun [KMockContract.Verifier](../tech.antibytes.kmock/-k-mock-contract/-verifier/index.md).[verifyStrictOrder](verify-strict-order.md)(scope: [KMockContract.VerificationInsurance](../tech.antibytes.kmock/-k-mock-contract/-verification-insurance/index.md).() -> [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))
More info


Verifies a chain VerificationHandles. Each Handle must be in strict order of the referenced Proxy invocation and all invocations must be present.



#### Author


Matthias Geisler



## See also

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>[hasBeenCalled](has-been-called.md)| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>hasBeenCalledWith| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>hasBeenStrictlyCalledWith| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>hasBeenCalledWithout| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>wasGotten| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>wasSet| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>wasSetTo| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>|



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>scope| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a><br><br>chain of VerificationHandle factory.<br><br>|



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a>[kotlin.AssertionError](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-assertion-error/index.html)| <a name="tech.antibytes.kmock.verification//verifyStrictOrder/tech.antibytes.kmock.KMockContract.Verifier#kotlin.Function1[tech.antibytes.kmock.KMockContract.VerificationInsurance,kotlin.Any]/PointingToDeclaration/"></a><br><br>if given criteria are not met.<br><br>|
