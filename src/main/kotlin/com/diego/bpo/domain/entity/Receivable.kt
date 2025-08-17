package com.diego.bpo.domain.entity

import com.diego.bpo.domain.enums.ReceivableStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "receivables")
data class Receivable(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var customer: String,

    @Column(nullable = false, precision = 15, scale = 2)
    var amount: BigDecimal,

    @Column(name = "due_date", nullable = false)
    var dueDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ReceivableStatus = ReceivableStatus.PENDING,

    var category: String? = null,

    @Column(name = "received_at")
    var receivedAt: OffsetDateTime? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: OffsetDateTime? = null
)
