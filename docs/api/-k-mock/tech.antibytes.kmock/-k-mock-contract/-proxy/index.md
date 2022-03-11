//[KMock](../../../../index.md)/[tech.antibytes.kmock](../../index.md)/[KMockContract](../index.md)/[Proxy](index.md)



# Proxy
 [common] interface [Proxy](index.md)<[ReturnValue](index.md), [Arguments](index.md)>

Base Proxy definition



#### Author


Matthias Geisler




## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.Proxy///PointingToDeclaration/"></a>ReturnValue| <a name="tech.antibytes.kmock/KMockContract.Proxy///PointingToDeclaration/"></a><br><br>the return value of the Proxy.<br><br>|
| <a name="tech.antibytes.kmock/KMockContract.Proxy///PointingToDeclaration/"></a>Arguments| <a name="tech.antibytes.kmock/KMockContract.Proxy///PointingToDeclaration/"></a><br><br>the arguments which are delegated to the Proxy.<br><br>|



## Functions

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.Proxy/clear/#/PointingToDeclaration/"></a>[clear](clear.md)| <a name="tech.antibytes.kmock/KMockContract.Proxy/clear/#/PointingToDeclaration/"></a>[common]  <br>Content  <br>abstract fun [clear](clear.md)()  <br>More info  <br>Clears the Proxies captured arguments  <br><br><br>|
| <a name="tech.antibytes.kmock/KMockContract.Proxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a>[getArgumentsForCall](get-arguments-for-call.md)| <a name="tech.antibytes.kmock/KMockContract.Proxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a>[common]  <br>Content  <br>abstract fun [getArgumentsForCall](get-arguments-for-call.md)(callIndex: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Arguments](index.md)  <br>More info  <br>Resolves given arguments of an invocation.  <br><br><br>|


## Properties

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.Proxy/calls/#/PointingToDeclaration/"></a>[calls](calls.md)| <a name="tech.antibytes.kmock/KMockContract.Proxy/calls/#/PointingToDeclaration/"></a> [common] abstract val [calls](calls.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)Counter of the actual invocations of the proxy.   <br>|
| <a name="tech.antibytes.kmock/KMockContract.Proxy/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="tech.antibytes.kmock/KMockContract.Proxy/id/#/PointingToDeclaration/"></a> [common] abstract val [id](id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)Unique Id of the proxy derived from the Interface which it build upon.   <br>|
| <a name="tech.antibytes.kmock/KMockContract.Proxy/verificationBuilderReference/#/PointingToDeclaration/"></a>[verificationBuilderReference](verification-builder-reference.md)| <a name="tech.antibytes.kmock/KMockContract.Proxy/verificationBuilderReference/#/PointingToDeclaration/"></a> [common] abstract var [verificationBuilderReference](verification-builder-reference.md): [KMockContract.VerificationChainBuilder](../-verification-chain-builder/index.md)?Reference to its correspondent VerificationChain.   <br>|


## Inheritors

|  Name |
|---|
| <a name="tech.antibytes.kmock/KMockContract.FunProxy///PointingToDeclaration/"></a>[KMockContract](../-fun-proxy/index.md)|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy///PointingToDeclaration/"></a>[KMockContract](../-property-proxy/index.md)|
