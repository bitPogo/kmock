import tech.antibytes.kmock.KMockContract

class VerificationChainBuilderStub(
    private val collector: MutableList<KMockContract.VerificationHandle>
) : KMockContract.VerificationChainBuilder {
    override fun add(handle: KMockContract.VerificationHandle) {
        collector.add(handle)
    }

    override fun toList(): List<KMockContract.VerificationHandle> {
        TODO("Not yet implemented")
    }
}
