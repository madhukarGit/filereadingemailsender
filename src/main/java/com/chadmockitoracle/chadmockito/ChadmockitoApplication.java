package com.chadmockitoracle.chadmockito;

import com.chadmockitoracle.chadmockito.service.emailsender.EmailService;
import com.chadmockitoracle.chadmockito.service.filereader.FileReaderImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChadmockitoApplication implements CommandLineRunner {
	private final FileReaderImpl fileReader;

	private final EmailService emailService;

	public ChadmockitoApplication(FileReaderImpl fileReader, EmailService emailService) {
		this.fileReader = fileReader;
		this.emailService = emailService;
	}

	public static void main(String[] args) {
		SpringApplication.run(ChadmockitoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		fileReader.readFileFromDirectory();
	}
}
