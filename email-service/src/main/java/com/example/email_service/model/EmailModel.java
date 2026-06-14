package com.example.email_service.model;

import com.example.email_service.enums.StatusEmail;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "emails")
public class EmailModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailId;
    
    private Long userId;
    private String emailFrom;
    private String emailTo;
    private String subject;
    
    @Column(columnDefinition = "TEXT")
    private String text;
    
    private LocalDateTime sendDateEmail;
    
    @Enumerated(EnumType.STRING)
    private StatusEmail status;
}