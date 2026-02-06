package org.pn.igest.controller;

import java.util.Map;
import java.util.UUID;

import org.pn.igest.domain.repository.IgestLogRepository;
import org.pn.igest.service.FaturacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/faturacao")
public class FaturacaoController {
	
    private final FaturacaoService faturacaoService;
    private final IgestLogRepository logRepository;

    public FaturacaoController(FaturacaoService faturacaoService, IgestLogRepository logRepository) {
        this.faturacaoService = faturacaoService;
        this.logRepository = logRepository;
    }
    
    /**
     * Submete uma fatura para processamento assíncrono.
     * POST http://localhost:8080/api/v1/faturacao/emitir
     */
    @PostMapping("/emitir")
    public ResponseEntity<Map<String, String>> emitirFatura(
            @RequestParam(defaultValue = "999999990") String nif,
            @RequestParam(defaultValue = "Consumidor Final") String nome) {
        
        try {
            // 1. Gerar o identificador único (Tracking ID)
            String trackingId = UUID.randomUUID().toString();
            
            // 2. Disparar o serviço (que chama o Orquestrador @Async)
            faturacaoService.emitirFaturaExemplo(nif, nome, trackingId);
            
            // 3. Responder imediatamente com 202 Accepted
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "status", "PROCESSANDO",
                "trackingId", trackingId,
                "mensagem", "O pedido de faturação foi aceite e está a ser comunicado ao iGest.",
                "link_consulta", "/api/v1/faturacao/status/" + trackingId
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "Erro",
                "detalhe", e.getMessage()
            ));
        }
    }

    /**
     * Consulta o estado de uma fatura submetida.
     * GET http://localhost:8080/api/v1/faturacao/status/{trackingId}
     */
    @GetMapping("/status/{trackingId}")
    public ResponseEntity<?> consultarStatus(@PathVariable String trackingId) {
        return logRepository.findByIdentificadorExterno(trackingId)
            .map(log -> {
                if (Boolean.TRUE.equals(log.getSucesso())) {
                    return ResponseEntity.ok(Map.of(
                        "status", "CONCLUIDO",
                        "data", log.getDataHora(),
                        "metodo", log.getMetodo(),
                        "resposta_igest", "Documento emitido com sucesso."
                    ));
                } else {
                    return ResponseEntity.ok(Map.of(
                        "status", "ERRO",
                        "mensagem", log.getMensagemErro() != null ? log.getMensagemErro() : "Erro desconhecido na integração."
                    ));
                }
            })
            .orElse(ResponseEntity.ok(Map.of(
                "status", "PENDENTE",
                "mensagem", "A fatura ainda está na fila de processamento ou a ser comunicada."
            )));
    }
}