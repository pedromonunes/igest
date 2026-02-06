package org.pn.igest.domain.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.pn.igest.domain.model.IgestLog;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface IgestLogRepository extends JpaRepository<IgestLog, Long> {
	// Garante que o nome depois do "findBy" coincide com o atributo da Entity
    Optional<IgestLog> findByIdentificadorExterno(String identificadorExterno);
}