package com.google.shinyay

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableBatchProcessing
class SpringBatchKubernetesGsApplication

fun main(args: Array<String>) {
	runApplication<SpringBatchKubernetesGsApplication>(*args)
}
