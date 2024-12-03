package com.Yasindu.TicketSystem.repository;

import com.Yasindu.TicketSystem.model.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfiguration, Long> {

}
