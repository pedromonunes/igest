package org.pn.igest.integration.util;

import org.pn.igest.config.IgestConfig;
import org.pn.igest.integration.model.IgestDocType;
import org.pn.igest.integration.model.IgestInvoiceRequest;
import org.pn.igest.integration.model.IgestInvoiceRequest.Documento;
import org.pn.igest.integration.model.IgestInvoiceRequest.Linha;
import org.pn.igest.util.DateUtil;
import org.springframework.stereotype.Component;

@Component
public class IgestRequestFactory {
	
	private final IgestConfig config;

    public IgestRequestFactory(IgestConfig config) {
        this.config = config;
    }

    /*
     * NOTA
     * 
     * Se amanhã precisares de criar uma Fatura-Recibo (FR),
     * basta mudares o parâmetro na Factory:
     * 	var request = requestFactory.createBaseInvoice(nif, nome, "FR");
     * 
     */
    
    /**
     * esqueleto de fatura com os dados fixos da empresa já preenchidos.
     */
    public IgestInvoiceRequest createBaseInvoice(String nifCliente, String nomeCliente, IgestDocType tipoDoc) {
        return IgestInvoiceRequest.builder()
                .idEntidade(String.valueOf(config.getEntityId())) // Vem do application.properties
                .documento(Documento.builder()
                        .tipoDocumento(tipoDoc.name())
                        .nifCliente(nifCliente)
                        .nomeCliente(nomeCliente)
                        .dataDocumento(DateUtil.getTodayIso())
                        .build())
                .build();
    }
    
    
    // metodo utilitario para criar linhas com logica de isencao automatica
    public Linha createLinha(String ref, String desc, String preco, double taxa) {
        var linhaBuilder = Linha.builder()
                .referencia(ref)
                .descricao(desc)
                .quantidade("1")
                .preco(preco)
                .taxa(String.valueOf((int) taxa));

        // Regra de Ouro: Isenção em Portugal
        if (taxa == 0) {
            linhaBuilder.codigoIsencao("M01") // Podes tornar isto dinâmico
                        .motivoIsencao("Isento ao abrigo do Artigo 16.º do CIVA");
        }

        return linhaBuilder.build();
    }
    
}