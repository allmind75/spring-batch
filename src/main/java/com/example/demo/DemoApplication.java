package com.example.demo;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);

	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	JdbcTemplate jdbcTemplate(Datasource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	CommandLineRunner run(JobLauncher launcher, Job job, @Value("${user.home}") String home) {
		return args -> launcher.run(job,
				new JobParametersBuilder().addString("input", path(home, "in.csv"))
		)
	}



}
