package com.diego.bpo.service

import com.diego.bpo.domain.ReceivableSpecs
import com.diego.bpo.domain.entity.Receivable
import com.diego.bpo.domain.enums.ReceivableStatus
import com.diego.bpo.dto.ReceivableCreateDTO
import com.diego.bpo.repository.ReceivableRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@Service
class ReceivableService(private val repo: ReceivableRepository) {

    fun list(status: ReceivableStatus?, customer: String?, from: LocalDate?, to: LocalDate?): List<Receivable> =
        repo.findAll(ReceivableSpecs.withFilters(status, customer, from, to))

    fun findById(id: UUID): Receivable =
        repo.findById(id).orElseThrow { NoSuchElementException("Receivable not found: $id") }.let { deriveOverdue(it) }

    @Transactional
    fun create(dto: ReceivableCreateDTO): Receivable {
        val r = Receivable(
            description = dto.description,
            customer = dto.customer,
            amount = dto.amount,
            dueDate = dto.dueDate,
            category = dto.category
        )
        return repo.save(r)
    }

    @Transactional
    fun update(id: UUID, dto: ReceivableCreateDTO): Receivable {
        val existing = repo.findById(id).orElseThrow { NoSuchElementException("Receivable not found: $id") }
        existing.description = dto.description
        existing.customer = dto.customer
        existing.amount = dto.amount
        existing.dueDate = dto.dueDate
        existing.category = dto.category
        existing.updatedAt = OffsetDateTime.now(ZoneOffset.UTC)
        return repo.save(existing)
    }

    @Transactional
    fun delete(id: UUID) = repo.deleteById(id)

    @Transactional
    fun receive(id: UUID): Receivable {
        val r = repo.findById(id).orElseThrow { NoSuchElementException("Receivable not found: $id") }
        r.status = ReceivableStatus.RECEIVED
        r.receivedAt = OffsetDateTime.now(ZoneOffset.UTC)
        r.updatedAt = OffsetDateTime.now(ZoneOffset.UTC)
        return repo.save(r)
    }

    private fun deriveOverdue(r: Receivable): Receivable {
        if (r.status == ReceivableStatus.PENDING && r.dueDate.isBefore(LocalDate.now())) {
            r.status = ReceivableStatus.OVERDUE
        }
        return r
    }
}
