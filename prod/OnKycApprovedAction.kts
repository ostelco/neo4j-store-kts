import arrow.core.Either
import arrow.core.right
import org.ostelco.prime.auditlog.AuditLog
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.KycStatus.APPROVED
import org.ostelco.prime.model.KycType
import org.ostelco.prime.storage.StoreError
import org.ostelco.prime.storage.graph.AllowedRegionsService
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton
import org.ostelco.prime.storage.graph.OnKycApprovedAction
import org.ostelco.prime.storage.graph.PrimeTransaction

object : OnKycApprovedAction {

    override fun apply(
            customer: Customer,
            regionCode: String,
            kycType: KycType,
            kycExpiryDate: String?,
            kycIdType: String?,
            allowedRegionsService: AllowedRegionsService,
            transaction: PrimeTransaction): Either<StoreError, Unit> {

        return when {
            kycIdType == "PASSPORT" -> {
                allowedRegionsService.get(
                        customer = customer,
                        transaction = transaction
                ).map { allowedRegion ->
                    for (region in (allowedRegion - regionCode)) {
                        Neo4jStoreSingleton.setKycStatus(
                                customer = customer,
                                regionCode = region,
                                kycType = kycType,
                                kycExpiryDate = kycExpiryDate,
                                kycStatus = APPROVED,
                                transaction = transaction
                        )
                    }
                }
            }
            regionCode == "sg" -> {

                AuditLog.info(customerId = customer.id, message = "Approving kyc - $kycType for 'my' region since it's approved for 'sg' region.")

                Neo4jStoreSingleton.setKycStatus(
                        customer = customer,
                        regionCode = "my",
                        kycType = kycType,
                        kycExpiryDate = kycExpiryDate,
                        kycStatus = APPROVED,
                        transaction = transaction
                )
            }
            else -> Unit.right()
        }
    }
}