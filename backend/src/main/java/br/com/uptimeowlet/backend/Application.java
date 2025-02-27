package br.com.uptimeowlet.backend;

import br.com.uptimeowlet.backend.listeners.ApplicationCloseEventListener;
import br.com.uptimeowlet.backend.listeners.ApplicationReadyEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.addListeners(new ApplicationReadyEventListener(), new ApplicationCloseEventListener());
		app.run(args);
	}

}
