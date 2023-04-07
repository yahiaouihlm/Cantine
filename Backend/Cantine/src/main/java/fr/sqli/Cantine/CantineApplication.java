package fr.sqli.Cantine;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication (exclude = SecurityAutoConfiguration.class)
public class CantineApplication {

	public static void main(String[] args) {

		SpringApplication.run(CantineApplication.class, args);

	}

}
