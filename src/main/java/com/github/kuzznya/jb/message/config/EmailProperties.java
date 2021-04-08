package com.github.kuzznya.jb.message.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("email")
@Data
public class EmailProperties {
    private String from;
    private String subject = "<No subject>";
}
