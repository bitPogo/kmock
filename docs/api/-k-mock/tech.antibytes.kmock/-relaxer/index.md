//[KMock](../../../index.md)/[tech.antibytes.kmock](../index.md)/[Relaxer](index.md)



# Relaxer
 [common] @[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.FUNCTION](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-f-u-n-c-t-i-o-n/index.html)])

annotation class [Relaxer](index.md)

Determines a Relaxer (optional). The Processor will use only the first specified relaxer. Note: The relaxer must match the following form or the Processor fails:

fun <T> relax(id: String): T {
   ...
}

or:

inline fun <reified T> relax(id: String): T {
    ...
}

The Processor will delegate the id of the Proxy which will invoke the Relaxer.




## See also

common

| | |
|---|---|
| <a name="tech.antibytes.kmock/Relaxer///PointingToDeclaration/"></a>[tech.antibytes.kmock.KMockContract.Relaxer](../-k-mock-contract/-relaxer/index.md)| <a name="tech.antibytes.kmock/Relaxer///PointingToDeclaration/"></a>|



## Constructors

| | |
|---|---|
| <a name="tech.antibytes.kmock/Relaxer/Relaxer/#/PointingToDeclaration/"></a>[Relaxer](-relaxer.md)| <a name="tech.antibytes.kmock/Relaxer/Relaxer/#/PointingToDeclaration/"></a> [common] fun [Relaxer](-relaxer.md)()   <br>|
