package com.diego.bpo.service

import com.diego.bpo.domain.entity.Payable
import com.diego.bpo.domain.enums.PayableStatus
import com.diego.bpo.dto.PayableCreateDTO
import com.diego.bpo.repository.PayableRepository
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

class PayableServiceTest {

 private val repo: PayableRepository = mockk(relaxed = true)
 private lateinit var service: PayableService

 @BeforeEach
 fun setup() {
  clearAllMocks()
  service = PayableService(repo)
 }

 @AfterEach
 fun tearDown() {
  confirmVerified(repo)
 }

 @Test
 fun `list should derive OVERDUE for past due pending items`() {
  val overduePayable = Payable(
   id = UUID.randomUUID(),
   description = "past due",
   vendor = "V",
   amount = BigDecimal("10.00"),
   dueDate = LocalDate.now().minusDays(2),
   status = PayableStatus.PENDING,
   category = null,
   paidAt = null,
   createdAt = OffsetDateTime.now(),
   updatedAt = null
  )

  every { repo.findFiltered(null, null, null, null) } returns listOf(overduePayable)

  val result = service.list(null, null, null, null)
  assertEquals(1, result.size)
  assertEquals(PayableStatus.OVERDUE, result[0].status)

  verify { repo.findFiltered(null, null, null, null) }
 }

 @Test
 fun `findById should return entity when present`() {
  val id = UUID.randomUUID()
  val p = Payable(
   id = id,
   description = "x",
   vendor = "v",
   amount = BigDecimal("100.00"),
   dueDate = LocalDate.now().plusDays(1),
   status = PayableStatus.PENDING,
   category = null,
   paidAt = null,
   createdAt = OffsetDateTime.now(),
   updatedAt = null
  )

  every { repo.findById(id) } returns Optional.of(p)

  val found = service.findById(id)
  assertEquals(id, found.id)
  assertEquals("v", found.vendor)

  verify { repo.findById(id) }
 }

 @Test
 fun `findById should throw when not found`() {
  val id = UUID.randomUUID()
  every { repo.findById(id) } returns Optional.empty()

  val ex = assertThrows(NoSuchElementException::class.java) {
   service.findById(id)
  }
  assertTrue(ex.message!!.contains("Payable not found"))

  verify { repo.findById(id) }
 }

 @Test
 fun `create should save and return entity`() {
  val dto = PayableCreateDTO(
   description = "buy",
   vendor = "Vendor A",
   amount = BigDecimal("250.00"),
   dueDate = LocalDate.now().plusDays(5),
   category = "Cat"
  )

  // simulate save returning entity with id and createdAt set
  every { repo.save(any()) } answers {
   val arg = firstArg<Payable>()
   arg.copy(id = UUID.randomUUID(), createdAt = OffsetDateTime.now(ZoneOffset.UTC))
  }

  val saved = service.create(dto)
  assertNotNull(saved.id)
  assertEquals("Vendor A", saved.vendor)
  verify { repo.save(any()) }
 }

 @Test
 fun `update should modify existing entity`() {
  val id = UUID.randomUUID()
  val existing = Payable(
   id = id,
   description = "old",
   vendor = "V",
   amount = BigDecimal("10.00"),
   dueDate = LocalDate.now().plusDays(1),
   status = PayableStatus.PENDING,
   category = null,
   paidAt = null,
   createdAt = OffsetDateTime.now().minusDays(2),
   updatedAt = null
  )

  val dto = PayableCreateDTO("new desc", "V2", BigDecimal("50.00"), LocalDate.now().plusDays(10), "CatX")

  every { repo.findById(id) } returns Optional.of(existing)
  every { repo.save(any()) } answers { firstArg() }

  val updated = service.update(id, dto)
  assertEquals("new desc", updated.description)
  assertEquals("V2", updated.vendor)
  assertEquals(BigDecimal("50.00"), updated.amount)
  assertEquals("CatX", updated.category)
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
 fun `pay should set status PAID and timestamps`() {
  val id = UUID.randomUUID()
  val existing = Payable(
   id = id,
   description = "pay me",
   vendor = "V",
   amount = BigDecimal("100.00"),
   dueDate = LocalDate.now().plusDays(1),
   status = PayableStatus.PENDING,
   category = null,
   paidAt = null,
   createdAt = OffsetDateTime.now().minusDays(1),
   updatedAt = null
  )

  every { repo.findById(id) } returns Optional.of(existing)
  every { repo.save(any()) } answers { firstArg() }

  val paid = service.pay(id)
  assertEquals(PayableStatus.PAID, paid.status)
  assertNotNull(paid.paidAt)
  assertNotNull(paid.updatedAt)

  verify { repo.findById(id); repo.save(any()) }
 }
}
