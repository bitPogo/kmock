//[KMock](../../index.md)/[tech.antibytes.kmock.verification](index.md)/[assertWasSetTo](assert-was-set-to.md)



# assertWasSetTo
[common]
Content
fun [KMockContract.PropertyProxy](../tech.antibytes.kmock/-k-mock-contract/-property-proxy/index.md)<*>.[assertWasSetTo](assert-was-set-to.md)(exactly: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?)
More info


Asserts that the setter of a PropertyProxy was invoked with the given value.



#### Author


Matthias Geisler



## See also

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a>[tech.antibytes.kmock.KMockContract.FunProxy](../tech.antibytes.kmock/-k-mock-contract/-fun-proxy/index.md)| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a>[verify](verify.md)| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a>[tech.antibytes.kmock.KMockContract.VerificationConstraint](../tech.antibytes.kmock/-k-mock-contract/-verification-constraint/index.md)| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a>|



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a>exactly| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a><br><br>how often the Proxy was invoked.<br><br>|
| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a>value| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a><br><br>or constraint of the expected value.<br><br>|



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a>[kotlin.AssertionError](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-assertion-error/index.html)| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a><br><br>if at least one call contains a given argument.<br><br>|
