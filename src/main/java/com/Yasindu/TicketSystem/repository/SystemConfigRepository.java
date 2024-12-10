package com.Yasindu.TicketSystem.repository;

import com.Yasindu.TicketSystem.model.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing entities
 * This interface provides CRUD operations methods for the SystemConfiguration Entity
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfiguration, Long> {

}
