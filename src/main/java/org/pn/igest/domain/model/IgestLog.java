package org.pn.igest.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "log_integracao_igest"/*, schema = "sniturh"*/)
@Data
public class IgestLog {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora")
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(length = 50, nullable = false)
    private String metodo;

    @Column(name = "identificador_externo", length = 100)
    private String identificadorExterno;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "tempo_processamento_ms")
    private Long tempoProcessamentoMs;

    private Boolean sucesso = false;

    @Column(name = "mensagem_erro", columnDefinition = "TEXT")
    private String mensagemErro;

    private Integer tentativas = 0;
    
}