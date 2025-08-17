package com.diego.bpo.domain

import com.diego.bpo.domain.entity.Receivable
import com.diego.bpo.domain.enums.ReceivableStatus
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

object ReceivableSpecs {
    fun withFilters(
        status: ReceivableStatus?,
        customer: String?,
        dueFrom: LocalDate?,
        dueTo: LocalDate?
    ): Specification<Receivable> {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            status?.let { predicates += cb.equal(root.get<ReceivableStatus>("status"), it) }
            customer?.let { predicates += cb.like(cb.lower(root.get("customer")), "%${customer.lowercase()}%") }
            dueFrom?.let { predicates += cb.greaterThanOrEqualTo(root.get("dueDate"), it) }
            dueTo?.let { predicates += cb.lessThanOrEqualTo(root.get("dueDate"), it) }
            cb.and(*predicates.toTypedArray())
        }
    }
}