package com.ats.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningResultEvent {
    private String name;
    private double score;
    private Long applicationId;
    private String candidateEmail;
}