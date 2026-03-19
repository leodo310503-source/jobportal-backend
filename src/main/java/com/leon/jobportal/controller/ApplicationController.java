package com.leon.jobportal.controller;

import com.leon.jobportal.entity.Application;
import com.leon.jobportal.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/{jobId}")
    public ResponseEntity<Application> applyJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.applyJob(jobId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Application>> getMyApplications() {
        return ResponseEntity.ok(applicationService.getMyApplications());
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getApplicationsByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Application> updateStatus(@PathVariable Long id,
                                                    @RequestParam String status) {
        return ResponseEntity.ok(applicationService.updateStatus(id, status));
    }
}
