package com.diego.bpo.controller

import com.diego.bpo.domain.enums.ReceivableStatus
import com.diego.bpo.dto.ReceivableCreateDTO
import com.diego.bpo.dto.ReceivableResponseDTO
import com.diego.bpo.service.ReceivableService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/receivables")
class ReceivableController(private val service: ReceivableService) {

    @GetMapping
    fun list(
        @RequestParam status: ReceivableStatus?,
        @RequestParam(required = false) customer: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dueFrom: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dueTo: LocalDate?
    ): ResponseEntity<List<ReceivableResponseDTO>> {
        val list = service.list(status, customer, dueFrom, dueTo).map { r ->
            ReceivableResponseDTO(r.id!!, r.description, r.customer, r.amount, r.dueDate, r.status.name, r.category, r.receivedAt?.toString())
        }
        return ResponseEntity.ok(list)
    }

    @PostMapping
    fun create(@Valid @RequestBody dto: ReceivableCreateDTO): ResponseEntity<ReceivableResponseDTO> {
        val saved = service.create(dto)
        return ResponseEntity.status(201).body(ReceivableResponseDTO(saved.id!!, saved.description, saved.customer, saved.amount, saved.dueDate, saved.status.name, saved.category, saved.receivedAt?.toString()))
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID) = ResponseEntity.ok(service.findById(id).let { r ->
        ReceivableResponseDTO(r.id!!, r.description, r.customer, r.amount, r.dueDate, r.status.name, r.category, r.receivedAt?.toString())
    })

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody dto: ReceivableCreateDTO) = ResponseEntity.ok(service.update(id, dto).let { r ->
        ReceivableResponseDTO(r.id!!, r.description, r.customer, r.amount, r.dueDate, r.status.name, r.category, r.receivedAt?.toString())
    })

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Unit> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/receive")
    fun receive(@PathVariable id: UUID): ResponseEntity<ReceivableResponseDTO> {
        val r = service.receive(id)
        return ResponseEntity.ok(ReceivableResponseDTO(r.id!!, r.description, r.customer, r.amount, r.dueDate, r.status.name, r.category, r.receivedAt?.toString()))
    }
}
