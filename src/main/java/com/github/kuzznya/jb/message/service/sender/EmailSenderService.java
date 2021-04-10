package com.github.kuzznya.jb.message.service.sender;

import com.github.kuzznya.jb.message.config.EmailProperties;
import com.github.kuzznya.jb.message.exception.MessageSendException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class EmailSenderService implements SenderService {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    @Override
    public boolean canSend(URI recipient) {
        return recipient.getScheme().equals("mailto") &&
                isEmailValid(recipient.toString().substring(7));
    }

    @Override
    public void send(String message, URI recipient) {
        if (!canSend(recipient))
            throw new MessageSendException("Unsupported recipient " + recipient);

        String to = recipient.toString().substring(7); // remove 'mailto:'

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(emailProperties.getFrom());
        mail.setTo(to);
        mail.setSubject(emailProperties.getSubject());
        mail.setText(message);
        mailSender.send(mail);
    }

    private boolean isEmailValid(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}
