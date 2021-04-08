package com.github.kuzznya.jb.message.service.sender;

import com.github.kuzznya.jb.message.config.EmailProperties;
import lombok.RequiredArgsConstructor;
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
        return recipient.getScheme().equals("mailto");
    }

    @Override
    public void send(String message, URI recipient) {
        if (!recipient.getScheme().equals("mailto"))
            throw new UnsupportedOperationException("Unknown scheme " + recipient.getScheme());

        String to = recipient.toString().substring(7); // remove 'mailto:'

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(emailProperties.getFrom());
        mail.setTo(to);
        mail.setSubject(emailProperties.getSubject());
        mail.setText(message);
        mailSender.send(mail);
    }
}
