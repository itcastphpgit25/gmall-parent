package com.atguigu.gmall.admin;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDubbo
@RestController
public class GmallAdminWebApplication {

	@GetMapping("/info")
	public String abTest(){
		return "ok";
	}

	public static void main(String[] args) {
		SpringApplication.run(GmallAdminWebApplication.class, args);
	}

}
