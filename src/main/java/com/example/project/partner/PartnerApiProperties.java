package com.example.project.partner;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "partner.api")
public class PartnerApiProperties {

    private String key = "dev-partner-key";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
