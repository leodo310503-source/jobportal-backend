package com.leon.jobportal.repository;

import com.leon.jobportal.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobSeekerId(Long jobSeekerId);
    List<Application> findByJobId(Long jobId);
    boolean existsByJobIdAndJobSeekerId(Long jobId, Long jobSeekerId);
}