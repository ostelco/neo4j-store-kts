package neo4j

import arrow.core.Either
import arrow.core.right
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import org.ostelco.prime.storage.StoreError
import org.ostelco.prime.storage.graph.AllowedRegionsService
import org.ostelco.prime.storage.graph.PrimeTransaction

object : AllowedRegionsService {
    override fun get(identity: Identity, customer: Customer, transaction: PrimeTransaction): Either<StoreError, Collection<String>> {
        val allowedEmailDomains = listOf("@oya.sg", "@oya.world", "@redotter.sg", "@wgtwo.com")
        val matchedDomains = allowedEmailDomains.filter { customer.contactEmail.toLowerCase().endsWith(it) }
        return if (matchedDomains.size > 0)
            listOf("no", "sg", "us", "my").right()
        else
            listOf("sg", "us", "my").right()
    }
}