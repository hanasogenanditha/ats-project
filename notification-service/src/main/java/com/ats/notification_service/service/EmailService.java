package com.ats.notification_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendScreeningResultEmail(String to, String name, double score) {

        System.out.println("STEP 3: Preparing email...");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("stylgirlie@gmail.com");
            message.setSubject("ATS Screening Result");

            String result = score >= 70 ? "PASSED " : "REJECTED";

            message.setText(
                "Hello " + name + ",\n\n" +
                "Your screening score is: " + score + "\n" +
                "Result: " + result + "\n\n" +
                "Best regards,\nATS Team"
            );

            mailSender.send(message);

            System.out.println("STEP 4: Email sent successfully to " + to);

        } catch (Exception e) {
            System.out.println("ERROR sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}