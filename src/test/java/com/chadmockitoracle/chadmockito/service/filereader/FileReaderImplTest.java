package com.chadmockitoracle.chadmockito.service.filereader;

import com.chadmockitoracle.chadmockito.entity.HousingProduction;
import com.chadmockitoracle.chadmockito.repository.HousingRepository;
import com.chadmockitoracle.chadmockito.service.emailsender.EmailServiceImpl;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@SpringBootTest
class FileReaderImplTest {
    @InjectMocks
    public FileReaderImpl fileReader;

    @Mock
    private HousingRepository housingRepository;
    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender javaMailSender;

    private MimeMessage mimeMessage;

    @BeforeEach
    public void setUp(){
        HousingProduction housingProduction = new HousingProduction();

        fileReader = new FileReaderImpl(emailService,housingRepository);

        ReflectionTestUtils.setField(fileReader,"batchSize",50);

        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        when(housingRepository.saveAll(any())).thenReturn(List.of(housingProduction));
        emailService = new EmailServiceImpl(javaMailSender);
    }


    @Test
    void readFileFromDirectoryTest() {
        fileReader.readFileFromDirectory();
    }
}