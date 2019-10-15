import arrow.core.Either
import arrow.core.fix
import arrow.effects.IO
import arrow.instances.either.monad.monad
import org.ostelco.prime.auditlog.AuditLog
import org.ostelco.prime.dsl.WriteTransaction
import org.ostelco.prime.dsl.withId
import org.ostelco.prime.model.Customer
import org.ostelco.prime.storage.StoreError
import org.ostelco.prime.storage.graph.OnRegionApprovedAction
import org.ostelco.prime.storage.graph.PrimeTransaction
import org.ostelco.prime.storage.graph.model.Segment

object : OnRegionApprovedAction {

    override fun apply(
            customer: Customer,
            regionCode: String,
            transaction: PrimeTransaction
    ): Either<StoreError, Unit> {
        val segmentId = when (regionCode.toLowerCase()) {
            "no", "us" -> "country-${regionCode.toLowerCase()}"
            else -> "country-sg"
        }
        return IO {
            Either.monad<StoreError>().binding {
                WriteTransaction(transaction).apply {
                    fact { (Customer withId customer.id) belongsToSegment (Segment withId segmentId) }.bind()
                    AuditLog.info(customer.id, "Added customer to segment - $segmentId")
                }
                Unit
            }.fix()
        }.unsafeRunSync()
    }
}