package com.springboot.ai_docs;

import com.springboot.ai_docs.configuration.HintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.shell.command.annotation.CommandScan;

@ImportRuntimeHints(HintsRegistrar.class)
@CommandScan
@SpringBootApplication
public class AiDocsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiDocsApplication.class, args);
	}

}
