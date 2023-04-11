package fr.sqli.Cantine;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

@SpringBootApplication (exclude = SecurityAutoConfiguration.class)
public class CantineApplication {

	private static final Logger LOG = LogManager.getLogger();




	public static void main(String[] args) {
		SpringApplication.run(CantineApplication.class, args);
	}

	/**
	 * @doc The method should check for the existence of the 'images/users',
	 * 'images/meals', and 'images/menus' directories. If any of these directories
	 * do not exist, the application will stop and not start.
	 * @return ApplicationRunner
	 * @exit If any of the directories ('images/users', 'images/meals', or 'images/menus') do not exist
	 */

	@Bean
	public ApplicationRunner checkExistingImagesDirectories() {
		return args -> {
			Stream.of(
					new File("images/meals"),
		         			new File("images/menus"),
	                		new File("images/users")
					       ).forEach( (file) -> {
				   if  (!file.exists() || !file.isDirectory()){
					   CantineApplication.LOG.fatal(" \u001B[31m FATAL ERROR APPLICATION EXITED BECAUSE {} DOES NOT EXISTS \u001B[0m",file.getName());
					   SpringApplication.exit(SpringApplication.run(CantineApplication.class));
				   }
			});
		};
	}

}