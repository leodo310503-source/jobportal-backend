package com.leon.jobportal.repository;

import com.leon.jobportal.entity.Job;
import com.leon.jobportal.entity.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByEmployerId(Long employerId);

    @Query(value = "SELECT * FROM jobs j WHERE " +
            "(:keyword IS NULL OR j.title ILIKE CONCAT('%', :keyword, '%') " +
            "OR j.description ILIKE CONCAT('%', :keyword, '%')) " +
            "AND (:location IS NULL OR j.location ILIKE CONCAT('%', :location, '%')) " +
            "AND (:salary IS NULL OR j.salary ILIKE CONCAT('%', :salary, '%')) " +
            "AND j.status = 'OPEN' " +
            "ORDER BY j.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM jobs j WHERE " +
                    "(:keyword IS NULL OR j.title ILIKE CONCAT('%', :keyword, '%') " +
                    "OR j.description ILIKE CONCAT('%', :keyword, '%')) " +
                    "AND (:location IS NULL OR j.location ILIKE CONCAT('%', :location, '%')) " +
                    "AND (:salary IS NULL OR j.salary ILIKE CONCAT('%', :salary, '%')) " +
                    "AND j.status = 'OPEN'",
            nativeQuery = true)
    Page<Job> searchJobs(@Param("keyword") String keyword,
                         @Param("location") String location,
                         @Param("salary") String salary,
                         Pageable pageable);
    Page<Job> findByStatus(JobStatus status, Pageable pageable);
}