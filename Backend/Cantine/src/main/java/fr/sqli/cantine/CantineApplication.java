package fr.sqli.cantine;


import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
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
	                		new File("images/persons/admin"),
						    new File("images/persons/students"),
						    new File("images/orders")
					       ).forEach( (file) -> {
				   if  (!file.exists() || !file.isDirectory()){
					   CantineApplication.LOG.fatal(" \u001B[31m FATAL ERROR APPLICATION EXITED BECAUSE {} DOES NOT EXISTS \u001B[0m",file.getName());
					   SpringApplication.exit(SpringApplication.run(CantineApplication.class));
				   }
			});
		};
	}


	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder (){
		return  new BCryptPasswordEncoder();
	}

/*	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization" , "Content-Type"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}*/

}