package com.leon.jobportal.service;

import com.leon.jobportal.entity.*;
import com.leon.jobportal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // JobSeeker apply job
    public Application applyJob(Long jobId) {
        User user = getCurrentUser();

        JobSeeker jobSeeker = jobSeekerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Please create your profile first"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new RuntimeException("This job is closed");
        }

        if (applicationRepository.existsByJobIdAndJobSeekerId(jobId, jobSeeker.getId())) {
            throw new RuntimeException("You already applied for this job");
        }

        Application application = new Application();
        application.setJob(job);
        application.setJobSeeker(jobSeeker);
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    // JobSeeker xem lịch sử apply
    public List<Application> getMyApplications() {
        User user = getCurrentUser();

        JobSeeker jobSeeker = jobSeekerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return applicationRepository.findByJobSeekerId(jobSeeker.getId());
    }

    // Employer xem danh sách ứng viên của job
    public List<Application> getApplicationsByJob(Long jobId) {
        User user = getCurrentUser();

        Employer employer = employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Employer profile not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("You are not allowed to view this job's applications");
        }

        return applicationRepository.findByJobId(jobId);
    }

    // Employer cập nhật trạng thái ứng viên
    public Application updateStatus(Long applicationId, String status) {
        User user = getCurrentUser();

        Employer employer = employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Employer profile not found"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getJob().getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("You are not allowed to update this application");
        }

        application.setStatus(ApplicationStatus.valueOf(status));
        return applicationRepository.save(application);
    }
}