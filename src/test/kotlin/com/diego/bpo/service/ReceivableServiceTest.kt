package com.diego.bpo.service

import com.diego.bpo.domain.entity.Receivable
import com.diego.bpo.domain.enums.ReceivableStatus
import com.diego.bpo.dto.ReceivableCreateDTO
import com.diego.bpo.repository.ReceivableRepository
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class ReceivableServiceTest {

    private val repo: ReceivableRepository = mockk(relaxed = true)
    private lateinit var service: ReceivableService

    @BeforeEach
    fun setup() {
        clearAllMocks()
        service = ReceivableService(repo)
    }

    @AfterEach
    fun tearDown() {
        confirmVerified(repo)
    }

    @Test
    fun `list should derive OVERDUE for past due pending receivables`() {
        val r = Receivable(
            id = UUID.randomUUID(),
            description = "old invoice",
            customer = "C",
            amount = BigDecimal("200.00"),
            dueDate = LocalDate.now().minusDays(3),
            status = ReceivableStatus.PENDING,
            category = null,
            receivedAt = null,
            createdAt = OffsetDateTime.now(),
            updatedAt = null
        )

        every { repo.findFiltered(null, null, null, null) } returns listOf(r)

        val res = service.list(null, null, null, null)
        assertEquals(1, res.size)
        assertEquals(ReceivableStatus.OVERDUE, res[0].status)

        verify { repo.findFiltered(null, null, null, null) }
    }

    @Test
    fun `findById returns when found`() {
        val id = UUID.randomUUID()
        val r = Receivable(
            id = id,
            description = "x",
            customer = "c",
            amount = BigDecimal("10.00"),
            dueDate = LocalDate.now().plusDays(1),
            status = ReceivableStatus.PENDING,
            category = null,
            receivedAt = null,
            createdAt = OffsetDateTime.now(),
            updatedAt = null
        )

        every { repo.findById(id) } returns Optional.of(r)

        val found = service.findById(id)
        assertEquals(id, found.id)
        verify { repo.findById(id) }
    }

    @Test
    fun `findById throws when not found`() {
        val id = UUID.randomUUID()
        every { repo.findById(id) } returns Optional.empty()

        val ex = assertThrows(NoSuchElementException::class.java) {
            service.findById(id)
        }
        assertTrue(ex.message!!.contains("Receivable not found"))
        verify { repo.findById(id) }
    }

    @Test
    fun `create should save and return entity`() {
        val dto = ReceivableCreateDTO("desc", "C", BigDecimal("500.00"), LocalDate.now().plusDays(5), "Cat")
        every { repo.save(any()) } answers {
            val arg = firstArg<Receivable>()
            arg.copy(id = UUID.randomUUID(), createdAt = OffsetDateTime.now(ZoneOffset.UTC))
        }

        val saved = service.create(dto)
        assertNotNull(saved.id)
        assertEquals("C", saved.customer)
        verify { repo.save(any()) }
    }

    @Test
    fun `update should modify existing receivable`() {
        val id = UUID.randomUUID()
        val existing = Receivable(
            id = id,
            description = "old",
            customer = "C",
            amount = BigDecimal("100.00"),
            dueDate = LocalDate.now().plusDays(1),
            status = ReceivableStatus.PENDING,
            category = null,
            receivedAt = null,
            createdAt = OffsetDateTime.now().minusDays(2),
            updatedAt = null
        )
        val dto = ReceivableCreateDTO("new", "C2", BigDecimal("120.00"), LocalDate.now().plusDays(8), "CatX")

        every { repo.findById(id) } returns Optional.of(existing)
        every { repo.save(any()) } answers { firstArg() }

        val updated = service.update(id, dto)
        assertEquals("new", updated.description)
        assertEquals("C2", updated.customer)
        assertEquals(BigDecimal("120.00"), updated.amount)
        assertNotNull(updated.updatedAt)

        verify { repo.findById(id); repo.save(any()) }
    }

    @Test
    fun `delete should call repository deleteById`() {
        val id = UUID.randomUUID()
        every { repo.deleteById(id) } just Runs

        service.delete(id)

        verify { repo.deleteById(id) }
    }

    @Test
    fun `receive should set status RECEIVED and timestamps`() {
        val id = UUID.randomUUID()
        val existing = Receivable(
            id = id,
            description = "to receive",
            customer = "C",
            amount = BigDecimal("250.00"),
            dueDate = LocalDate.now().plusDays(2),
            status = ReceivableStatus.PENDING,
            category = null,
            receivedAt = null,
            createdAt = OffsetDateTime.now().minusDays(1),
            updatedAt = null
        )

        every { repo.findById(id) } returns Optional.of(existing)
        every { repo.save(any()) } answers { firstArg() }

        val rec = service.receive(id)
        assertEquals(ReceivableStatus.RECEIVED, rec.status)
        assertNotNull(rec.receivedAt)
        assertNotNull(rec.updatedAt)

        verify { repo.findById(id); repo.save(any()) }
    }
}
