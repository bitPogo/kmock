//[KMock](../../../../index.md)/[tech.antibytes.kmock](../../index.md)/[KMockContract](../index.md)/[FunProxy](index.md)/[returnValues](return-values.md)



# returnValues
[common]
Content
abstract var [returnValues](return-values.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ReturnValue](index.md)>
More info


Setter/Getter in order to set/get a List of ReturnValues of the function. If the given List has a smaller size than the actual invocation the last value of the list is used for any further invocation.



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.FunProxy/returnValues/#/PointingToDeclaration/"></a>[kotlin.NullPointerException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-null-pointer-exception/index.html)| <a name="tech.antibytes.kmock/KMockContract.FunProxy/returnValues/#/PointingToDeclaration/"></a><br><br>on get if no value was set.<br><br>|
| <a name="tech.antibytes.kmock/KMockContract.FunProxy/returnValues/#/PointingToDeclaration/"></a>[tech.antibytes.kmock.error.MockError.MissingStub](../../../tech.antibytes.kmock.error/-mock-error/-missing-stub/index.md)| <a name="tech.antibytes.kmock/KMockContract.FunProxy/returnValues/#/PointingToDeclaration/"></a><br><br>if the given List is empty.<br><br>|
