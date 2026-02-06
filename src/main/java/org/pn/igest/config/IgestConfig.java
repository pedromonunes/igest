package org.pn.igest.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "igest") 				// requires: spring-boot-configuration-processor @ pom.xml
//@PropertySource("classpath:application.properties") 	// commented
@Data
public class IgestConfig {
	
    private String endpoint;  
    private String secretKey;
    private String integrationCode;
    private int entityId;
    private String entityNif;
    
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(5)) // Tempo para ligar ao servidor ACIN
                .readTimeout(Duration.ofSeconds(30))    // Tempo para esperar pelo XML da fatura
                .build();
        
        // or
        
        /*
        return builder
                .requestFactory(() -> {
                    var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout(5000); // Milissegundos
                    factory.setReadTimeout(30000);   // Milissegundos
                    return factory;
                })
                .build();
         */
    }    
	
}