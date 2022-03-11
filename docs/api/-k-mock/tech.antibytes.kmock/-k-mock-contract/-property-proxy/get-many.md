//[KMock](../../../../index.md)/[tech.antibytes.kmock](../../index.md)/[KMockContract](../index.md)/[PropertyProxy](index.md)/[getMany](get-many.md)



# getMany
[common]
Content
abstract var [getMany](get-many.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Value](index.md)>
More info


Setter/Getter in order to set/get a List of Values of the property. If the given List has a smaller size than the actual invocation the last value of the list is used for any further invocation.



#### Throws

| | |
|---|---|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/getMany/#/PointingToDeclaration/"></a>[kotlin.NullPointerException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-null-pointer-exception/index.html)| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/getMany/#/PointingToDeclaration/"></a><br><br>on get if no value was set.<br><br>|
| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/getMany/#/PointingToDeclaration/"></a>[tech.antibytes.kmock.error.MockError.MissingStub](../../../tech.antibytes.kmock.error/-mock-error/-missing-stub/index.md)| <a name="tech.antibytes.kmock/KMockContract.PropertyProxy/getMany/#/PointingToDeclaration/"></a><br><br>if the given List is empty.<br><br>|
