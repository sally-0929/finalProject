package com.treasuredigger.devel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Enable
@SpringBootApplication
@MapperScan("com.treasuredigger.devel.mapper")
public class DevelApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevelApplication.class, args);
	}

}
