package org.pn.igest.service.impl;

import org.pn.igest.integration.IgestOrchestrator;
import org.pn.igest.integration.model.IgestDocType;
import org.pn.igest.integration.model.IgestInvoiceRequest;
import org.pn.igest.integration.util.IgestRequestFactory;
import org.pn.igest.service.FaturacaoService;
import org.springframework.stereotype.Service;

@Service
public class FaturacaoServiceImpl implements FaturacaoService {

	private final IgestOrchestrator igestOrchestrator;
    private final IgestRequestFactory requestFactory;        

	public FaturacaoServiceImpl(
			IgestOrchestrator igestOrchestrator,
			IgestRequestFactory requestFactory) {
		super();
		this.igestOrchestrator = igestOrchestrator;
		this.requestFactory = requestFactory;
	}

	@Override
	public void emitirFaturaExemplo(String nifCliente, String nomeCliente, String trackingId) {
		IgestInvoiceRequest request = requestFactory.createBaseInvoice(nifCliente, nomeCliente, IgestDocType.FT);
		
		/*
		 * criar linhas
		 * @Singular no DTO, a lista esta inicializada
		 */
		// criar as linhas (pode-se usar o .linha() multiplas vezes devido ao @Singular)
		var linha1 = requestFactory.createLinha("REF001", "Serviço de Alojamento", "120.00", 23.0);
        var linha2 = requestFactory.createLinha("REF002", "Taxa Turística", "2.00", 0.0);

        request.getDocumento().getLinhas().add(linha1);
        request.getDocumento().getLinhas().add(linha2);		
     
        // Orquestrador: injetar Hash SHA-1, converter para XML e enviar à ACIN
        igestOrchestrator.processarEnvio(request, "novo_documento", trackingId);
	}

}