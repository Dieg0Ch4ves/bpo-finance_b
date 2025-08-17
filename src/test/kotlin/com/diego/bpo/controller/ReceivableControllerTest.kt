package com.diego.bpo.controller

import com.diego.bpo.domain.entity.Receivable
import com.diego.bpo.domain.enums.ReceivableStatus
import com.diego.bpo.service.ReceivableService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@WebMvcTest(controllers = [ReceivableController::class])
class ReceivableControllerTest {

 @Autowired
 lateinit var mockMvc: MockMvc

 @MockkBean
 lateinit var service: ReceivableService

 @Test
 fun `POST create receivable returns 201`() {
  val dtoJson = """
            {
              "description": "Venda X",
              "customer": "Cliente A",
              "amount": 300.0,
              "dueDate": "${LocalDate.now().plusDays(7)}",
              "category": "Serviço"
            }
        """.trimIndent()

  val saved = Receivable(
   id = UUID.randomUUID(),
   description = "Venda X",
   customer = "Cliente A",
   amount = BigDecimal("300.0"),
   dueDate = LocalDate.now().plusDays(7),
   status = ReceivableStatus.PENDING,
   category = "Serviço",
   receivedAt = null,
   createdAt = OffsetDateTime.now(ZoneOffset.UTC),
   updatedAt = null
  )

  every { service.create(any()) } returns saved

  mockMvc.perform(
   post("/api/receivables")
    .contentType(MediaType.APPLICATION_JSON)
    .content(dtoJson)
  )
   .andExpect(status().isCreated)
   .andExpect(jsonPath("$.customer").value("Cliente A"))
   .andExpect(jsonPath("$.amount").value(300.0))
 }

 @Test
 fun `GET list returns list`() {
  val r = Receivable(
   id = UUID.randomUUID(),
   description = "r",
   customer = "C",
   amount = BigDecimal("10.00"),
   dueDate = LocalDate.now().plusDays(1),
   status = ReceivableStatus.PENDING,
   category = null,
   receivedAt = null,
   createdAt = OffsetDateTime.now(),
   updatedAt = null
  )

  every { service.list(null, null, null, null) } returns listOf(r)

  mockMvc.perform(get("/api/receivables"))
   .andExpect(status().isOk)
   .andExpect(jsonPath("$", hasSize<Int>(1)))
   .andExpect(jsonPath("$[0].customer", `is`("C")))
 }

 @Test
 fun `GET by id returns 200`() {
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

  every { service.findById(id) } returns r

  mockMvc.perform(get("/api/receivables/$id"))
   .andExpect(status().isOk)
   .andExpect(jsonPath("$.id").value(id.toString()))
 }

 @Test
 fun `PUT update returns 200`() {
  val id = UUID.randomUUID()
  val dtoJson = """
            {
              "description": "upd",
              "customer": "C2",
              "amount": 50.0,
              "dueDate": "${LocalDate.now().plusDays(10)}",
              "category":"Cat"
            }
        """.trimIndent()

  val updated = Receivable(
   id = id,
   description = "upd",
   customer = "C2",
   amount = BigDecimal("50.0"),
   dueDate = LocalDate.now().plusDays(10),
   status = ReceivableStatus.PENDING,
   category = "Cat",
   receivedAt = null,
   createdAt = OffsetDateTime.now(),
   updatedAt = OffsetDateTime.now()
  )

  every { service.update(id, any()) } returns updated

  mockMvc.perform(
   put("/api/receivables/$id")
    .contentType(MediaType.APPLICATION_JSON)
    .content(dtoJson)
  )
   .andExpect(status().isOk)
   .andExpect(jsonPath("$.customer").value("C2"))
   .andExpect(jsonPath("$.description").value("upd"))
 }

 @Test
 fun `DELETE returns 204`() {
  val id = UUID.randomUUID()
  every { service.delete(id) } returns Unit

  mockMvc.perform(delete("/api/receivables/$id"))
   .andExpect(status().isNoContent)
 }

 @Test
 fun `PATCH receive should return 200 and RECEIVED status`() {
  val id = UUID.randomUUID()
  val rec = Receivable(
   id = id,
   description = "rec",
   customer = "C",
   amount = BigDecimal("10.00"),
   dueDate = LocalDate.now().plusDays(1),
   status = ReceivableStatus.RECEIVED,
   category = null,
   receivedAt = OffsetDateTime.now(ZoneOffset.UTC),
   createdAt = OffsetDateTime.now(),
   updatedAt = OffsetDateTime.now()
  )

  every { service.receive(id) } returns rec

  mockMvc.perform(patch("/api/receivables/$id/receive"))
   .andExpect(status().isOk)
   .andExpect(jsonPath("$.status").value("RECEIVED"))
   .andExpect(jsonPath("$.receivedAt", notNullValue()))
 }
}
