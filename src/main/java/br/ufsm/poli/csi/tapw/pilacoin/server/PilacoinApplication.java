package br.ufsm.poli.csi.tapw.pilacoin.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "br.ufsm.poli.csi.tapw.pilacoin")
@EntityScan("br.ufsm.poli.csi.tapw.pilacoin")
public class PilacoinApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PilacoinApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(PilacoinApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
	}

}
