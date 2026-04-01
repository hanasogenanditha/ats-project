package com.ats.application_service.repository;

import com.ats.application_service.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobId(Long jobId);
    List<Application> findByCandidateEmail(String candidateEmail);
    List<Application> findByStatus(Application.ApplicationStatus status);
}
