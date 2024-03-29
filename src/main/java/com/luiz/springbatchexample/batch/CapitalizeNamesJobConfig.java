package com.luiz.springbatchexample.batch;

import com.luiz.springbatchexample.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

@Configuration
public class CapitalizeNamesJobConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(CapitalizeNamesJobConfig.class);

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        return jobBuilderFactory.get("capitalizedNamesJob")
                .start(capitalizeNamesStep(stepBuilderFactory))
                .next(deleteFilesStep(stepBuilderFactory))
                .build();
    }

    @Bean
    public Step capitalizeNamesStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("capitalizedNamesStep")
                .<Person, Person>chunk(10)
                .reader(multiItemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Step deleteFilesStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("deleteFileStep")
                .tasklet(fileDeletingTasklet())
                .build();
    }

    @Bean
    public MultiResourceItemReader<Person> multiItemReader() {
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = null;
        try {
            resources = patternResolver.getResources("file:target/test-inputs/*.csv");
        } catch (IOException e) {
            LOGGER.error("error reading file", e);
        }

        return new MultiResourceItemReaderBuilder<Person>()
                .name("multiPersonItemReader")
                .delegate(itemReader())
                .resources(resources)
                .setStrict(true)
                .build();
    }

    @Bean
    public FlatFileItemReader<Person> itemReader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .targetType(Person.class)
                .build();
    }

    @Bean
    public PersonItemProcessor itemProcessor() {
        return new PersonItemProcessor();
    }

    @Bean
    public FlatFileItemWriter<Person> itemWriter() {
        return new FlatFileItemWriterBuilder<Person>()
                .name("personItemWriter")
                .resource(new FileSystemResource("target/test-outputs/persons.txt"))
                .delimited().delimiter(", ")
                .names(new String[]{"firstName", "lastName"})
                .build();
    }

    @Bean
    public FileDeletingTasklet fileDeletingTasklet() {
        return new FileDeletingTasklet(new FileSystemResource("target/test-inputs"));
    }


}
