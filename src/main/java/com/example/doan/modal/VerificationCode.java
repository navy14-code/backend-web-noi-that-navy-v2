package com.example.doan.modal;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;

    private String otp;

    private String email;

    private boolean isUsed = false;

    private boolean forPasswordReset = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne
    private User user;
}
