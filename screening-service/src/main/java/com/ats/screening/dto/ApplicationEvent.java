package com.ats.screening.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationEvent {

    @JsonProperty("candidateName")
    private String name;

    private String resumeText;
    private String candidateEmail;
    private Long jobId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }

    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
}
//review