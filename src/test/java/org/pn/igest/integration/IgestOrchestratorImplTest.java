package org.pn.igest.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pn.igest.config.IgestConfig;
import org.pn.igest.domain.model.IgestLog;
import org.pn.igest.domain.repository.IgestLogRepository;
import org.pn.igest.integration.util.IgestXmlUtil;

@ExtendWith(MockitoExtension.class)
class IgestOrchestratorImplTest {

    @Mock
    private IgestGateway gateway;

    @Mock
    private IgestLogRepository logRepository;

    @Mock
    private IgestConfig config;

    @Mock
    private IgestXmlUtil xmlUtil;

    @InjectMocks
    private IgestOrchestratorImpl orchestrator;

    @BeforeEach
    void setUp() {
        // evitar NullPointer em campos basicos
        lenient().when(config.getSecretKey()).thenReturn("minha-chave-secreta");
    }

    @Test
    void deveGravarLogComErroQuandoGatewayFalha() {
    	// excecao
        String erroEsperado = "Timeout no servidor ACIN";
        when(xmlUtil.convertToXml(any())).thenReturn("<xml>teste</xml>");
        when(gateway.enviar(anyString())).thenThrow(new RuntimeException(erroEsperado));
        
        orchestrator.processarEnvio(new Object(), "teste_metodo", "123456789");

        // persistencia em BD
        ArgumentCaptor<IgestLog> logCaptor = ArgumentCaptor.forClass(IgestLog.class);
        verify(logRepository).save(logCaptor.capture());

        IgestLog logGravado = logCaptor.getValue();
        
        assertFalse(logGravado.getSucesso(), "O log deveria indicar falha");
        assertEquals(erroEsperado, logGravado.getMensagemErro());
        assertNotNull(logGravado.getTempoProcessamentoMs());
        assertEquals("123456789", logGravado.getIdentificadorExterno());
    }
}