package com.diego.bpo.domain

import com.diego.bpo.domain.entity.Payable
import com.diego.bpo.domain.enums.PayableStatus
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

object PayableSpecs {
    fun withFilters(
        status: PayableStatus?,
        vendor: String?,
        from: LocalDate?,
        to: LocalDate?
    ): Specification<Payable> {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            status?.let { predicates += cb.equal(root.get<PayableStatus>("status"), it) }
            vendor?.let { predicates += cb.like(cb.lower(root.get("vendor")), "%${vendor.lowercase()}%") }
            from?.let { predicates += cb.greaterThanOrEqualTo(root.get("dueDate"), from) }
            to?.let { predicates += cb.lessThanOrEqualTo(root.get("dueDate"), to) }
            cb.and(*predicates.toTypedArray())
        }
    }
}