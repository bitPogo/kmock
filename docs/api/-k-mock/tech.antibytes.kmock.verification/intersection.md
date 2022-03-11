//[KMock](../../index.md)/[tech.antibytes.kmock.verification](index.md)/[intersection](intersection.md)



# intersection
[common]
Content
infix fun [KMockContract.VerificationHandle](../tech.antibytes.kmock/-k-mock-contract/-verification-handle/index.md).[intersection](intersection.md)(other: [KMockContract.VerificationHandle](../tech.antibytes.kmock/-k-mock-contract/-verification-handle/index.md)): [KMockContract.VerificationHandle](../tech.antibytes.kmock/-k-mock-contract/-verification-handle/index.md)
More info


Operator to determine the intersection of 2 VerificationHandles call indices. Both handles must be refer to same Proxy.



#### Return


VerificationHandle which contains the intersection of both given call indices.



#### Author


Matthias Geisler



## See also

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//intersection/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[tech.antibytes.kmock.KMockContract.VerificationHandle](../tech.antibytes.kmock/-k-mock-contract/-verification-handle/index.md)| <a name="tech.antibytes.kmock.verification//intersection/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>|



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//intersection/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>other| <a name="tech.antibytes.kmock.verification//intersection/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a><br><br>2nd handle.<br><br>|



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//intersection/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[kotlin.IllegalArgumentException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-illegal-argument-exception/index.html)| <a name="tech.antibytes.kmock.verification//intersection/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a><br><br>if the 2nd handle does not refer to the same proxy.<br><br>|
