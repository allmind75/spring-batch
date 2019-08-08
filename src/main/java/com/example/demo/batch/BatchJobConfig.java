package com.example.demo.batch;

import java.util.Map;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpStatusCodeException;

@Configuration
public class BatchJobConfig {

  //Spring Batch Job
  @Bean
  Job etl(JobBuilderFactory jbf, StepBuilderFactory sbf,
      Step1Configuration step1,
      Step2Configuration step2, Step3Configuration step3) throws Exception {

    //Step1Configuration : 각 스탭에 필요한 빈 정의


    //형식이 자유로은 Tasklet 콜백 사용
    Step setup = sbf.get("clean-contact-table").tasklet(step1.tasklet(null)).build();

    /*
    skip : 발생 가능한 실패에 대비하기 위해 작업의 무시 정책(무시를 위해 필요한 예외)
           재실행 정책(스탭에서 주어진 아이템을 얼마나 많이 재시도할 것인지에 대한 예외)을 정의
           데이터를 처리하는 동안 하나의 문제로 전체 작업이 취소되지 않도록 설정함
     */
    Step s2 = sbf.get("file-db").<Person, Person>chunk(1000)
        .faultTolerant()
        .skip(InvalidEmailException.class) //해당 예외 발생시 무시
        .retry(HttpStatusCodeException.class)//HttpSatusCodeException 예외 발생시 재시도
        .retryLimit(2).reader(step2.fileReader(null))
        .processor(step2.emailValidatingProcessor(null))
        .writer(step2.jdbcWriter(null))
        .build();

    //Step1에서 저장한 데이터에 질의를 실행하고 레코드 사용빈도를 계산해서 .csv 에 저장
    Step s3 = sbf.get("db-file")
        .<Map<Integer, Integer>, Map<Integer, Integer>chunk(100)
        .reader(step3.jdbcReader(null)).writer(step3.fileWriter(null)).build();

    //Spring Batch Job 파라미터화
    return jbf.get("etl").incrementer(new RunIdIncrementer())
        .start(setup)
        .next(s2).next(s3).build(); //JOB 빌드

  }

}
