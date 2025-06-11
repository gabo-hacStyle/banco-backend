package gabs.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "gabs.infrastructure",
        "gabs.application"
})
@EnableJpaRepositories(basePackages = "gabs.infrastructure.repository.springdata")
public class AppBancoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppBancoApplication.class, args);
    }

}
