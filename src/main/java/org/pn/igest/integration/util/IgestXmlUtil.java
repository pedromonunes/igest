package org.pn.igest.integration.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.Formatter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.pn.igest.domain.model.IgestResponse;
import org.pn.igest.integration.model.IgestInvoiceRequest;
import org.springframework.stereotype.Component;

@Component
public class IgestXmlUtil {
	
private final JAXBContext context;
	
    public IgestXmlUtil() {
        try {
        	/*
        	 * O contexto tem de conhecer a classe principal
        	 * O JAXB encarrega-se de descobrir as Inner Classes (Documento, Linha) automaticamente
        	 */
            this.context = JAXBContext.newInstance(IgestResponse.class, IgestInvoiceRequest.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Falha ao inicializar contexto JAXB", e);
        }
    }

    public String gerarHash(String secretKey, String dataFormatada) {
        try {
            String input = secretKey + dataFormatada;
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(input.getBytes("UTF-8"));
            return byteToHex(crypt.digest());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular assinatura iGest", e);
        }
    }
    
    public String convertToXml(Object dto) {
        try {
            // USAR O CONTEXTO DA CLASSE (this.context)
            Marshaller marshaller = this.context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");

            StringWriter sw = new StringWriter();
            marshaller.marshal(dto, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("Erro no Marshalling XML", e);
        }
    }
    
    public IgestResponse convertFromXml(String xml) {
        if (xml == null || xml.isBlank()) {
            throw new RuntimeException("XML de resposta vazio");
        }
        try {
            // USAR O CONTEXTO DA CLASSE (this.context)
            Unmarshaller unmarshaller = this.context.createUnmarshaller();
            try (StringReader reader = new StringReader(xml)) {
                return (IgestResponse) unmarshaller.unmarshal(reader);
            }
        } catch (JAXBException e) {
            System.err.println("Erro ao converter XML: " + xml);
            throw new RuntimeException("Erro ao ler resposta XML do iGest", e);
        }
    }
    
    private String byteToHex(final byte[] hash) {
        try (Formatter formatter = new Formatter()) {
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }

}