package com.diego.bpo.repository

import com.diego.bpo.domain.entity.Receivable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface ReceivableRepository : JpaRepository<Receivable, UUID>, JpaSpecificationExecutor<Receivable>
