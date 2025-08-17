package com.diego.bpo.controller

import com.diego.bpo.domain.entity.Payable
import com.diego.bpo.domain.enums.PayableStatus
import com.diego.bpo.service.PayableService
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

@WebMvcTest(controllers = [PayableController::class])
class PayableControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var service: PayableService

    private fun toJson(body: String) = body

    @Test
    fun `POST create should return 201 and body`() {
        val dtoJson = """
            {
              "description": "Compra X",
              "vendor": "Fornecedor A",
              "amount": 150.50,
              "dueDate": "${LocalDate.now().plusDays(5)}",
              "category": "Software"
            }
        """.trimIndent()

        val saved = Payable(
            id = UUID.randomUUID(),
            description = "Compra X",
            vendor = "Fornecedor A",
            amount = BigDecimal("150.50"),
            dueDate = LocalDate.now().plusDays(5),
            status = PayableStatus.PENDING,
            category = "Software",
            paidAt = null,
            createdAt = OffsetDateTime.now(ZoneOffset.UTC),
            updatedAt = null
        )

        every { service.create(any()) } returns saved

        mockMvc.perform(
            post("/api/payables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dtoJson))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(saved.id.toString()))
            .andExpect(jsonPath("$.vendor").value("Fornecedor A"))
            .andExpect(jsonPath("$.amount").value(150.50))
    }

    @Test
    fun `GET list should return 200 and list`() {
        val p = Payable(
            id = UUID.randomUUID(),
            description = "x",
            vendor = "V",
            amount = BigDecimal("10.00"),
            dueDate = LocalDate.now().plusDays(1),
            status = PayableStatus.PENDING,
            category = null,
            paidAt = null,
            createdAt = OffsetDateTime.now(),
            updatedAt = null
        )

        every { service.list(null, null, null, null) } returns listOf(p)

        mockMvc.perform(get("/api/payables"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Int>(1)))
            .andExpect(jsonPath("$[0].vendor", `is`("V")))
    }

    @Test
    fun `GET by id should return 200`() {
        val id = UUID.randomUUID()
        val p = Payable(
            id = id,
            description = "x",
            vendor = "V",
            amount = BigDecimal("10.00"),
            dueDate = LocalDate.now().plusDays(1),
            status = PayableStatus.PENDING,
            category = null,
            paidAt = null,
            createdAt = OffsetDateTime.now(),
            updatedAt = null
        )

        every { service.findById(id) } returns p

        mockMvc.perform(get("/api/payables/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.vendor").value("V"))
    }

    @Test
    fun `PUT update should return 200`() {
        val id = UUID.randomUUID()
        val dtoJson = """
            {
              "description": "upd",
              "vendor": "V2",
              "amount": 99.9,
              "dueDate": "${LocalDate.now().plusDays(10)}",
              "category":"Cat"
            }
        """.trimIndent()

        val updated = Payable(
            id = id,
            description = "upd",
            vendor = "V2",
            amount = BigDecimal("99.9"),
            dueDate = LocalDate.now().plusDays(10),
            status = PayableStatus.PENDING,
            category = "Cat",
            paidAt = null,
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )

        every { service.update(id, any()) } returns updated

        mockMvc.perform(
            put("/api/payables/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dtoJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.vendor").value("V2"))
            .andExpect(jsonPath("$.description").value("upd"))
    }

    @Test
    fun `DELETE should return 204`() {
        val id = UUID.randomUUID()
        every { service.delete(id) } returns Unit

        mockMvc.perform(delete("/api/payables/$id"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `PATCH pay should return 200 and PAID status`() {
        val id = UUID.randomUUID()
        val paid = Payable(
            id = id,
            description = "pay",
            vendor = "V",
            amount = BigDecimal("10.00"),
            dueDate = LocalDate.now().plusDays(1),
            status = PayableStatus.PAID,
            category = null,
            paidAt = OffsetDateTime.now(ZoneOffset.UTC),
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )

        every { service.pay(id) } returns paid

        mockMvc.perform(patch("/api/payables/$id/pay"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("PAID"))
            .andExpect(jsonPath("$.paidAt", notNullValue()))
    }

    @Test
    fun `POST create should return 400 when validation fails`() {
        // missing vendor
        val badJson = """
            {
              "description": "Compra X",
              "amount": 150.50,
              "dueDate": "${LocalDate.now().plusDays(5)}"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/payables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson)
        )
            .andExpect(status().isBadRequest)
    }
}
