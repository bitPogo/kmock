//[KMock](../../../../index.md)/[tech.antibytes.kmock](../../index.md)/[KMockContract](../index.md)/[VerificationHandle](index.md)



# VerificationHandle
 [common] interface [VerificationHandle](index.md)

Handle with the aggregated information of a Proxy invocation. Meant for internal usage only!



#### Author


Matthias Geisler




## Properties

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.VerificationHandle/callIndices/#/PointingToDeclaration/"></a>[callIndices](call-indices.md)| <a name="tech.antibytes.kmock/KMockContract.VerificationHandle/callIndices/#/PointingToDeclaration/"></a> [common] abstract val [callIndices](call-indices.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)>List with aggregated indices of invocation of the refered Proxy.   <br>|
| <a name="tech.antibytes.kmock/KMockContract.VerificationHandle/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="tech.antibytes.kmock/KMockContract.VerificationHandle/id/#/PointingToDeclaration/"></a> [common] abstract val [id](id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)Id of the Proxy.   <br>|


## Inheritors

|  Name |
|---|
| <a name="tech.antibytes.kmock.verification/VerificationHandle///PointingToDeclaration/"></a>[VerificationHandle](../../../tech.antibytes.kmock.verification/-verification-handle/index.md)|


## Extensions

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock.verification//and/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[and](../../../tech.antibytes.kmock.verification/and.md)| <a name="tech.antibytes.kmock.verification//and/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[common]  <br>Content  <br>infix fun [KMockContract.VerificationHandle](index.md).[and](../../../tech.antibytes.kmock.verification/and.md)(other: [KMockContract.VerificationHandle](index.md)): [KMockContract.VerificationHandle](index.md)  <br>More info  <br>Alias of intersect  <br><br><br>|
| <a name="tech.antibytes.kmock.verification//diff/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[diff](../../../tech.antibytes.kmock.verification/diff.md)| <a name="tech.antibytes.kmock.verification//diff/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[common]  <br>Content  <br>infix fun [KMockContract.VerificationHandle](index.md).[diff](../../../tech.antibytes.kmock.verification/diff.md)(other: [KMockContract.VerificationHandle](index.md)): [KMockContract.VerificationHandle](index.md)  <br>More info  <br>Operator to determine the symmetrical difference of 2 VerificationHandles call indices.  <br><br><br>|
| <a name="tech.antibytes.kmock.verification//intersection/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[intersection](../../../tech.antibytes.kmock.verification/intersection.md)| <a name="tech.antibytes.kmock.verification//intersection/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[common]  <br>Content  <br>infix fun [KMockContract.VerificationHandle](index.md).[intersection](../../../tech.antibytes.kmock.verification/intersection.md)(other: [KMockContract.VerificationHandle](index.md)): [KMockContract.VerificationHandle](index.md)  <br>More info  <br>Operator to determine the intersection of 2 VerificationHandles call indices.  <br><br><br>|
| <a name="tech.antibytes.kmock.verification//or/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[or](../../../tech.antibytes.kmock.verification/or.md)| <a name="tech.antibytes.kmock.verification//or/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[common]  <br>Content  <br>infix fun [KMockContract.VerificationHandle](index.md).[or](../../../tech.antibytes.kmock.verification/or.md)(other: [KMockContract.VerificationHandle](index.md)): [KMockContract.VerificationHandle](index.md)  <br>More info  <br>Alias of union  <br><br><br>|
| <a name="tech.antibytes.kmock.verification//union/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[union](../../../tech.antibytes.kmock.verification/union.md)| <a name="tech.antibytes.kmock.verification//union/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[common]  <br>Content  <br>infix fun [KMockContract.VerificationHandle](index.md).[union](../../../tech.antibytes.kmock.verification/union.md)(other: [KMockContract.VerificationHandle](index.md)): [KMockContract.VerificationHandle](index.md)  <br>More info  <br>Operator to determine the union of 2 VerificationHandles call indices.  <br><br><br>|
| <a name="tech.antibytes.kmock.verification//xor/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[xor](../../../tech.antibytes.kmock.verification/xor.md)| <a name="tech.antibytes.kmock.verification//xor/tech.antibytes.kmock.KMockContract.VerificationHandle#tech.antibytes.kmock.KMockContract.VerificationHandle/PointingToDeclaration/"></a>[common]  <br>Content  <br>infix fun [KMockContract.VerificationHandle](index.md).[xor](../../../tech.antibytes.kmock.verification/xor.md)(other: [KMockContract.VerificationHandle](index.md)): [KMockContract.VerificationHandle](index.md)  <br>More info  <br>Alias of diff  <br><br><br>|
