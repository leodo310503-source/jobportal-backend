package com.leon.jobportal.controller;

import com.leon.jobportal.dto.EmployerDTO;
import com.leon.jobportal.entity.Employer;
import com.leon.jobportal.service.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employer")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping("/profile")
    public ResponseEntity<Employer> createProfile(@RequestBody EmployerDTO dto) {
        return ResponseEntity.ok(employerService.createProfile(dto));
    }

    @GetMapping("/profile")
    public ResponseEntity<Employer> getProfile() {
        return ResponseEntity.ok(employerService.getProfile());
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Employer> updateProfile(@RequestBody EmployerDTO dto) {
        return ResponseEntity.ok(employerService.updateProfile(dto));
    }
}
