package com.diego.bpo.controller

import com.diego.bpo.domain.enums.PayableStatus
import com.diego.bpo.dto.PayableCreateDTO
import com.diego.bpo.dto.PayableResponseDTO
import com.diego.bpo.service.PayableService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/payables")
class PayableController(private val service: PayableService) {

    @GetMapping
    fun list(
        @RequestParam status: PayableStatus?,
        @RequestParam(required = false) vendor: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dueFrom: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dueTo: LocalDate?
    ): ResponseEntity<List<PayableResponseDTO>> {
        val list = service.list(status, vendor, dueFrom, dueTo).map { p ->
            PayableResponseDTO(
                id = p.id!!,
                description = p.description,
                vendor = p.vendor,
                amount = p.amount,
                dueDate = p.dueDate,
                status = p.status.name,
                category = p.category,
                paidAt = p.paidAt?.toString()
            )
        }
        return ResponseEntity.ok(list)
    }

    @PostMapping
    fun create(@Valid @RequestBody dto: PayableCreateDTO): ResponseEntity<PayableResponseDTO> {
        val saved = service.create(dto)
        val resp = PayableResponseDTO(
            saved.id!!,
            saved.description,
            saved.vendor,
            saved.amount,
            saved.dueDate,
            saved.status.name,
            saved.category,
            saved.paidAt?.toString()
        )
        return ResponseEntity.status(201).body(resp)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<PayableResponseDTO> {
        val p = service.findById(id)
        return ResponseEntity.ok(
            PayableResponseDTO(
                p.id!!,
                p.description,
                p.vendor,
                p.amount,
                p.dueDate,
                p.status.name,
                p.category,
                p.paidAt?.toString()
            )
        )
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody dto: PayableCreateDTO): ResponseEntity<PayableResponseDTO> {
        val p = service.update(id, dto)
        return ResponseEntity.ok(
            PayableResponseDTO(
                p.id!!,
                p.description,
                p.vendor,
                p.amount,
                p.dueDate,
                p.status.name,
                p.category,
                p.paidAt?.toString()
            )
        )
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Unit> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/pay")
    fun pay(@PathVariable id: UUID): ResponseEntity<PayableResponseDTO> {
        val p = service.pay(id)
        return ResponseEntity.ok(
            PayableResponseDTO(
                p.id!!,
                p.description,
                p.vendor,
                p.amount,
                p.dueDate,
                p.status.name,
                p.category,
                p.paidAt?.toString()
            )
        )
    }
}
