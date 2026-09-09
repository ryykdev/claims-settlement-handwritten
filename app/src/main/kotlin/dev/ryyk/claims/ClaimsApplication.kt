package dev.ryyk.claims

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ClaimsApplication

fun main(args: Array<String>) {
	runApplication<ClaimsApplication>(*args)
}
