package com.luiz.springbatchexample.batch;

import com.luiz.springbatchexample.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonItemProcessor.class);

    /*@Override
    public String process(Person person) throws Exception {
        String greeting = "Hello " + person.getFirstName() + " "
                + person.getLastName() + "!";

        LOGGER.info("converting '{}' into '{}'", person, greeting);
        return greeting;
    }*/

    @Override
    public Person process(Person person) throws Exception {
        Person newPerson = new Person();
        newPerson.setFirstName(person.getFirstName().toUpperCase());
        newPerson.setLastName(person.getLastName().toUpperCase());

        LOGGER.info("converting to Uppercase");
        return newPerson;
    }
}
