package com.google.shinyay.processor

import com.google.shinyay.entity.Person
import com.google.shinyay.logger
import org.springframework.batch.item.ItemProcessor

class PersonItemProcessor : ItemProcessor<Person, Person> {
    override fun process(person: Person): Person {
        val transformedPerson = Person(
            person.firstName.toUpperCase(),
            person.lastName.toUpperCase(),
            person.email,
            person.location
        )
        logger.debug("From $person To $transformedPerson")
        return transformedPerson
    }
}