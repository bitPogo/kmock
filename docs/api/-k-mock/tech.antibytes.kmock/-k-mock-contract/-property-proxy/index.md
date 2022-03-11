//[KMock](../../../../index.md)/[tech.antibytes.kmock](../../index.md)/[KMockContract](../index.md)/[PropertyProxy](index.md)



# PropertyProxy
 [common] interface [PropertyProxy](index.md)<[Value](index.md)> : [KMockContract.Proxy](../-proxy/index.md)<[Value](index.md), [KMockContract.GetOrSet](../-get-or-set/index.md)>

Proxy in order to stub/mock property behaviour.



#### Author


Matthias Geisler




## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy///PointingToDeclaration/"></a>Value| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy///PointingToDeclaration/"></a><br><br>the value of the Property.<br><br>|



## Functions

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.Proxy/clear/#/PointingToDeclaration/"></a>[clear](../-proxy/clear.md)| <a name="tech.antibytes.kmock/KMockContract.Proxy/clear/#/PointingToDeclaration/"></a>[common]  <br>Content  <br>abstract fun [clear](../-proxy/clear.md)()  <br>More info  <br>Clears the Proxies captured arguments  <br><br><br>|
| <a name="tech.antibytes.kmock/KMockContract.Proxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a>[getArgumentsForCall](../-proxy/get-arguments-for-call.md)| <a name="tech.antibytes.kmock/KMockContract.Proxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a>[common]  <br>Content  <br>abstract fun [getArgumentsForCall](../-proxy/get-arguments-for-call.md)(callIndex: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [KMockContract.GetOrSet](../-get-or-set/index.md)  <br>More info  <br>Resolves given arguments of an invocation.  <br><br><br>|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/onGet/#/PointingToDeclaration/"></a>[onGet](on-get.md)| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/onGet/#/PointingToDeclaration/"></a>[common]  <br>Content  <br>abstract fun [onGet](on-get.md)(): [Value](index.md)  <br>More info  <br>Invocation of property getter.  <br><br><br>|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/onSet/#TypeParam(bounds=[kotlin.Any?])/PointingToDeclaration/"></a>[onSet](on-set.md)| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/onSet/#TypeParam(bounds=[kotlin.Any?])/PointingToDeclaration/"></a>[common]  <br>Content  <br>abstract fun [onSet](on-set.md)(value: [Value](index.md))  <br>More info  <br>Invocation of property setter.  <br><br><br>|


## Properties

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/calls/#/PointingToDeclaration/"></a>[calls](index.md#-194997252%2FProperties%2F-34120600)| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/calls/#/PointingToDeclaration/"></a> [common] abstract val [calls](index.md#-194997252%2FProperties%2F-34120600): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)Counter of the actual invocations of the proxy.   <br>|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/get/#/PointingToDeclaration/"></a>[get](get.md)| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/get/#/PointingToDeclaration/"></a> [common] abstract var [get](get.md): [Value](index.md)Setter/Getter in order to set/get constant Value of the property.   <br>|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/getMany/#/PointingToDeclaration/"></a>[getMany](get-many.md)| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/getMany/#/PointingToDeclaration/"></a> [common] abstract var [getMany](get-many.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Value](index.md)>Setter/Getter in order to set/get a List of Values of the property.   <br>|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/getSideEffect/#/PointingToDeclaration/"></a>[getSideEffect](get-side-effect.md)| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/getSideEffect/#/PointingToDeclaration/"></a> [common] abstract var [getSideEffect](get-side-effect.md): () -> [Value](index.md)Setter/Getter in order to set/get custom SideEffect for the properties getter.   <br>|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/id/#/PointingToDeclaration/"></a>[id](index.md#-1220184306%2FProperties%2F-34120600)| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/id/#/PointingToDeclaration/"></a> [common] abstract val [id](index.md#-1220184306%2FProperties%2F-34120600): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)Unique Id of the proxy derived from the Interface which it build upon.   <br>|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/set/#/PointingToDeclaration/"></a>[set](set.md)| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/set/#/PointingToDeclaration/"></a> [common] abstract var [set](set.md): ([Value](index.md)) -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)Setter/Getter in order to set/get custom SideEffect for the properties setter.   <br>|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/verificationBuilderReference/#/PointingToDeclaration/"></a>[verificationBuilderReference](index.md#-1981428482%2FProperties%2F-34120600)| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/verificationBuilderReference/#/PointingToDeclaration/"></a> [common] abstract var [verificationBuilderReference](index.md#-1981428482%2FProperties%2F-34120600): [KMockContract.VerificationChainBuilder](../-verification-chain-builder/index.md)?Reference to its correspondent VerificationChain.   <br>|


## Inheritors

|  Name |
|---|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a>[PropertyProxy](../../../tech.antibytes.kmock.proxy/-property-proxy/index.md)|


## Extensions

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock.verification//assertWasGotten/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int/PointingToDeclaration/"></a>[assertWasGotten](../../../tech.antibytes.kmock.verification/assert-was-gotten.md)| <a name="tech.antibytes.kmock.verification//assertWasGotten/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int/PointingToDeclaration/"></a>[common]  <br>Content  <br>fun [KMockContract.PropertyProxy](index.md)<*>.[assertWasGotten](../../../tech.antibytes.kmock.verification/assert-was-gotten.md)(exactly: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))  <br>More info  <br>Asserts that the getter of a PropertyProxy was invoked.  <br><br><br>|
| <a name="tech.antibytes.kmock.verification//assertWasSet/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int/PointingToDeclaration/"></a>[assertWasSet](../../../tech.antibytes.kmock.verification/assert-was-set.md)| <a name="tech.antibytes.kmock.verification//assertWasSet/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int/PointingToDeclaration/"></a>[common]  <br>Content  <br>fun [KMockContract.PropertyProxy](index.md)<*>.[assertWasSet](../../../tech.antibytes.kmock.verification/assert-was-set.md)(exactly: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))  <br>More info  <br>Asserts that the setter of a PropertyProxy was invoked.  <br><br><br>|
| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a>[assertWasSetTo](../../../tech.antibytes.kmock.verification/assert-was-set-to.md)| <a name="tech.antibytes.kmock.verification//assertWasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Int#kotlin.Any?/PointingToDeclaration/"></a>[common]  <br>Content  <br>fun [KMockContract.PropertyProxy](index.md)<*>.[assertWasSetTo](../../../tech.antibytes.kmock.verification/assert-was-set-to.md)(exactly: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?)  <br>More info  <br>Asserts that the setter of a PropertyProxy was invoked with the given value.  <br><br><br>|
| <a name="tech.antibytes.kmock.verification//wasGotten/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#/PointingToDeclaration/"></a>[wasGotten](../../../tech.antibytes.kmock.verification/was-gotten.md)| <a name="tech.antibytes.kmock.verification//wasGotten/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#/PointingToDeclaration/"></a>[common]  <br>Content  <br>fun [KMockContract.PropertyProxy](index.md)<*>.[wasGotten](../../../tech.antibytes.kmock.verification/was-gotten.md)(): [VerificationHandle](../../../tech.antibytes.kmock.verification/-verification-handle/index.md)  <br>More info  <br>VerificationHandle Factory, which collects all invocation of an PropertyProxy Getter.  <br><br><br>|
| <a name="tech.antibytes.kmock.verification//wasSet/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#/PointingToDeclaration/"></a>[wasSet](../../../tech.antibytes.kmock.verification/was-set.md)| <a name="tech.antibytes.kmock.verification//wasSet/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#/PointingToDeclaration/"></a>[common]  <br>Content  <br>fun [KMockContract.PropertyProxy](index.md)<*>.[wasSet](../../../tech.antibytes.kmock.verification/was-set.md)(): [VerificationHandle](../../../tech.antibytes.kmock.verification/-verification-handle/index.md)  <br>More info  <br>VerificationHandle Factory, which collects all invocation of an PropertyProxy Setter.  <br><br><br>|
| <a name="tech.antibytes.kmock.verification//wasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Any?/PointingToDeclaration/"></a>[wasSetTo](../../../tech.antibytes.kmock.verification/was-set-to.md)| <a name="tech.antibytes.kmock.verification//wasSetTo/tech.antibytes.kmock.KMockContract.PropertyProxy[*]#kotlin.Any?/PointingToDeclaration/"></a>[common]  <br>Content  <br>fun [KMockContract.PropertyProxy](index.md)<*>.[wasSetTo](../../../tech.antibytes.kmock.verification/was-set-to.md)(value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [VerificationHandle](../../../tech.antibytes.kmock.verification/-verification-handle/index.md)  <br>More info  <br>VerificationHandle Factory, which collects all invocation of an PropertyProxy Setter with the given Value.  <br><br><br>|
