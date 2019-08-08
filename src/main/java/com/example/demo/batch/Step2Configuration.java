package com.example.demo.batch;

import javax.sql.DataSource;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class Step2Configuration {

  FlatFileItemReader<Person> fileReader(@Value("file://#{jobParameters['input']}") Resource in) throws Exception {
    return new FlatFileItemReader<Person>().name("file-reader")
        .resource(in).targetType(Persion.class).delimited().delimiter(",")
        .names(new String[] { "firstName", "age", "email"}).build();

  }

  @Bean
  ItemProcessor<Persion, Person> emailValidatingProcessor(EmailValidationService emailValidator) {
    return item-> {
      String email = item.getEmail();
      if(!emailValidator.isEmailValid(email)) {
        throw new InvalidEmailException(email);
      }
      return item;
    };
  }

  @Bean
  JdbcBatchItemWriter<Person> jdbcWriter(DataSource ds) {
    return new JdbcBatchItemWriterBuilder<Person>()
        .dataSource(ds)
        .sql(
            "").beanMapped().build();
  }


}
