package com.google.shinyay.configuration

import com.google.shinyay.entity.Person
import com.google.shinyay.processor.PersonItemProcessor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import javax.sql.DataSource

@Configuration
class BatchConfiguration(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory
) {

    @Bean
    @StepScope
    fun resource(@Value("#{jobParameters['fileName']}") fileName: String): Resource = UrlResource(fileName)

    @Bean
    fun reader(resource: Resource): FlatFileItemReader<Person> = FlatFileItemReaderBuilder<Person>()
        .name("PersonReader")
        .resource(resource)
        .delimited()
        .delimiter(",")
        .names("firstName", "lastName", "email", "location")
        .fieldSetMapper(object : BeanWrapperFieldSetMapper<Person?>() {
            init {
                setTargetType(Person::class.java)
            }
        })
        .build()

    @Bean
    fun writer(dataSource: DataSource): JdbcBatchItemWriter<Person> = JdbcBatchItemWriterBuilder<Person>()
//        .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider<Person>())
        .beanMapped()
        .sql("INSERT INTO PEOPLE (first_name, last_name, email, location) VALUES (:firstName, :lastName, :email, :location)")
        .dataSource(dataSource)
        .build()

    @Bean
    fun upperCaseProcessor(): PersonItemProcessor = PersonItemProcessor()

    @Bean
    fun step(resource: Resource, dataSource: DataSource): Step = stepBuilderFactory.get("uppercase-step")
        .chunk<Person, Person>(10)
        .reader(reader(resource))
        .processor(upperCaseProcessor())
        .writer(writer(dataSource))
        .build()

    @Bean
    fun job(step: Step): Job = jobBuilderFactory["uppercase-job"]
        .incrementer(RunIdIncrementer())
        .flow(step)
        .end()
        .build()
}