package com.ats.job.service.impl;

import com.ats.job.model.Job;
import com.ats.job.repository.JobRepository;
import com.ats.job.service.JobService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository repository;

    public JobServiceImpl(JobRepository repository) {
        this.repository = repository;
    }

    @Override
    @CacheEvict(value = "jobs", key = "#result.id")
    public Job createJob(Job job) {
        return repository.save(job);
    }

    @Override
    @Cacheable(value = "jobs", key = "#id")
    public Job getJob(Long id) {
        return repository.findById(id).orElse(null);
    }
}
//review