package org.pn.igest.integration;

public interface IgestOrchestrator {
    
    /**
     * Processa e regista o envio de um documento para o iGest.
     * @param requestDto Objeto JAXB com os dados.
     * @param metodo Nome da operação (ex: novo_documento).
     * @param identificador Identificador para auditoria (ex: NIF).
     */
    void processarEnvio(Object requestDto, String metodo, String identificador);
    
}