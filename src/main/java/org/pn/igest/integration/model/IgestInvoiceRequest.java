package org.pn.igest.integration.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "input")
@XmlAccessorType(XmlAccessType.FIELD)
public class IgestInvoiceRequest {

    @XmlElement(name = "id_entidade")
    private String idEntidade;
    
    @XmlElement(name = "metodo")
    private String metodo;

    @Builder.Default
    private Autenticacao autenticacao = new Autenticacao();
    
    @Builder.Default
    private Documento documento = new Documento();

 // --- INNER CLASS: Autenticacao ---
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Autenticacao {
        private String nif;
        private String codigo;
        private String chave;
        private String data;
    }

    // --- INNER CLASS: Documento ---
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Documento {
        @XmlElement(name = "tipo_documento")
        private String tipoDocumento;
        
        @XmlElement(name = "nif_cliente")
        private String nifCliente;

        @XmlElement(name = "nome_cliente")
        private String nomeCliente;

        @XmlElement(name = "data_documento")
        private String dataDocumento;

        @Builder.Default
        @XmlElementWrapper(name = "linhas")
        @XmlElement(name = "linha")
        private List<Linha> linhas = new ArrayList<>();
       
    }

    // --- INNER CLASS: Linha ---
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Linha {
        private String referencia;
        private String descricao;
        private String quantidade;
        private String preco;
        private String taxa;

        @XmlElement(name = "codigo_isencao")
        private String codigoIsencao;
        
        @XmlElement(name = "motivo_isencao")
        private String motivoIsencao;
    }
}