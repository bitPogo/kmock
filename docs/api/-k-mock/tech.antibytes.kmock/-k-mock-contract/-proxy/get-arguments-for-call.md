//[KMock](../../../../index.md)/[tech.antibytes.kmock](../../index.md)/[KMockContract](../index.md)/[Proxy](index.md)/[getArgumentsForCall](get-arguments-for-call.md)



# getArgumentsForCall
[common]
Content
abstract fun [getArgumentsForCall](get-arguments-for-call.md)(callIndex: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Arguments](index.md)
More info


Resolves given arguments of an invocation.



#### Return


the Arguments of the given invocation or null if the proxy is used for void invocations.



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.Proxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a>callIndex| <a name="tech.antibytes.kmock/KMockContract.Proxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a><br><br>index of an invocation.<br><br>|



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.Proxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a>[tech.antibytes.kmock.error.MockError.MissingCall](../../../tech.antibytes.kmock.error/-mock-error/-missing-call/index.md)| <a name="tech.antibytes.kmock/KMockContract.Proxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a><br><br>if the callIndex is invalid.<br><br>|
