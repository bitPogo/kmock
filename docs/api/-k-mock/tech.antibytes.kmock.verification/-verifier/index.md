//[KMock](../../../index.md)/[tech.antibytes.kmock.verification](../index.md)/[Verifier](index.md)



# Verifier
 [common] class [Verifier](index.md) : [KMockContract.Verifier](../../tech.antibytes.kmock/-k-mock-contract/-verifier/index.md), [KMockContract.Collector](../../tech.antibytes.kmock/-k-mock-contract/-collector/index.md)

Container which collects and holds actual references of proxy calls in a freezing manner. The references are ordered by their invocation. This is intended as default mode for Verification.



#### Author


Matthias Geisler




## Constructors

| | |
|---|---|
| <a name="tech.antibytes.kmock.verification/Verifier/Verifier/#/PointingToDeclaration/"></a>[Verifier](-verifier.md)| <a name="tech.antibytes.kmock.verification/Verifier/Verifier/#/PointingToDeclaration/"></a> [common] fun [Verifier](-verifier.md)()   <br>|


## Functions

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock.verification/Verifier/addReference/#tech.antibytes.kmock.KMockContract.Proxy[*,*]#kotlin.Int/PointingToDeclaration/"></a>[addReference](add-reference.md)| <a name="tech.antibytes.kmock.verification/Verifier/addReference/#tech.antibytes.kmock.KMockContract.Proxy[*,*]#kotlin.Int/PointingToDeclaration/"></a>[common]  <br>Content  <br>open override fun [addReference](add-reference.md)(referredProxy: [KMockContract.Proxy](../../tech.antibytes.kmock/-k-mock-contract/-proxy/index.md)<*, *>, referredCall: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))  <br>More info  <br>Collects a invocation of a Proxy.  <br><br><br>|
| <a name="tech.antibytes.kmock.verification/Verifier/clear/#/PointingToDeclaration/"></a>[clear](clear.md)| <a name="tech.antibytes.kmock.verification/Verifier/clear/#/PointingToDeclaration/"></a>[common]  <br>Content  <br>open override fun [clear](clear.md)()  <br>More info  <br>Clears the Container  <br><br><br>|


## Properties

|  Name |  Summary |
|---|---|
| <a name="tech.antibytes.kmock.verification/Verifier/references/#/PointingToDeclaration/"></a>[references](references.md)| <a name="tech.antibytes.kmock.verification/Verifier/references/#/PointingToDeclaration/"></a> [common] open override val [references](references.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[KMockContract.Reference](../../tech.antibytes.kmock/-k-mock-contract/-reference/index.md)>Holds the actual references   <br>|
