package com.ats.application_service.service;

import com.ats.application_service.model.Application;
import com.ats.application_service.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository repository;

    public Application saveApplication(Application application) {
        return repository.save(application);
    }

    public Application getApplication(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Application updateApplicationStatus(Long id, Application.ApplicationStatus status) {
        Application app = repository.findById(id).orElse(null);
        if (app != null) {
            app.setStatus(status);
            return repository.save(app);
        }
        return null;
    }

    public Application updateScreeningScore(Long id, Double score) {
        Application app = repository.findById(id).orElse(null);
        if (app != null) {
            app.setScreeningScore(score);
            app.setStatus(Application.ApplicationStatus.SCREENING_COMPLETED);
            return repository.save(app);
        }
        return null;
    }
}
