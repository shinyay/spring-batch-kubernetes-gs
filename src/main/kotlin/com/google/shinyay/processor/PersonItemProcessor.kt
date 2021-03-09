package com.google.shinyay.processor

import com.google.shinyay.entity.Person
import com.google.shinyay.logger
import org.springframework.batch.item.ItemProcessor

class PersonItemProcessor : ItemProcessor<Person, Person> {
    override fun process(person: Person): Person {
        val upperCasedPerson = Person(
            person.firstName.toUpperCase(),
            person.lastName.toUpperCase(),
            (0..100).random(),
            person.email,
            person.location
        )
        logger.debug("From $person To $upperCasedPerson")
        return upperCasedPerson
    }
}