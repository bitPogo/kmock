//[KMock](../../../index.md)/[tech.antibytes.kmock.proxy](../index.md)/[PropertyProxy](index.md)/[getArgumentsForCall](get-arguments-for-call.md)



# getArgumentsForCall
[common]
Content
open override fun [getArgumentsForCall](get-arguments-for-call.md)(callIndex: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [KMockContract.GetOrSet](../../tech.antibytes.kmock/-k-mock-contract/-get-or-set/index.md)
More info


Resolves given arguments of an invocation.



#### Return


the Arguments of the given invocation or null if the proxy is used for void invocations.



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a>callIndex| <a name="tech.antibytes.kmock.proxy/PropertyProxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a><br><br>index of an invocation.<br><br>|



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a>MissingCall| <a name="tech.antibytes.kmock.proxy/PropertyProxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a><br><br>if the callIndex is invalid.<br><br>|
