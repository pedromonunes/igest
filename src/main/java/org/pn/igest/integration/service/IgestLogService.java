package org.pn.igest.integration.service;

import org.pn.igest.domain.model.IgestLog;
import org.pn.igest.domain.repository.IgestLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IgestLogService {

	private final IgestLogRepository logRepository;

    public IgestLogService(IgestLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void guardarLogFinal(IgestLog logEntry) {
        // saveAndFlush garante que o Hibernate envie o comando para a BD no imediato
        logRepository.saveAndFlush(logEntry);
    }

}