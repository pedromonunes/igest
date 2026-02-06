package org.pn.igest.integration;

import java.nio.charset.StandardCharsets;

import org.pn.igest.config.IgestConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IgestGateway {
	
	private final RestTemplate restTemplate;
    private final IgestConfig config;
    
    public IgestGateway(RestTemplate restTemplate, IgestConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    /**
     * Envia o XML para o endpoint do iGest definido nas propriedades.
     * @param xmlPayload Conteúdo XML já montado e assinado.
     * @return String Resposta raw (XML) do webservice.
     */
    public String enviar(String xmlPayload) {        
        HttpHeaders headers = new HttpHeaders();
        
        /* * Importante: O iGest tipicamente espera ISO-8859-1. 
         * Definimos o Content-Type explicitamente.
         */
        headers.setContentType(new MediaType("text", "xml", StandardCharsets.ISO_8859_1));
        
        HttpEntity<String> request = new HttpEntity<>(xmlPayload, headers);        
        
        // RestTemplate utiliza os timeouts (5s/30s) definidos em IgestConfig        
        return restTemplate.postForObject(config.getEndpoint(), request, String.class);
    }

}