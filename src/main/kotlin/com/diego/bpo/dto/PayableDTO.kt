package com.diego.bpo.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class PayableCreateDTO(
    @field:NotBlank val description: String,
    @field:NotBlank val vendor: String,
    @field:NotNull val amount: BigDecimal,
    @field:NotNull val dueDate: LocalDate,
    val category: String? = null
)

data class PayableResponseDTO(
    val id: UUID,
    val description: String,
    val vendor: String,
    val amount: BigDecimal,
    val dueDate: LocalDate,
    val status: String,
    val category: String?,
    val paidAt: String?
)