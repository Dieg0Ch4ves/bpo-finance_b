package com.diego.bpo.repository

import com.diego.bpo.domain.entity.Payable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface PayableRepository : JpaRepository<Payable, UUID>, JpaSpecificationExecutor<Payable>