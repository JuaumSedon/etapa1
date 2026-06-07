package com.exemplo.secrest.dto;

public record EmailDto(
        Long userId,
        String emailTo,
        String subject,
        String text
) {
}