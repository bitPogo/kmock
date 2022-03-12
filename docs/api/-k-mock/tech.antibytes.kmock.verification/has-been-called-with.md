//[KMock](../../index.md)/[tech.antibytes.kmock.verification](index.md)/[hasBeenCalledWith](has-been-called-with.md)



# hasBeenCalledWith
[common]
Content
fun [KMockContract.FunProxy](../tech.antibytes.kmock/-k-mock-contract/-fun-proxy/index.md)<*, *>.[hasBeenCalledWith](has-been-called-with.md)(vararg arguments: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [VerificationHandle](-verification-handle/index.md)
More info


VerificationHandle Factory, which collects all invocation of an FunProxy which matches the given Arguments.



#### Return


VerificationHandle



#### Author


Matthias Geisler



## See also

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//hasBeenCalledWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>[tech.antibytes.kmock.KMockContract.VerificationConstraint](../tech.antibytes.kmock/-k-mock-contract/-verification-constraint/index.md)| <a name="tech.antibytes.kmock.verification//hasBeenCalledWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>|



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//hasBeenCalledWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>arguments| <a name="tech.antibytes.kmock.verification//hasBeenCalledWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a><br><br>or constraints which calls must match. The arguments/constraints must follow the order of the mocked/stubbed function but can contain gaps and do not need to all arguments.<br><br>|