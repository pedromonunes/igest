package org.pn.igest.service;

public interface FaturacaoService {
	
	/**
     * Emite uma fatura para um cliente específico.
     * @param nifCliente NIF do destinatário.
     * @param nomeCliente Nome completo do cliente.
     */
    void emitirFaturaExemplo(String nifCliente, String nomeCliente, String trackingId);
    
    // Futuramente :
    // void cancelarDocumento(String idDocumento);
    // byte[] descarregarPdf(String idDocumento);

}