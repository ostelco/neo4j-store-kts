import arrow.core.Either
import arrow.core.right
import org.ostelco.prime.model.Customer
import org.ostelco.prime.storage.StoreError
import org.ostelco.prime.storage.graph.AllowedRegionsService
import org.ostelco.prime.storage.graph.PrimeTransaction

object : AllowedRegionsService {

    override fun get(customer: Customer, transaction: PrimeTransaction): Either<StoreError, Collection<String>> {

        val allowedRegions = setOf(
                "sg",
                "us",
                "my",
                "no".takeIf {
                    isEmailAllowed(
                            customerEmail = customer.contactEmail.toLowerCase(),
                            allowedEmails = setOf(
                                    "alyssa.rivera3@gmail.com",
                                    "eyvind.bernhardsen@gmail.com",
                                    "havardnoren@gmail.com",
                                    "oisinzimmermann@mac.com",
                                    "prasanth.u@gmail.com",
                                    "rmz@rmz.no",
                                    "vihang.patil@gmail.com"
                            ),
                            allowedEmailSuffixes = setOf(
                                    "@oya.sg",
                                    "@oya.world",
                                    "@redotter.sg",
                                    "@redotter.world",
                                    "@wgtwo.com"
                            )
                    )
                }
        ).filterNotNull()

        return allowedRegions.right()
    }

    private fun isEmailAllowed(
            customerEmail: String,
            allowedEmails: Set<String>,
            allowedEmailSuffixes: Set<String>
    ): Boolean = allowedEmailSuffixes.any { customerEmail.endsWith(it) } || allowedEmails.contains(customerEmail)

}
