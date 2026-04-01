package com.ats.job.controller;

import com.ats.job.model.Job;
import com.ats.job.service.JobService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")   
public class JobController {

    private final JobService service;

    public JobController(JobService service) {
        this.service = service;
    }

    @PostMapping
    public Job createJob(@RequestBody Job job) {
        return service.createJob(job);
    }

    @GetMapping("/{id}")
    public Job getJob(@PathVariable Long id) {
        return service.getJob(id);
    }
}