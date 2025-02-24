package com.validata.deepdetect;

import com.validata.deepdetect.security.JwtConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtConfigProperties.class})
public class DeepDetectApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeepDetectApplication.class, args);
	}

}
