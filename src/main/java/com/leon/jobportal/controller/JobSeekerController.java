package com.leon.jobportal.controller;

import com.leon.jobportal.dto.JobSeekerDTO;
import com.leon.jobportal.entity.JobSeeker;
import com.leon.jobportal.service.JobSeekerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobseeker")
@RequiredArgsConstructor
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;

    @PostMapping("/profile")
    public ResponseEntity<JobSeeker> createProfile(@RequestBody JobSeekerDTO dto) {
        return ResponseEntity.ok(jobSeekerService.createProfile(dto));
    }

    @GetMapping("/profile")
    public ResponseEntity<JobSeeker> getProfile() {
        return ResponseEntity.ok(jobSeekerService.getProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<JobSeeker> updateProfile(@RequestBody JobSeekerDTO dto) {
        return ResponseEntity.ok(jobSeekerService.updateProfile(dto));
    }
}
