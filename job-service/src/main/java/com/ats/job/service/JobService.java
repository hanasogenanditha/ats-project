package com.ats.job.service;

import com.ats.job.model.Job;

public interface JobService {
    Job createJob(Job job);
    Job getJob(Long id);
}