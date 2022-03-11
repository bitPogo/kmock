//[KMock](../../../../index.md)/[tech.antibytes.kmock](../../index.md)/[KMockContract](../index.md)/[VerificationInsurance](index.md)/[ensureVerificationOf](ensure-verification-of.md)



# ensureVerificationOf
[common]
Content
abstract fun [ensureVerificationOf](ensure-verification-of.md)(vararg proxies: [KMockContract.Proxy](../-proxy/index.md)<*, *>)
More info


Ensures that given Proxies are covered by the VerificationChain.



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.VerificationInsurance/ensureVerificationOf/#kotlin.Array[tech.antibytes.kmock.KMockContract.Proxy[*,*]]/PointingToDeclaration/"></a>[kotlin.IllegalStateException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-illegal-state-exception/index.html)| <a name="tech.antibytes.kmock/KMockContract.VerificationInsurance/ensureVerificationOf/#kotlin.Array[tech.antibytes.kmock.KMockContract.Proxy[*,*]]/PointingToDeclaration/"></a><br><br>if a given Proxy is not covered by a VerificationChain.<br><br>|
