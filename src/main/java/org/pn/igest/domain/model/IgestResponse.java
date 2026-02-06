package org.pn.igest.domain.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "output")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class IgestResponse {
	
	private String status;			// Ex: "sucesso" ou "erro"         			

	@XmlElement(name = "mensagem_erro")
	private String mensagemErro;

	@XmlElement(name = "id_documento")
	private String idDocumento;

	@XmlElement(name = "numero_documento")
	private String numeroDocumento; // Ex: FT 2026/1 		

	@XmlElement(name = "url_pdf")
	private String urlPdf;

	@XmlElement(name = "hash_at")
	private String hashAt;         // hash de certificaçao da AT

}