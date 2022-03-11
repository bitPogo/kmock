//[KMock](../../index.md)/[tech.antibytes.kmock.verification](index.md)/[assertHasBeenCalledStrictlyWith](assert-has-been-called-strictly-with.md)



# assertHasBeenCalledStrictlyWith
[common]
Content
fun [KMockContract.FunProxy](../tech.antibytes.kmock/-k-mock-contract/-fun-proxy/index.md)<*, *>.[assertHasBeenCalledStrictlyWith](assert-has-been-called-strictly-with.md)(exactly: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), vararg arguments: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?)
More info


Asserts that invocations of a FunProxy with the given arguments happened.



#### Author


Matthias Geisler



## See also

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>[tech.antibytes.kmock.KMockContract.FunProxy](../tech.antibytes.kmock/-k-mock-contract/-fun-proxy/index.md)| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>[verify](verify.md)| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>[tech.antibytes.kmock.KMockContract.VerificationConstraint](../tech.antibytes.kmock/-k-mock-contract/-verification-constraint/index.md)| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>|



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>exactly| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a><br><br>how often the Proxy was invoked.<br><br>|
| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>arguments| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a><br><br>or constraints which follow the order of the mocked/stubbed function and need to contain all arguments/constraints.<br><br>|



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>[kotlin.AssertionError](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-assertion-error/index.html)| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWith/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a><br><br>if the amount of calls does not match the expected amount.<br><br>|
