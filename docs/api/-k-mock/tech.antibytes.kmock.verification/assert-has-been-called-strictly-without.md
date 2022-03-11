//[KMock](../../index.md)/[tech.antibytes.kmock.verification](index.md)/[assertHasBeenCalledStrictlyWithout](assert-has-been-called-strictly-without.md)



# assertHasBeenCalledStrictlyWithout
[common]
Content
fun [KMockContract.FunProxy](../tech.antibytes.kmock/-k-mock-contract/-fun-proxy/index.md)<*, *>.[assertHasBeenCalledStrictlyWithout](assert-has-been-called-strictly-without.md)(vararg illegal: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?)
More info


Asserts that invocations of a FunProxy did not happened with given parameter.



#### Author


Matthias Geisler



## See also

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWithout/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>[tech.antibytes.kmock.KMockContract.FunProxy](../tech.antibytes.kmock/-k-mock-contract/-fun-proxy/index.md)| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWithout/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWithout/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>[verify](verify.md)| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWithout/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWithout/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>[tech.antibytes.kmock.KMockContract.VerificationConstraint](../tech.antibytes.kmock/-k-mock-contract/-verification-constraint/index.md)| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWithout/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>|



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWithout/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>illegal| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWithout/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a><br><br>arguments/constraints which follow the order of the mocked/stubbed function but can contain gaps and do not need to all arguments/constraints.<br><br>|



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWithout/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a>[kotlin.AssertionError](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-assertion-error/index.html)| <a name="tech.antibytes.kmock.verification//assertHasBeenCalledStrictlyWithout/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Array[kotlin.Any?]/PointingToDeclaration/"></a><br><br>if the amount of calls does not match the expected amount.<br><br>|
