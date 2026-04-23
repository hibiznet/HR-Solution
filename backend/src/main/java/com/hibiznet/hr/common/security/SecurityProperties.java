package com.hibiznet.hr.common.security;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(List<String> allowedOrigins, boolean secureHeaders) {
    public SecurityProperties {
        allowedOrigins = allowedOrigins == null ? new ArrayList<>(List.of("http://localhost:5173")) : allowedOrigins;
    }
}
