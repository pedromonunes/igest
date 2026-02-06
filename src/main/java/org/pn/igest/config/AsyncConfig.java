package org.pn.igest.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "igestTaskExecutor")
    Executor igestTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Configurações para o volume de faturas
        executor.setCorePoolSize(5);   	// Mantém 5 threads sempre ativas
        executor.setMaxPoolSize(10);  	// Sobe até 10 se a fila encher
        executor.setQueueCapacity(100); // Fila de espera para pedidos de faturação
        executor.setThreadNamePrefix("iGestAsync-");
        
        // Garante que as threads terminam as tarefas antes de o servidor desligar
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        return executor;
    }
}