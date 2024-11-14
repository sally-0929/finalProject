package com.treasuredigger.devel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Enable
@SpringBootApplication
@MapperScan("com.treasuredigger.devel.mapper")
public class DevelApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DevelApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(DevelApplication.class, args);
	}

}
