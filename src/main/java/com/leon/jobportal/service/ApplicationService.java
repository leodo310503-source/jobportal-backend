package com.leon.jobportal.service;

import com.leon.jobportal.exception.*;
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Application applyJob(Long jobId) {
        User user = getCurrentUser();

        JobSeeker jobSeeker = jobSeekerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Please create your profile first"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new BadRequestException("This job is closed");
        }

        if (applicationRepository.existsByJobIdAndJobSeekerId(jobId, jobSeeker.getId())) {
            throw new ConflictException("You have already applied for this job");
        }

        Application application = new Application();
        application.setJob(job);
        application.setJobSeeker(jobSeeker);
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    public List<Application> getMyApplications() {
        User user = getCurrentUser();

        JobSeeker jobSeeker = jobSeekerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Jobseeker profile not found"));

        return applicationRepository.findByJobSeekerId(jobSeeker.getId());
    }

    public List<Application> getApplicationsByJob(Long jobId) {
        User user = getCurrentUser();

        Employer employer = employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new ForbiddenException("You are not allowed to view this job's applications");
        }

        return applicationRepository.findByJobId(jobId);
    }

    public Application updateStatus(Long applicationId, String status) {
        User user = getCurrentUser();

        Employer employer = employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (!application.getJob().getEmployer().getId().equals(employer.getId())) {
            throw new ForbiddenException("You are not allowed to update this application");
        }

        try {
            application.setStatus(ApplicationStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status. Accepted values: PENDING, ACCEPTED, REJECTED");
        }

        return applicationRepository.save(application);
    }
}