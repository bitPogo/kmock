//[KMock](../../../../index.md)/[tech.antibytes.kmock](../../index.md)/[KMockContract](../index.md)/[SideEffectChainBuilder](index.md)



# SideEffectChainBuilder
 [common] interface [SideEffectChainBuilder](index.md)<[ReturnValue](index.md), [SideEffect](index.md) : [Function](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-function/index.html)<[ReturnValue](index.md)>>

Builder for chained SideEffects.



#### Author


Matthias Geisler




## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.SideEffectChainBuilder///PointingToDeclaration/"></a>ReturnValue| <a name="tech.antibytes.kmock/KMockContract.SideEffectChainBuilder///PointingToDeclaration/"></a><br><br>the return value of the hosting Proxy.<br><br>|
| <a name="tech.antibytes.kmock/KMockContract.SideEffectChainBuilder///PointingToDeclaration/"></a>SideEffect| <a name="tech.antibytes.kmock/KMockContract.SideEffectChainBuilder///PointingToDeclaration/"></a><br><br>the function signature of the hosting Proxy.<br><br>|



## Functions

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.SideEffectChainBuilder/add/#TypeParam(bounds=[kotlin.Function[TypeParam(bounds=[kotlin.Any?])]])/PointingToDeclaration/"></a>[add](add.md)| <a name="tech.antibytes.kmock/KMockContract.SideEffectChainBuilder/add/#TypeParam(bounds=[kotlin.Function[TypeParam(bounds=[kotlin.Any?])]])/PointingToDeclaration/"></a>[common]  <br>Content  <br>abstract fun [add](add.md)(sideEffect: [SideEffect](index.md)): [KMockContract.SideEffectChainBuilder](index.md)<[ReturnValue](index.md), [SideEffect](index.md)>  <br>More info  <br>Adds a SideEffect to chain.  <br><br><br>|
| <a name="tech.antibytes.kmock/KMockContract.SideEffectChainBuilder/addAll/#kotlin.collections.Iterable[TypeParam(bounds=[kotlin.Function[TypeParam(bounds=[kotlin.Any?])]])]/PointingToDeclaration/"></a>[addAll](add-all.md)| <a name="tech.antibytes.kmock/KMockContract.SideEffectChainBuilder/addAll/#kotlin.collections.Iterable[TypeParam(bounds=[kotlin.Function[TypeParam(bounds=[kotlin.Any?])]])]/PointingToDeclaration/"></a>[common]  <br>Content  <br>abstract fun [addAll](add-all.md)(sideEffect: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)<[SideEffect](index.md)>): [KMockContract.SideEffectChainBuilder](index.md)<[ReturnValue](index.md), [SideEffect](index.md)>  <br>More info  <br>Adds a multiple SideEffects to chain.  <br><br><br>|
