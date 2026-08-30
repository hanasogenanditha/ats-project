package com.ats.screening.service;

import com.ats.screening.dto.ApplicationEvent;

public interface ScreeningService {
    void processApplication(ApplicationEvent event);
}
//review