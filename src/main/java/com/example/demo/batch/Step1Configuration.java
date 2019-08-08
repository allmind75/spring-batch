package com.example.demo.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class Step1Configuration {

  //Tasklet은 일반 콜백
  @Bean
  Tasklet tasklet(JdbcTemplate jdbcTemplate) {
    Log log = LogFactory.getLog(getClass());

    return (contribution, chunkContext) -> {
      log.info("starting the ETL JOB");
      jdbcTemplate.update("select * from product where rownum < 10");
      return RepeatStatus.FINISHED;
    };

  }
}
