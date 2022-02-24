import tech.antibytes.kmock.KMockContract

class VerificationChainBuilderStub(
    val collector: MutableList<KMockContract.VerificationHandle>
) : KMockContract.VerificationChainBuilder {
    override fun ensureVerificationOf(vararg mocks: KMockContract.Mockery<*, *>) {
        TODO("Not yet implemented")
    }

    override fun add(handle: KMockContract.VerificationHandle) {
        collector.add(handle)
    }

    override fun toList(): List<KMockContract.VerificationHandle> {
        TODO("Not yet implemented")
    }
}
