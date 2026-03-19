package com.leon.jobportal.service;

import com.leon.jobportal.dto.JobSeekerDTO;
import com.leon.jobportal.entity.JobSeeker;
import com.leon.jobportal.entity.User;
import com.leon.jobportal.repository.JobSeekerRepository;
import com.leon.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobSeekerService {

    private final JobSeekerRepository jobSeekerRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public JobSeeker createProfile(JobSeekerDTO dto) {
        User user = getCurrentUser();

        if (jobSeekerRepository.existsByUserId(user.getId())) {
            throw new RuntimeException("Profile already exists");
        }

        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setUser(user);
        jobSeeker.setFullName(dto.getFullName());
        jobSeeker.setPhone(dto.getPhone());
        jobSeeker.setSkills(dto.getSkills());
        jobSeeker.setExperience(dto.getExperience());
        jobSeeker.setEducation(dto.getEducation());

        return jobSeekerRepository.save(jobSeeker);
    }

    public JobSeeker getProfile() {
        User user = getCurrentUser();
        return jobSeekerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public JobSeeker updateProfile(JobSeekerDTO dto) {
        JobSeeker jobSeeker = getProfile();
        jobSeeker.setFullName(dto.getFullName());
        jobSeeker.setPhone(dto.getPhone());
        jobSeeker.setSkills(dto.getSkills());
        jobSeeker.setExperience(dto.getExperience());
        jobSeeker.setEducation(dto.getEducation());
        return jobSeekerRepository.save(jobSeeker);
    }
}