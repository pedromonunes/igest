CREATE TABLE log_integracao_igest (
    id BIGSERIAL PRIMARY KEY,
    data_hora TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    metodo VARCHAR(50) NOT NULL,
    identificador_externo VARCHAR(100),
    request_body TEXT,
    response_body TEXT,
    http_status INTEGER,
    tempo_processamento_ms BIGINT, -- Corrigido de LONG para BIGINT
    sucesso BOOLEAN DEFAULT FALSE,
    mensagem_erro TEXT,
    tentativas INTEGER DEFAULT 1
);

-- Índices para performance
CREATE INDEX idx_igest_log_data ON log_integracao_igest (data_hora);
CREATE INDEX idx_igest_log_identificador ON log_integracao_igest (identificador_externo);