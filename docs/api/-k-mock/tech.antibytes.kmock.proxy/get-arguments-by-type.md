//[KMock](../../index.md)/[tech.antibytes.kmock.proxy](index.md)/[getArgumentsByType](get-arguments-by-type.md)



# getArgumentsByType
[common]
Content
inline fun <[T](get-arguments-by-type.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [KMockContract.FunProxy](../tech.antibytes.kmock/-k-mock-contract/-fun-proxy/index.md)<*, *>.[getArgumentsByType](get-arguments-by-type.md)(callIndex: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[T](get-arguments-by-type.md)>
More info


Retrieves arguments of a call at a given index for a given type of FunProxy.



#### Return


List of T



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.proxy//getArgumentsByType/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int/PointingToDeclaration/"></a>T| <a name="tech.antibytes.kmock.proxy//getArgumentsByType/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int/PointingToDeclaration/"></a><br><br>the type to look out for.<br><br>|
| <a name="tech.antibytes.kmock.proxy//getArgumentsByType/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int/PointingToDeclaration/"></a>callIndex| <a name="tech.antibytes.kmock.proxy//getArgumentsByType/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int/PointingToDeclaration/"></a><br><br>the index to look at.<br><br>|



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock.proxy//getArgumentsByType/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int/PointingToDeclaration/"></a>[tech.antibytes.kmock.error.MockError.MissingCall](../tech.antibytes.kmock.error/-mock-error/-missing-call/index.md)| <a name="tech.antibytes.kmock.proxy//getArgumentsByType/tech.antibytes.kmock.KMockContract.FunProxy[*,*]#kotlin.Int/PointingToDeclaration/"></a><br><br>if there is no call recorded at given index<br><br>|
