package com.leon.jobportal.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobDTO {
    private String title;
    private String description;
    private String location;
    private String salary;
    private LocalDateTime deadline;
}