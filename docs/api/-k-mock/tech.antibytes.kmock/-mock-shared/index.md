//[KMock](../../../index.md)/[tech.antibytes.kmock](../index.md)/[MockShared](index.md)



# MockShared
 [common] @[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.CLASS](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-c-l-a-s-s/index.html)])

annotation class [MockShared](index.md)(**sourceSetName**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **interfaces**: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)<*>)

Determines which interfaces should be stubbed/mocked for a shared source.




## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock/MockShared///PointingToDeclaration/"></a>sourceSetName| <a name="tech.antibytes.kmock/MockShared///PointingToDeclaration/"></a><br><br>to bind the given interface to a sourceSet (e.g. nativeTest).<br><br>|
| <a name="tech.antibytes.kmock/MockShared///PointingToDeclaration/"></a>interfaces| <a name="tech.antibytes.kmock/MockShared///PointingToDeclaration/"></a><br><br>variable amount of interfaces.<br><br>|



## Constructors

| | |
|---|---|
| <a name="tech.antibytes.kmock/MockShared/MockShared/#kotlin.String#kotlin.Array[kotlin.reflect.KClass[*]]/PointingToDeclaration/"></a>[MockShared](-mock-shared.md)| <a name="tech.antibytes.kmock/MockShared/MockShared/#kotlin.String#kotlin.Array[kotlin.reflect.KClass[*]]/PointingToDeclaration/"></a> [common] fun [MockShared](-mock-shared.md)(sourceSetName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), vararg interfaces: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)<*>)to bind the given interface to a sourceSet (e.g.   <br>|


## Properties

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock/MockShared/interfaces/#/PointingToDeclaration/"></a>[interfaces](interfaces.md)| <a name="tech.antibytes.kmock/MockShared/interfaces/#/PointingToDeclaration/"></a> [common] val [interfaces](interfaces.md): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<out [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)<*>>   <br>|
| <a name="tech.antibytes.kmock/MockShared/sourceSetName/#/PointingToDeclaration/"></a>[sourceSetName](source-set-name.md)| <a name="tech.antibytes.kmock/MockShared/sourceSetName/#/PointingToDeclaration/"></a> [common] val [sourceSetName](source-set-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>|
