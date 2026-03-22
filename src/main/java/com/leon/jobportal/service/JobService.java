package com.leon.jobportal.service;

import com.leon.jobportal.dto.JobDTO;
import com.leon.jobportal.entity.Employer;
import com.leon.jobportal.entity.Job;
import com.leon.jobportal.entity.JobStatus;
import com.leon.jobportal.entity.User;
import com.leon.jobportal.repository.EmployerRepository;
import com.leon.jobportal.repository.JobRepository;
import com.leon.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;
    private final UserRepository userRepository;

    private Employer getCurrentEmployer() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Employer profile not found"));
    }

    public Job createJob(JobDTO dto) {
        Employer employer = getCurrentEmployer();

        Job job = new Job();
        job.setEmployer(employer);
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setSalary(dto.getSalary());
        job.setDeadline(dto.getDeadline());
        job.setStatus(JobStatus.OPEN);
        job.setCreatedAt(LocalDateTime.now());

        return jobRepository.save(job);
    }

    public Page<Job> getAllJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jobRepository.findAll(pageable);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public Job updateJob(Long id, JobDTO dto) {
        Job job = getJobById(id);
        Employer employer = getCurrentEmployer();

        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("You are not allowed to update this job");
        }

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setSalary(dto.getSalary());
        job.setDeadline(dto.getDeadline());

        return jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        Job job = getJobById(id);
        Employer employer = getCurrentEmployer();

        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("You are not allowed to delete this job");
        }

        jobRepository.delete(job);
    }

    public Page<Job> searchJobs(String keyword, String location,
                                String salary, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jobRepository.searchJobs(keyword, location, salary, pageable);
    }
}