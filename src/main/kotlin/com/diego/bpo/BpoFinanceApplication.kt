package com.diego.bpo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BpoFinanceApplication

fun main(args: Array<String>) {
	runApplication<BpoFinanceApplication>(*args)
}
