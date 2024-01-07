package com.chadmockitoracle.chadmockito.service.emailsender;

import com.chadmockitoracle.chadmockito.domain.EmailDetails;

public interface EmailService {
    String sendSimpleMail(EmailDetails details);
    String sendMailWithAttachment(EmailDetails details);
}
