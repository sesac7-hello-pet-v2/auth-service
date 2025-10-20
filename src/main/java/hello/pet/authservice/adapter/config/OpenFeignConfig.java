package hello.pet.authservice.adapter.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "hello.pet.authservice.adapter.out")
public class OpenFeignConfig {
}
