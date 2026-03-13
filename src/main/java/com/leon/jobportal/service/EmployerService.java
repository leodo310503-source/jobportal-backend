package com.leon.jobportal.service;

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

    public Employer createProfile(EmployerDTO dto) {
        // Lấy email từ token hiện tại
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (employerRepository.existsByUserId(user.getId())) {
            throw new RuntimeException("Profile already exists");
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
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }
}