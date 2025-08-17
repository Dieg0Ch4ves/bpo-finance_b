package com.diego.bpo.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class ReceivableCreateDTO(
    @field:NotBlank val description: String,
    @field:NotBlank val customer: String,
    @field:NotNull val amount: BigDecimal,
    @field:NotNull val dueDate: LocalDate,
    val category: String? = null
)

data class ReceivableResponseDTO(
    val id: UUID,
    val description: String,
    val customer: String,
    val amount: BigDecimal,
    val dueDate: LocalDate,
    val status: String,
    val category: String?,
    val receivedAt: String?
)
