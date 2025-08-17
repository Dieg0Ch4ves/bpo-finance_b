package com.diego.bpo.service

import com.diego.bpo.domain.PayableSpecs
import com.diego.bpo.domain.entity.Payable
import com.diego.bpo.domain.enums.PayableStatus
import com.diego.bpo.dto.PayableCreateDTO
import com.diego.bpo.repository.PayableRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class PayableService(
    private val repo: PayableRepository
) {

    fun list(
        status: PayableStatus?,
        vendor: String?,
        from: LocalDate?,
        to: LocalDate?
    ): List<Payable> {
        return repo.findAll(PayableSpecs.withFilters(status, vendor, from, to))
    }

    fun findById(id: UUID): Payable =
        repo.findById(id).orElseThrow { NoSuchElementException("Payable not found: $id") }.let { deriveOverdue(it) }

    @Transactional
    fun create(dto: PayableCreateDTO): Payable {
        val p = Payable(
            description = dto.description,
            vendor = dto.vendor,
            amount = dto.amount,
            dueDate = dto.dueDate,
            category = dto.category
        )
        return repo.save(p)
    }

    @Transactional
    fun update(id: UUID, dto: PayableCreateDTO): Payable {
        val existing = repo.findById(id).orElseThrow { NoSuchElementException("Payable not found: $id") }
        existing.description = dto.description
        existing.vendor = dto.vendor
        existing.amount = dto.amount
        existing.dueDate = dto.dueDate
        existing.category = dto.category
        existing.updatedAt = OffsetDateTime.now(ZoneOffset.UTC)
        return repo.save(existing)
    }

    @Transactional
    fun delete(id: UUID) {
        repo.deleteById(id)
    }

    @Transactional
    fun pay(id: UUID): Payable {
        val p = repo.findById(id).orElseThrow { NoSuchElementException("Payable not found: $id") }
        p.status = PayableStatus.PAID
        p.paidAt = OffsetDateTime.now(ZoneOffset.UTC)
        p.updatedAt = OffsetDateTime.now(ZoneOffset.UTC)
        return repo.save(p)
    }

    private fun deriveOverdue(p: Payable): Payable {
        if (p.status == PayableStatus.PENDING && p.dueDate.isBefore(LocalDate.now())) {
            p.status = PayableStatus.OVERDUE
        }
        return p
    }
}
