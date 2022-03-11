//[KMock](../../index.md)/[tech.antibytes.kmock.verification](index.md)/[verify](verify.md)



# verify
[common]
Content
fun [verify](verify.md)(exactly: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)? = null, atLeast: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 1, atMost: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)? = null, action: () -> [KMockContract.VerificationHandle](../tech.antibytes.kmock/-k-mock-contract/-verification-handle/index.md))
More info


Verifies the last produced VerificationHandle.



#### Author


Matthias Geisler



## See also

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>[hasBeenCalled](has-been-called.md)| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>hasBeenCalledWith| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>hasBeenStrictlyCalledWith| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>hasBeenCalledWithout| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>wasGotten| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>wasSet| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>wasSetTo| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>|



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>exactly| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a><br><br>optional parameter which indicates the exact amount of calls. This parameter overrides atLeast and atMost.<br><br>|
| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>atLeast| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a><br><br>optional parameter which indicates the minimum amount of calls.<br><br>|
| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>atMost| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a><br><br>optional parameter which indicates the maximum amount of calls.<br><br>|
| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>action| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a><br><br>producer of VerificationHandle.<br><br>|



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a>[kotlin.AssertionError](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-assertion-error/index.html)| <a name="tech.antibytes.kmock.verification//verify/#kotlin.Int?#kotlin.Int#kotlin.Int?#kotlin.Function0[tech.antibytes.kmock.KMockContract.VerificationHandle]/PointingToDeclaration/"></a><br><br>if given criteria are not met.<br><br>|
