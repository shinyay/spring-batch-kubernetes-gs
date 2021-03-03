package com.google.shinyay.configuration

import com.google.shinyay.entity.Person
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource

@Configuration
class BatchConfiguration(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory
) {

    @Bean
    @StepScope
    fun resource(@Value("#{jobParameters['fileName']}") fileName: String): Resource = UrlResource(fileName)

    @Bean
    fun reader(): FlatFileItemReader<Person> = FlatFileItemReaderBuilder<Person>()
        .name("PersonReader")
        .build()
}