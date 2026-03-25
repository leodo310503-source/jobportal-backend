package com.leon.jobportal.service;

import com.leon.jobportal.exception.*;
import com.leon.jobportal.dto.EmployerDTO;
import com.leon.jobportal.entity.Employer;
import com.leon.jobportal.entity.User;
import com.leon.jobportal.repository.EmployerRepository;
import com.leon.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Employer createProfile(EmployerDTO dto) {
        User user = getCurrentUser();

        if (employerRepository.existsByUserId(user.getId())) {
            throw new ConflictException("Employer profile already exists");
        }

        Employer employer = new Employer();
        employer.setUser(user);
        employer.setCompanyName(dto.getCompanyName());
        employer.setCompanyAddress(dto.getCompanyAddress());
        employer.setCompanyDescription(dto.getCompanyDescription());
        employer.setWebsite(dto.getWebsite());

        return employerRepository.save(employer);
    }

    public Employer getProfile() {
        User user = getCurrentUser();

        return employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));
    }

    public Employer updateProfile(EmployerDTO dto) {
        Employer employer = getProfile();
        employer.setCompanyName(dto.getCompanyName());
        employer.setCompanyAddress(dto.getCompanyAddress());
        employer.setCompanyDescription(dto.getCompanyDescription());
        employer.setWebsite(dto.getWebsite());
        return employerRepository.save(employer);
    }
}