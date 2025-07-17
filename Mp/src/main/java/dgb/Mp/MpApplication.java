package dgb.Mp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(scanBasePackages = {"dgb.Mp", "dgb.Mp.validation", "dgb.Mp.Couriel", "dgb.Mp.config","dgb.Mp.Utils"})

public class MpApplication {

	public static void main(String[] args) {


		SpringApplication.run(MpApplication.class, args);
	}
	@Bean
	CommandLineRunner run(PasswordEncoder passwordEncoder) {
		return args -> {
			String hash = passwordEncoder.encode("admin1234");
			System.out.println("Hash for 'admin1234': " + hash);
		};
	}

}
