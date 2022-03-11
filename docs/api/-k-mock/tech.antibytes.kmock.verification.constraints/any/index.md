//[KMock](../../../index.md)/[tech.antibytes.kmock.verification.constraints](../index.md)/[any](index.md)



# any
 [common] class [any](index.md)(**expected**: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)<*>?) : [KMockContract.VerificationConstraint](../../tech.antibytes.kmock/-k-mock-contract/-verification-constraint/index.md)

VerificationConstraint which allows any value including null.



#### Author


Matthias Geisler




## See also

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification.constraints/any///PointingToDeclaration/"></a>[tech.antibytes.kmock.KMockContract.VerificationConstraint](../../tech.antibytes.kmock/-k-mock-contract/-verification-constraint/index.md)| <a name="tech.antibytes.kmock.verification.constraints/any///PointingToDeclaration/"></a>|



## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification.constraints/any///PointingToDeclaration/"></a>expected| <a name="tech.antibytes.kmock.verification.constraints/any///PointingToDeclaration/"></a><br><br>KClass of the expected value. If set the matcher is restricted to the given class type (excluding null). If null any value (type) is accepted. Default is null.<br><br>|



## Constructors

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification.constraints/any/any/#kotlin.reflect.KClass[*]?/PointingToDeclaration/"></a>[any](any.md)| <a name="tech.antibytes.kmock.verification.constraints/any/any/#kotlin.reflect.KClass[*]?/PointingToDeclaration/"></a> [common] fun [any](any.md)(expected: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)<*>? = null)KClass of the expected value.   <br>|


## Functions

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock.verification.constraints/any/matches/#kotlin.Any?/PointingToDeclaration/"></a>[matches](matches.md)| <a name="tech.antibytes.kmock.verification.constraints/any/matches/#kotlin.Any?/PointingToDeclaration/"></a>[common]  <br>Content  <br>open override fun [matches](matches.md)(actual: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br>More info  <br>Resolves if the constraint matches the given Proxy Argument.  <br><br><br>|
