//[KMock](../../../index.md)/[tech.antibytes.kmock.proxy](../index.md)/[PropertyProxy](index.md)



# PropertyProxy
 [common] class [PropertyProxy](index.md)<[Value](index.md)>(**id**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **collector**: [KMockContract.Collector](../../tech.antibytes.kmock/-k-mock-contract/-collector/index.md), **relaxer**: [KMockContract.Relaxer](../../tech.antibytes.kmock/-k-mock-contract/-relaxer/index.md)<[Value](index.md)>?, **freeze**: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), **spyOnGet**: () -> [Value](index.md)?, **spyOnSet**: ([Value](index.md)) -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)?) : [KMockContract.PropertyProxy](../../tech.antibytes.kmock/-k-mock-contract/-property-proxy/index.md)<[Value](index.md)>

Proxy in order to stub/mock property behaviour.



#### Author


Matthias Geisler




## See also

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a>[tech.antibytes.kmock.KMockContract.Collector](../../tech.antibytes.kmock/-k-mock-contract/-collector/index.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a>[tech.antibytes.kmock.KMockContract.Relaxer](../../tech.antibytes.kmock/-k-mock-contract/-relaxer/index.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a>|



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a>Value| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a><br><br>the value of the Property.<br><br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a>id| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a><br><br>a unique identifier for this Proxy.<br><br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a>collector| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a><br><br>a optional Collector for VerificationChains. Default is a NoopCollector.<br><br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a>relaxer| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a><br><br>a optional Relaxer for autogenerated values. Default is null.<br><br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a>freeze| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a><br><br>boolean which indicates if freezing can be used or not. Default is true.<br><br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a>spyOnGet| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a><br><br>a optional getter function reference which is wrapped by this proxy and will be invoked if given.<br><br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a>spyOnSet| <a name="tech.antibytes.kmock.proxy/PropertyProxy///PointingToDeclaration/"></a><br><br>a optional setter function reference which is wrapped by this proxy and will be invoked if given. Default is null.<br><br>|



## Constructors

| | |
|---|---|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/PropertyProxy/#kotlin.String#tech.antibytes.kmock.KMockContract.Collector#tech.antibytes.kmock.KMockContract.Relaxer[TypeParam(bounds=[kotlin.Any?])]?#kotlin.Boolean#kotlin.Function0[TypeParam(bounds=[kotlin.Any?])]?#kotlin.Function1[TypeParam(bounds=[kotlin.Any?]),kotlin.Unit]?/PointingToDeclaration/"></a>[PropertyProxy](-property-proxy.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy/PropertyProxy/#kotlin.String#tech.antibytes.kmock.KMockContract.Collector#tech.antibytes.kmock.KMockContract.Relaxer[TypeParam(bounds=[kotlin.Any?])]?#kotlin.Boolean#kotlin.Function0[TypeParam(bounds=[kotlin.Any?])]?#kotlin.Function1[TypeParam(bounds=[kotlin.Any?]),kotlin.Unit]?/PointingToDeclaration/"></a> [common] fun <[Value](index.md)> [PropertyProxy](-property-proxy.md)(id: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), collector: [KMockContract.Collector](../../tech.antibytes.kmock/-k-mock-contract/-collector/index.md) = NoopCollector, relaxer: [KMockContract.Relaxer](../../tech.antibytes.kmock/-k-mock-contract/-relaxer/index.md)<[Value](index.md)>? = null, freeze: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, spyOnGet: () -> [Value](index.md)? = null, spyOnSet: ([Value](index.md)) -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)? = null)Creates a PropertyProxy   <br>|


## Functions

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/clear/#/PointingToDeclaration/"></a>[clear](clear.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy/clear/#/PointingToDeclaration/"></a>[common]  <br>Content  <br>open override fun [clear](clear.md)()  <br>More info  <br>Clears the Proxies captured arguments  <br><br><br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a>[getArgumentsForCall](get-arguments-for-call.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy/getArgumentsForCall/#kotlin.Int/PointingToDeclaration/"></a>[common]  <br>Content  <br>open override fun [getArgumentsForCall](get-arguments-for-call.md)(callIndex: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [KMockContract.GetOrSet](../../tech.antibytes.kmock/-k-mock-contract/-get-or-set/index.md)  <br>More info  <br>Resolves given arguments of an invocation.  <br><br><br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/onGet/#/PointingToDeclaration/"></a>[onGet](on-get.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy/onGet/#/PointingToDeclaration/"></a>[common]  <br>Content  <br>open override fun [onGet](on-get.md)(): [Value](index.md)  <br>More info  <br>Invocation of property getter.  <br><br><br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/onSet/#TypeParam(bounds=[kotlin.Any?])/PointingToDeclaration/"></a>[onSet](on-set.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy/onSet/#TypeParam(bounds=[kotlin.Any?])/PointingToDeclaration/"></a>[common]  <br>Content  <br>open override fun [onSet](on-set.md)(value: [Value](index.md))  <br>More info  <br>Invocation of property setter.  <br><br><br>|


## Properties

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/calls/#/PointingToDeclaration/"></a>[calls](calls.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy/calls/#/PointingToDeclaration/"></a> [common] open override val [calls](calls.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)Counter of the actual invocations of the proxy.   <br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/get/#/PointingToDeclaration/"></a>[get](get.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy/get/#/PointingToDeclaration/"></a> [common] open override var [get](get.md): [Value](index.md)Setter/Getter in order to set/get constant Value of the property.   <br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/getMany/#/PointingToDeclaration/"></a>[getMany](get-many.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy/getMany/#/PointingToDeclaration/"></a> [common] open override var [getMany](get-many.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Value](index.md)>Setter/Getter in order to set/get a List of Values of the property.   <br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/getSideEffect/#/PointingToDeclaration/"></a>[getSideEffect](get-side-effect.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy/getSideEffect/#/PointingToDeclaration/"></a> [common] open override var [getSideEffect](get-side-effect.md): () -> [Value](index.md)Setter/Getter in order to set/get custom SideEffect for the properties getter.   <br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy/id/#/PointingToDeclaration/"></a> [common] open override val [id](id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)a unique identifier for this Proxy.   <br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/set/#/PointingToDeclaration/"></a>[set](set.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy/set/#/PointingToDeclaration/"></a> [common] open override var [set](set.md): ([Value](index.md)) -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)Setter/Getter in order to set/get custom SideEffect for the properties setter.   <br>|
| <a name="tech.antibytes.kmock.proxy/PropertyProxy/verificationBuilderReference/#/PointingToDeclaration/"></a>[verificationBuilderReference](verification-builder-reference.md)| <a name="tech.antibytes.kmock.proxy/PropertyProxy/verificationBuilderReference/#/PointingToDeclaration/"></a> [common] open override var [verificationBuilderReference](verification-builder-reference.md): [KMockContract.VerificationChainBuilder](../../tech.antibytes.kmock/-k-mock-contract/-verification-chain-builder/index.md)?Reference to its correspondent VerificationChain.   <br>|