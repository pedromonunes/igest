package org.pn.igest.integration;
import org.pn.igest.config.IgestConfig;
import org.pn.igest.domain.model.IgestLog;
import org.pn.igest.domain.model.IgestResponse;
import org.pn.igest.domain.repository.IgestLogRepository;
import org.pn.igest.integration.model.IgestInvoiceRequest;
import org.pn.igest.integration.service.IgestLogService;
import org.pn.igest.integration.util.IgestXmlUtil;
import org.pn.igest.util.DateUtil;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IgestOrchestratorImpl  implements IgestOrchestrator{
	
	private final IgestGateway gateway;
    private final IgestLogRepository logRepository;
    private final IgestConfig config;
    private final IgestXmlUtil xmlUtil;
    private final IgestLogService igestLogService;

    public IgestOrchestratorImpl(
    		IgestGateway gateway,
    		IgestLogRepository logRepository, 
            IgestConfig config,
            IgestXmlUtil xmlUtil,
            IgestLogService igestLogService) {
        this.gateway = gateway;
        this.logRepository = logRepository;
        this.config = config;
        this.xmlUtil = xmlUtil;
        this.igestLogService = igestLogService;
    }

    @Override
    //@Async("igestTaskExecutor")
    @Transactional
    @Retryable(
    	    retryFor = { RuntimeException.class }, 
    	    maxAttempts = 3, 
    	    backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void processarEnvio(Object requestDto, String metodo, String identificador) {
    	/* Se falhar, o Spring espera 2s e tenta de novo
    	 * Na 2ª falha espera 4s (multiplier 2)
    	 * Na 3ª tentativa, se falhar, chama o método @Recover.
    	 */
    	
    	final IgestLog logEntry = logRepository
		    						.findByIdentificadorExterno(identificador)
					                .orElseGet(() -> {
					                    IgestLog newLog = new IgestLog();
					                    newLog.setIdentificadorExterno(identificador);
					                    newLog.setTentativas(0);
					                    newLog.setMetodo(metodo);
					                    return newLog;
					                });
    	
    	logEntry.setTentativas(logEntry.getTentativas() + 1);    	
    	
        long startTime = System.currentTimeMillis();

        try {
            // Credenciais
            String dataHora = DateUtil.nowTimestamp();
            String hash = xmlUtil.gerarHash(config.getSecretKey(), dataHora);
            
            if (requestDto instanceof IgestInvoiceRequest request) {
                request.getAutenticacao().setChave(hash);
                request.getAutenticacao().setData(dataHora);
                request.getAutenticacao().setNif(config.getEntityNif());
                request.getAutenticacao().setCodigo(config.getIntegrationCode());
            }

            // converter e enviar
            String xmlRequest = xmlUtil.convertToXml(requestDto);
            logEntry.setRequestBody(xmlRequest);

            // Se o gateway lançar RuntimeException aqui, o Spring fará o Retry
            String xmlResponse = gateway.enviar(xmlRequest);
            
            logEntry.setResponseBody(xmlResponse);
            logEntry.setHttpStatus(200);

            // reposta
            IgestResponse responseObj = xmlUtil.convertFromXml(xmlResponse);
            
            if ("sucesso".equalsIgnoreCase(responseObj.getStatus())) {
                logEntry.setSucesso(true);
                logEntry.setMensagemErro(null); // limpar erros de tentativas anteriores
            } else {
                logEntry.setSucesso(false);
                logEntry.setMensagemErro(responseObj.getMensagemErro());
            }

        } catch (Exception e) {
            logEntry.setSucesso(false);
            logEntry.setMensagemErro("Tentativa " + logEntry.getTentativas() + " de integracao com a iGest falhou: " + e.getMessage());
            
            // guardar no log antes de lancar excecao, pois a persistencia pode nao ser efetuada devido a rollback do retry
            logEntry.setTempoProcessamentoMs(System.currentTimeMillis() - startTime);
            igestLogService.guardarLogFinal(logEntry);
            
            // ativar @Retryable
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException(e);            
            
        } finally {
            logEntry.setTempoProcessamentoMs(System.currentTimeMillis() - startTime);
            // forcar nova transacao devido a rollback existirem em Retry
            igestLogService.guardarLogFinal(logEntry);
        }
    }
    
    /**
     * metodo chamado automaticamente apos 3 tentativas falharem
     */
    @Recover
    public void recover(RuntimeException e, Object requestDto, String metodo, String identificador) {
        // 1. Atualizar o estado final na Base de Dados para auditabilidade
        logRepository.findByIdentificadorExterno(identificador).ifPresent(logEntry -> {
            logEntry.setSucesso(false);
            logEntry.setHttpStatus(500); // Ou o código de erro relevante
            logEntry.setMensagemErro("FALHA DEFINITIVA: Sistema esgotou as 3 tentativas. Erro: " + e.getMessage());
            // Usamos o serviço de log para garantir a escrita fora da transação que falhou
            igestLogService.guardarLogFinal(logEntry);
        });

        // 2. Teu mecanismo de segurança em disco (Ficheiro .txt)
        String fileName = "igest_fatal_error_" + identificador + "_" + System.currentTimeMillis() + ".txt";
        StringBuilder sb = new StringBuilder();
        sb.append("--- FALHA CRÍTICA IGEST (RECUPERAÇÃO) ---\n");
        sb.append("Data/Hora: ").append(DateUtil.nowTimestamp()).append("\n");
        sb.append("Identificador: ").append(identificador).append("\n");
        sb.append("Erro Final: ").append(e.getMessage()).append("\n");
        
        try {
            String xmlFallido = xmlUtil.convertToXml(requestDto);
            sb.append("Conteúdo XML que falhou:\n").append(xmlFallido).append("\n");
        } catch (Exception ex) {
            sb.append("Erro ao converter XML para dump: ").append(ex.getMessage());
        }

        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(fileName), sb.toString().getBytes());
            log.error(">>> MECANISMO DE RECOVERY: Tentativas esgotadas para {}. Backup criado em: {}", identificador, fileName);
        } catch (java.io.IOException ioException) {
            log.error(">>> ERRO CRÍTICO: Falha ao gravar backup físico!", ioException);
        }
    }

}