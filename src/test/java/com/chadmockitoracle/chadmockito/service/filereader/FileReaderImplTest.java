package com.chadmockitoracle.chadmockito.service.filereader;

import com.chadmockitoracle.chadmockito.repository.HousingRepository;
import com.chadmockitoracle.chadmockito.service.emailsender.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileReaderImplTest {
    @InjectMocks
    public FileReaderImpl fileReader;

    @Mock
    private HousingRepository housingRepository;
    @Mock
    private EmailServiceImpl emailService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(fileReader, "batchSize", 50);
        when(housingRepository.saveAll(any())).thenReturn(any());
        when(emailService.sendMailWithAttachment(any())).thenReturn(anyString());
        //fileReader = new FileReaderImpl(emailService, housingRepository);
    }


    @Test
    void readFileFromDirectoryTest() {
        fileReader.readFileFromDirectory();
    }
}