package com.google.shinyay

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBatchKubernetesGsApplication

fun main(args: Array<String>) {
	runApplication<SpringBatchKubernetesGsApplication>(*args)
}
