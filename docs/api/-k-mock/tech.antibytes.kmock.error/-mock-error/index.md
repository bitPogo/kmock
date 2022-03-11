//[KMock](../../../index.md)/[tech.antibytes.kmock.error](../index.md)/[MockError](index.md)



# MockError
 [common] sealed class [MockError](index.md) : [RuntimeException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-runtime-exception/index.html)

Base error class.



#### Author


Matthias Geisler




## Parameters

common

| | |
|---|---|
| <a name="tech.antibytes.kmock.error/MockError///PointingToDeclaration/"></a>message| <a name="tech.antibytes.kmock.error/MockError///PointingToDeclaration/"></a><br><br>the given error message.<br><br>|



## Types

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock.error/MockError.MissingCall///PointingToDeclaration/"></a>[MissingCall](-missing-call/index.md)| <a name="tech.antibytes.kmock.error/MockError.MissingCall///PointingToDeclaration/"></a>[common]  <br>Content  <br>class [MissingCall](-missing-call/index.md)(**message**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [MockError](index.md)  <br>More info  <br>Indicates that a Proxy was not called.  <br><br><br>|
| <a name="tech.antibytes.kmock.error/MockError.MissingStub///PointingToDeclaration/"></a>[MissingStub](-missing-stub/index.md)| <a name="tech.antibytes.kmock.error/MockError.MissingStub///PointingToDeclaration/"></a>[common]  <br>Content  <br>class [MissingStub](-missing-stub/index.md)(**message**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [MockError](index.md)  <br>More info  <br>Indicates that a Proxy is missing a defined behaviour.  <br><br><br>|


## Properties

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock.error/MockError/cause/#/PointingToDeclaration/"></a>[cause](index.md#-1018374974%2FProperties%2F-34120600)| <a name="tech.antibytes.kmock.error/MockError/cause/#/PointingToDeclaration/"></a> [common] open val [cause](index.md#-1018374974%2FProperties%2F-34120600): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)?   <br>|
| <a name="tech.antibytes.kmock.error/MockError/message/#/PointingToDeclaration/"></a>[message](index.md#-435659932%2FProperties%2F-34120600)| <a name="tech.antibytes.kmock.error/MockError/message/#/PointingToDeclaration/"></a> [common] open val [message](index.md#-435659932%2FProperties%2F-34120600): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?   <br>|


## Inheritors

|  Name |
|---|
| <a name="tech.antibytes.kmock.error/MockError.MissingStub///PointingToDeclaration/"></a>[MockError](-missing-stub/index.md)|
| <a name="tech.antibytes.kmock.error/MockError.MissingCall///PointingToDeclaration/"></a>[MockError](-missing-call/index.md)|
