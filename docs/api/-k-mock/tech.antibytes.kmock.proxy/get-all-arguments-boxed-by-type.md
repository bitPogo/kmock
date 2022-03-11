//[KMock](../../index.md)/[tech.antibytes.kmock.proxy](index.md)/[getAllArgumentsBoxedByType](get-all-arguments-boxed-by-type.md)



# getAllArgumentsBoxedByType
[common]
Content
inline fun <[T](get-all-arguments-boxed-by-type.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [KMockContract.FunProxy](../tech.antibytes.kmock/-k-mock-contract/-fun-proxy/index.md)<*, *>.[getAllArgumentsBoxedByType](get-all-arguments-boxed-by-type.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[T](get-all-arguments-boxed-by-type.md)>>
More info


Retrieves arguments for given type of FunProxy, while boxing them in sub lists for each call.



#### Return


List of List of T



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.proxy//getAllArgumentsBoxedByType/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#/PointingToDeclaration/"></a>T| <a name="tech.antibytes.kmock.proxy//getAllArgumentsBoxedByType/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#/PointingToDeclaration/"></a><br><br>the type to look out for.<br><br>|



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock.proxy//getAllArgumentsBoxedByType/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#/PointingToDeclaration/"></a>[tech.antibytes.kmock.error.MockError.MissingCall](../tech.antibytes.kmock.error/-mock-error/-missing-call/index.md)| <a name="tech.antibytes.kmock.proxy//getAllArgumentsBoxedByType/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#/PointingToDeclaration/"></a><br><br>if there is no call recorded at given index<br><br>|
